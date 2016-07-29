package com.dqhuynh.gplace.parser;

import android.net.http.AndroidHttpClient;

import com.dqhuynh.gplace.api.APIConstant;
import com.dqhuynh.gplace.common.CommonData;
import com.dqhuynh.gplace.model.Place;
import com.dqhuynh.gplace.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 6/25/2015.
 */
public class JSONParser {
    private static final String TAG = JSONParser.class.getSimpleName();

    public static ArrayList<String> placeAutoCompleteParser(JSONObject jsonObj) {
        ArrayList<String> resultList = new ArrayList<String>();
        try {
            String status = "";
            status = jsonObj.getString("status");
            if (status.equals("OK")) {
                //request successful
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                // Extract the Place descriptions from the results
                resultList = new ArrayList<String>(predsJsonArray.length());
                CommonData.listAutoCompletePlaceId = new ArrayList<String>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                    //save the place id to the list, so we can get the lat lng later
                    CommonData.listAutoCompletePlaceId.add(predsJsonArray.getJSONObject(i).getString("place_id"));
                }
            }
        } catch (JSONException e) {
            LogUtil.log(TAG, "Cannot parse", e);
        } catch (NullPointerException e) {
            LogUtil.log(TAG, "NullPointer", e);
        }
        return resultList;
    }

    public static Place placeDetailParser(JSONObject jsonObj) {
        Place place = null;
        try {
            String status = "";
            status = jsonObj.getString("status");
            if (status.equals("OK")) {
                place = new Place();
                JSONObject result = jsonObj.getJSONObject("result");
                place.setFormatted_address(result.getString("formatted_address"));
                place.setIcon(result.getString("icon"));
                place.setPlace_id(result.getString("place_id"));
                JSONObject geometry = result.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                LogUtil.log(TAG, location.toString());
                place.setLat(location.getDouble("lat"));
                place.setLng(location.getDouble("lng"));
            }
        } catch (JSONException e) {
            LogUtil.log(TAG, "Cannot parse", e);
        } catch (NullPointerException e) {
            LogUtil.log(TAG, "NullPointer", e);
        }
        return place;
    }

    public static Place geocodingLocation(JSONObject jsonObj) {
        Place place = new Place();
        LogUtil.log(TAG, "geocoding json: " + jsonObj.toString());
        try {
            String status = "";
            status = jsonObj.getString("status");
            if (status.equals("OK")) {
                place = new Place();
                JSONArray result = jsonObj.getJSONArray("results");
                place.setFormatted_address(result.getJSONObject(0).getString("formatted_address"));
                place.setPlace_id(result.getJSONObject(0).getString("place_id"));
                JSONObject geometry = result.getJSONObject(0).getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                place.setLat(location.getDouble("lat"));
                place.setLng(location.getDouble("lng"));
            }
        } catch (JSONException e) {
            LogUtil.log(TAG, "Cannot parse", e);
        } catch (NullPointerException e) {
            LogUtil.log(TAG, "NullPointer", e);
        }
        return place;
    }

    public static ArrayList<Place> placeNearByParser(JSONObject jsonObj) {
        AndroidHttpClient androidHttpClient;
        ArrayList<Place> listPlaces = new ArrayList<Place>();
        Place place = null;
        try {
            String status = "";
            status = jsonObj.getString("status");
            if (status.equals("OK")) {
                try {
                    //get the token of the next page result
                    CommonData.currentSearchOption.setPageToken(jsonObj.getString("next_page_token"));
                } catch (JSONException e) {
                    //there's no more result
                    CommonData.currentSearchOption.setDone(true);
                    CommonData.currentSearchOption.setPageToken("");
                }
                JSONArray result = jsonObj.getJSONArray("results");
                for (int i = 0; i < result.length(); i++) {
                    place = new Place();
                    JSONObject ob = result.getJSONObject(i);
                    place.setName(ob.getString("name"));
                    place.setFormatted_address(ob.getString("vicinity"));
                    place.setPlace_id(ob.getString("place_id"));
                    place.setIcon(ob.getString("icon"));
                    JSONObject geo = ob.getJSONObject("geometry");
                    JSONObject location = geo.getJSONObject("location");
                    place.setLat(location.getDouble("lat"));
                    place.setLng(location.getDouble("lng"));

                    //optional fields
                    try {
                        place.setRating((float) ob.getDouble("rating"));
                    } catch (JSONException e) {
                        LogUtil.log(TAG, "No rating");
                    }
                    try {
                        String directIcon = "";
                        JSONArray photos = ob.getJSONArray("photos");
                        JSONObject photo = photos.getJSONObject(0);
                        String photo_reference = photo.getString("photo_reference");
                        StringBuilder sb = new StringBuilder(APIConstant.PLACES_API_BASE +
                                APIConstant.TYPE_PHOTO);
                        sb.append("?maxheight=600");
                        sb.append("&photoreference=" + photo_reference);
                        sb.append("&key=" + APIConstant.API_BROWSER_KEY);
                      /*  try {
                      //get the direct url image
                            boolean redirect = false;
                            URL obj = new URL(sb.toString());
//                            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
//                            int statusCode = conn.getResponseCode();
//                            if (statusCode != HttpURLConnection.HTTP_OK) {
//                                if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
//                                        || statusCode == HttpURLConnection.HTTP_MOVED_PERM
//                                        || statusCode == HttpURLConnection.HTTP_SEE_OTHER)
//                                    redirect = true;
//                            }
//                            if(redirect) {
//                                directIcon = conn.getHeaderField("Location");
//                            }
//                            conn.disconnect();
                            androidHttpClient = AndroidHttpClient.newInstance("Android");
                            HttpGet httpGet = new HttpGet(sb.toString());
                            HttpResponse httpResponse = androidHttpClient.execute(httpGet);
                            androidHttpClient.close();

                            final int statusCode = httpResponse.getStatusLine().getStatusCode();
                            if (statusCode != HttpStatus.SC_OK) {
                                Header[] headers = httpResponse.getHeaders("Location");

                                if (headers != null && headers.length != 0) {
                                    directIcon = headers[headers.length - 1].getValue();
                                } else {
                                    return null;
                                }
                            }
                        } catch (MalformedURLException e) {

                        } catch (IllegalArgumentException e) {

                        } catch (IOException e) {

                        }
//                        if (!directIcon.equals("")) {
//                            LogUtil.log(TAG, "img url=" + directIcon);
//                            place.setIcon(directIcon);
//                        }
//                        else*/
                        place.setIcon(sb.toString());
                    } catch (JSONException e) {
                        LogUtil.log(TAG, "No photo...");
                    }
                    listPlaces.add(place);
                }
            } else {
                //
            }
        } catch (JSONException e) {
            LogUtil.log(TAG, "Cannot parse", e);
        } catch (NullPointerException e) {
            LogUtil.log(TAG, "NullPointer", e);
        }
        return listPlaces;
    }
}
