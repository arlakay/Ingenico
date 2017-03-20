package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ERD on 23/09/2016.
 */
public class ResolutionResponse {
    private boolean status;
    @SerializedName("data")
    private List<Resolution> results;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Resolution> getResults() {
        return results;
    }

    public void setResults(List<Resolution> results) {
        this.results = results;
    }
}