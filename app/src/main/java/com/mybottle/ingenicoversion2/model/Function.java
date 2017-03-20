package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ILM on 8/3/2016.
 */
public class Function {
    @SerializedName("function_id")
    private int function_id;
    @SerializedName("function_code")
    private int function_code;
    @SerializedName("function_description")
    private String function_description;

    public Function() {
    }

    public Function(int function_id, int function_code, String function_description) {
        this.function_id = function_id;
        this.function_code = function_code;
        this.function_description = function_description;
    }

    public int getFunction_id() {
        return function_id;
    }

    public void setFunction_id(int function_id) {
        this.function_id = function_id;
    }

    public int getFunction_code() {
        return function_code;
    }

    public void setFunction_code(int function_code) {
        this.function_code = function_code;
    }

    public String getFunction_description() {
        return function_description;
    }

    public void setFunction_description(String function_description) {
        this.function_description = function_description;
    }
}
