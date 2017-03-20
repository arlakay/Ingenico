package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ERD on 23/09/2016.
 */
public class Resolution {
    @SerializedName("resolution_id")
    private int resolution_id;
    @SerializedName("resolution_code")
    private String resolution_code;
    @SerializedName("resolution_description")
    private String resolution_description;

    public Resolution(int resolution_id, String resolution_code, String resolution_description) {
        this.resolution_id = resolution_id;
        this.resolution_code = resolution_code;
        this.resolution_description = resolution_description;
    }

    public int getResolution_id() {
        return resolution_id;
    }

    public void setResolution_id(int resolution_id) {
        this.resolution_id = resolution_id;
    }

    public String getResolution_code() {
        return resolution_code;
    }

    public void setResolution_code(String resolution_code) {
        this.resolution_code = resolution_code;
    }

    public String getResolution_description() {
        return resolution_description;
    }

    public void setResolution_description(String resolution_description) {
        this.resolution_description = resolution_description;
    }
}
