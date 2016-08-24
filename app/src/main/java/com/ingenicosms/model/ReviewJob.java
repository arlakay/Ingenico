package com.ingenicosms.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ILM on 8/1/2016.
 */
public class ReviewJob {
    private int job_id;
    private String technician_code;
    private String start_time;
    private String finish_time;
    private String terminal_code;
    private String function_code;
    private String symptom_code;
    @Nullable
    @SerializedName("spareparts")
    @Expose
    private List<String> spareparts = new ArrayList<String>();

    public ReviewJob(int job_id, String technician_code, String start_time, String finish_time, String terminal_code, String function_code, String symptom_code) {
        this.job_id = job_id;
        this.technician_code = technician_code;
        this.start_time = start_time;
        this.finish_time = finish_time;
        this.terminal_code = terminal_code;
        this.function_code = function_code;
        this.symptom_code = symptom_code;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public String getTechnician_code() {
        return technician_code;
    }

    public void setTechnician_code(String technician_code) {
        this.technician_code = technician_code;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(String finish_time) {
        this.finish_time = finish_time;
    }

    public String getTerminal_code() {
        return terminal_code;
    }

    public void setTerminal_code(String terminal_code) {
        this.terminal_code = terminal_code;
    }

    public String getFunction_code() {
        return function_code;
    }

    public void setFunction_code(String function_code) {
        this.function_code = function_code;
    }

    public String getSymptom_code() {
        return symptom_code;
    }

    public void setSymptom_code(String symptom_code) {
        this.symptom_code = symptom_code;
    }

    public List<String> getSpareparts() {
        return spareparts;
    }

    public void setSpareparts(List<String> spareparts) {
        this.spareparts = spareparts;
    }
}
