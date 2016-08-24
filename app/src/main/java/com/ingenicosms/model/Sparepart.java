package com.ingenicosms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ILM on 8/3/2016.
 */
public class Sparepart {
    @SerializedName("sparepart_id")
    private int sparepart_id;
    @SerializedName("sparepart_code")
    private int sparepart_code;
    @SerializedName("sparepart_description")
    private String sparepart_description;

    public Sparepart(int sparepart_id, int sparepart_code, String sparepart_description) {
        this.sparepart_id = sparepart_id;
        this.sparepart_code = sparepart_code;
        this.sparepart_description = sparepart_description;
    }

    public Sparepart() {
    }

    public int getSparepart_id() {

        return sparepart_id;
    }

    public void setSparepart_id(int sparepart_id) {
        this.sparepart_id = sparepart_id;
    }

    public int getSparepart_code() {
        return sparepart_code;
    }

    public void setSparepart_code(int sparepart_code) {
        this.sparepart_code = sparepart_code;
    }

    public String getSparepart_description() {
        return sparepart_description;
    }

    public void setSparepart_description(String sparepart_description) {
        this.sparepart_description = sparepart_description;
    }
}
