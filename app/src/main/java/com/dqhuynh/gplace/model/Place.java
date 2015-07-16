package com.dqhuynh.gplace.model;

import java.io.Serializable;

/**
 * Created by Administrator on 6/16/2015.
 */
public class Place implements Serializable {
    static final long serialVersionUID = -3010695769693014199L;
    private String name;
    private String type[];
    private float rating;
    private double lat;
    private double lng;
    private String place_id;
    private String icon;

    private String international_phone_number;
    private String website;
    private int user_ratings_total;
    private String formatted_address;
    private String short_name;


    public Place() {
        this.name = "";
        this.type = new String[0];
        this.rating = 0;
        this.lat = 0;
        this.lng = 0;
        this.place_id = "";
        this.icon = "";
        this.international_phone_number = "";
        this.website = "";
        this.user_ratings_total = 0;
        this.formatted_address = "";
        this.short_name = "";
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }
}