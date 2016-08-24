package com.ingenicosms.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ILM on 8/5/2016.
 */
public class ReviewJobResponse {
    private boolean status;
    private String technician_code;
    @SerializedName("data")
    private List<ReviewJob> results;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTechnician_code() {
        return technician_code;
    }

    public void setTechnician_code(String technician_code) {
        this.technician_code = technician_code;
    }

    public List<ReviewJob> getResults() {
        return results;
    }

    public void setResults(List<ReviewJob> results) {
        this.results = results;
    }
}
