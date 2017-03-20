package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ILM on 8/3/2016.
 */
public class Symptom {
    @SerializedName("symptom_id")
    private int symptom_id;
    @SerializedName("function_code")
    private int function_code;
    @SerializedName("symptom_code")
    private int symptom_code;
    @SerializedName("symptom_description")
    private String symptom_description;

    public Symptom() {
    }

    public Symptom(int symptom_id, int function_code, int symptom_code, String symptom_description) {
        this.symptom_id = symptom_id;
        this.function_code = function_code;
        this.symptom_code = symptom_code;
        this.symptom_description = symptom_description;
    }

    public int getSymptom_id() {
        return symptom_id;
    }

    public void setSymptom_id(int symptom_id) {
        this.symptom_id = symptom_id;
    }

    public int getFunction_code() {
        return function_code;
    }

    public void setFunction_code(int function_code) {
        this.function_code = function_code;
    }

    public int getSymptom_code() {
        return symptom_code;
    }

    public void setSymptom_code(int symptom_code) {
        this.symptom_code = symptom_code;
    }

    public String getSymptom_description() {
        return symptom_description;
    }

    public void setSymptom_description(String symptom_description) {
        this.symptom_description = symptom_description;
    }
}
