package com.ingenicosms.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ILM on 8/3/2016.
 */
public class SparepartResponse {
    private boolean status;
    @SerializedName("data")
    private List<Sparepart> results;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Sparepart> getResults() {
        return results;
    }

    public void setResults(List<Sparepart> results) {
        this.results = results;
    }
}
