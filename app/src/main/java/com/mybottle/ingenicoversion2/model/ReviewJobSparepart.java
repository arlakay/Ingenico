package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ILM on 8/10/2016.
 */
public class ReviewJobSparepart {
    @SerializedName("sparepart_code")
    private String sparepart_code;

    public ReviewJobSparepart(String sparepart_code) {
        this.sparepart_code = sparepart_code;
    }

    public String getSparepart_code() {
        return sparepart_code;
    }

    public void setSparepart_code(String sparepart_code) {
        this.sparepart_code = sparepart_code;
    }
}
