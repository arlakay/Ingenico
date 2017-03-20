package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by e_er_de on 08/02/2017.
 */

public class CloseReasonResponse {

    @SerializedName("status")
    private boolean status;
    @SerializedName("data")
    private List<CloseReason> data;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<CloseReason> getData() {
        return data;
    }

    public void setData(List<CloseReason> data) {
        this.data = data;
    }
}
