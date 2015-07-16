package com.dqhuynh.gplace.api;

import android.util.Log;

import com.dqhuynh.gplace.common.CommonData;
import com.dqhuynh.gplace.common.Config;
import com.dqhuynh.gplace.parser.JSONParser;
import com.dqhuynh.gplace.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 6/23/2015.
 */
public class PlaceAutoCompleteRequest {
    private ArrayList<String> resultList = new ArrayList<String>();
    private static final String TAG = PlaceAutoCompleteRequest.class.getSimpleName();

    public ArrayList<String> autoComplete(String input) {
        StringBuilder jsonResults = new StringBuilder();
        StringBuilder sb = new StringBuilder(APIConstant.PLACES_API_BASE + APIConstant.TYPE_AUTOCOMPLETE + APIConstant.OUT_JSON);
        sb.append("?key=" + APIConstant.API_BROWSER_KEY);
        sb.append("&language=" + CommonData.language);
        if (!CommonData.country.equals(""))
            sb.append("&components=country:" + CommonData.country);
//            sb.append("&types=(cities)");
        try {
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            if (Config.isLog) {
                Log.d(TAG, e.toString());
            }
        }
//        String url = sb.toString();
//        LogUtil.log(TAG,url);
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
            return resultList;
        } catch (SocketTimeoutException e) {
            LogUtil.log(TAG, "Socket timeout exception", e);
        } catch (IOException e) {
            LogUtil.log(TAG, "Error connecting to Places API", e);
            return resultList;
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
            return resultList;
        }
        resultList = JSONParser.placeAutoCompleteParser(jsonObj);

//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LogUtil.log(TAG,jsonObject.toString());
//                resultList = JSONParser.placeAutoCompleteParser(jsonObject);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                //Toast.makeText(getActivity(), "Error on request", Toast.LENGTH_SHORT).show();
//                LogUtil.log(TAG, "Error");
//            }
//        });
//        AppController.getInstance().addToRequestQueue(jsonObjReq);
        return resultList;
    }
}
