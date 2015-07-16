package com.dqhuynh.gplace.utils;

import android.util.Log;

import com.dqhuynh.gplace.common.Config;

/**
 * Created by Administrator on 6/25/2015.
 */
public class LogUtil {
    public static void log(String TAG, String message) {
        if(Config.isLog) {
            Log.e(TAG, message);
        }
    }
    public static void log(String TAG, String message, Exception e) {
        if(Config.isLog) {
            Log.e(TAG, message + e.toString());
        }
    }
}
