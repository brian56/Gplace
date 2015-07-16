package com.dqhuynh.gplace.api;

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
public class PlaceDetailRequest {
    private Place mPlace = new Place();
    private static final String TAG = PlaceDetailRequest.class.getSimpleName();

    public Place placeDetail(String placeId) {
        StringBuilder jsonResults = new StringBuilder();
        StringBuilder sb = new StringBuilder(APIConstant.PLACES_API_BASE + APIConstant.TYPE_DETAIL + APIConstant.OUT_JSON);
        sb.append("?key=" + APIConstant.API_BROWSER_KEY);
        sb.append("&language=" + CommonData.language);
        sb.append("&placeid=" + placeId);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(sb.toString());
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
            return mPlace;
        } catch (SocketTimeoutException e) {
            LogUtil.log(TAG, "Socket timeout exception", e);
        } catch (IOException e) {
            LogUtil.log(TAG, "Error connecting to Places API", e);
            return mPlace;
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
            return mPlace;
        }
        mPlace = JSONParser.placeDetailParser(jsonObj);
        return mPlace;
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                mPlace = JSONParser.placeDetailParser(jsonObject);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                //Toast.makeText(getActivity(), "Error on request", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
