package com.dqhuynh.gplace.model;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 7/6/2015.
 */
public class SearchOptions implements Serializable {
    static final long serialVersionUID = -3010695769693014199L;
    public static final String LOCATION = "location";
    public static final String RADIUS = "radius";
    public static final String KEYWORD = "keyword";
    public static final String LANGUAGE = "language";
    public static final String NAME = "name";
    public static final String OPEN_NOW = "opennow";
    public static final String TYPES = "types";
    public static final String RANK_BY = "rankby";
    public static final String PAGE_TOKEN = "pagetoken";
    public static final String DISTANCE = "distance";
    public static final String PROMINENCE = "prominence";

    private Location location = null;
    private int radius = 0;
    private String keyword = "";
    private String language = "";
    private String name ="";
    private boolean openNow = false;
    private ArrayList<PlaceType> placeTypes = new ArrayList<PlaceType>();
    private String rankBy = "";
    private String pageToken ="";

    public SearchOptions() {

    }
    public SearchOptions(Location location, int radius, String keyword, String language,
                         String name, boolean openNow, ArrayList<PlaceType> placeTypes,
                         String rankBy,
                         String pageToken) {
        this.location = location;
        this.radius = radius;
        this.keyword = keyword;
        this.language = language;
        this.name = name;
        this.openNow = openNow;
        this.placeTypes = placeTypes;
        this.rankBy = rankBy;
        this.pageToken = pageToken;
    }

    public boolean canLoadMore() {
        if(pageToken.equals("")) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(location!=null) {
            sb.append(LOCATION);
            sb.append("=");
            sb.append(location.getLatitude());
            sb.append(",");
            sb.append(location.getLongitude());
            sb.append("&");
        }
        if(radius!=0 && !rankBy.equals("distance")) {
            sb.append(RADIUS);
            sb.append("=");
            sb.append(radius*1000);
            sb.append("&");
        }
        if(!keyword.equals("")) {
            sb.append(KEYWORD);
            sb.append("=");
            sb.append(keyword);
            sb.append("&");
        }
        if(!language.equals("")) {
            sb.append(LANGUAGE);
            sb.append("=");
            sb.append(language);
            sb.append("&");
        }
        if(!name.equals("")) {
            sb.append(NAME);
            sb.append("=");
            sb.append(name);
            sb.append("&");
        }
        if(!name.equals("")) {
            sb.append(NAME);
            sb.append("=");
            sb.append(name);
            sb.append("&");
        }
        if(openNow) {
            sb.append(OPEN_NOW);
            sb.append("&");
        }
        if(placeTypes!=null) {
            sb.append(TYPES);
            sb.append("=");
            for(PlaceType p : placeTypes) {
                sb.append(p.getCode());
                if(p.getListPosition()!=placeTypes.size()-1)
                    sb.append("|");
            }
            sb.append("&");
        }
        if(!rankBy.equals("")) {
            sb.append(RANK_BY);
            sb.append("=");
            sb.append(rankBy);
            sb.append("&");
        }
        if(!pageToken.equals("")) {
            sb.append(PAGE_TOKEN);
            sb.append("=");
            sb.append(pageToken);
            sb.append("&");
        }
        return sb.toString();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public ArrayList<PlaceType> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(ArrayList<PlaceType> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public String getRankBy() {
        return rankBy;
    }

    public void setRankBy(String rankBy) {
        this.rankBy = rankBy;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }
}
