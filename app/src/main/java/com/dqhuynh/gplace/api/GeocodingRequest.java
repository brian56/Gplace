package com.dqhuynh.gplace.api;

import android.location.Location;

import com.dqhuynh.gplace.common.CommonData;
import com.dqhuynh.gplace.model.Place;
import com.dqhuynh.gplace.parser.JSONParser;
import com.dqhuynh.gplace.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Administrator on 6/23/2015.
 */
public class GeocodingRequest {
    StringBuilder jsonResults = new StringBuilder();
    private Place mCurrentAddress = null;
    private static final String TAG = GeocodingRequest.class.getSimpleName();

    public Place geocoding (Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        StringBuilder sb = new StringBuilder(APIConstant.MAP_API_BASE + APIConstant.TYPE_GEOCODE + APIConstant.OUT_JSON);
        sb.append("?key=" + APIConstant.API_BROWSER_KEY);
        sb.append("&language=" + CommonData.language);
        sb.append("&latlng=" + lat + "," + lng);
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(sb.toString());
            LogUtil.log(TAG,"url="+url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(APIConstant.TIME_OUT);
            conn.setReadTimeout(APIConstant.TIME_OUT);
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            LogUtil.log(TAG, "Error processing Places API URL", e);
            return mCurrentAddress;
        } catch (SocketTimeoutException e) {
            LogUtil.log(TAG, "Socket timeout exception", e);
        }
        catch (IOException e) {
            LogUtil.log(TAG, "Error connecting to Places API", e);
            return mCurrentAddress;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj = new JSONObject(jsonResults.toString());
        } catch (JSONException e) {
            LogUtil.log(TAG, "Error Parse JSON", e);
            return mCurrentAddress;
        }
        mCurrentAddress = JSONParser.geocodingLocation(jsonObj);
        return mCurrentAddress;

//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LogUtil.log(TAG,jsonObject.toString());
//                mCurrentAddress = JSONParser.geocodingLocation(jsonObject);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                LogUtil.log(TAG,"Error request");
//            }
//        });
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
