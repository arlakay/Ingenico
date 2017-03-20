package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ILM on 8/3/2016.
 */
public class SymptomResponse {
    private boolean status;
    @SerializedName("data")
    private List<Symptom> results;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Symptom> getResults() {
        return results;
    }

    public void setResults(List<Symptom> results) {
        this.results = results;
    }
}
