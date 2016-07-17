package com.dqhuynh.gplace.model;

import java.io.Serializable;

/**
 * Created by Administrator on 6/29/2015.
 */
public class PlaceType implements Serializable {
    static final long serialVersionUID = -3010695769693014199L;
    public static final int ITEM = 0;
    public static final int SECTION = 1;
    private String code;
    private String name;
    private final int type;
    private boolean isSection;
    private boolean isChecked;
    private int sectionPosition;
    private int listPosition;
    private int iconId;
    public PlaceType (String code, String name, boolean isSection, int sectionPosition, int listPosition, boolean isChecked, int type) {
        this.code = code;
        this.name = name;
        this.isSection = isSection;
        this.sectionPosition = sectionPosition;
        this.listPosition = listPosition;
        this.isChecked = isChecked;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean isSection) {
        this.isSection = isSection;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getType() {
        return type;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    @Override
    public String toString() {
        return name + "-"+ listPosition + " -" + sectionPosition;
    }
}
