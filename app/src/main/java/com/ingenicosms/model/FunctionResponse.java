package com.ingenicosms.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ILM on 8/3/2016.
 */
public class FunctionResponse {
    private boolean status;
    @SerializedName("data")
    private List<Function> results;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Function> getResults() {
        return results;
    }

    public void setResults(List<Function> results) {
        this.results = results;
    }
}
