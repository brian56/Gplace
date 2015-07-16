package com.dqhuynh.gplace.common;

import android.location.Location;

import com.dqhuynh.gplace.model.SearchOptions;

import java.util.ArrayList;

/**
 * Created by Administrator on 6/24/2015.
 */
public class CommonData {
    public static ArrayList<String> listAutoCompletePlaceId = null;
    public static Location currentLocation = null;
    public static Location searchLocation = null;
    public static String country = "";
    public static String language = "";
    public static boolean searchByCurrentLocation = true;
    public static SearchOptions currentSearchOption = new SearchOptions();

}
