package com.mybottle.ingenicoversion2.model;

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
    private String customer_code;
    private String terminal_code;
    private String function_code;
    private String symptom_code;
    private int jobcustom_id;
    private String item_code;
    private String problem_description;
    private String resolution_description;
    @Nullable
    @SerializedName("spareparts")
    @Expose
    private List<String> spareparts = new ArrayList<String>();
    @SerializedName("resolutions")
    @Expose
    private List<String> resolutions = new ArrayList<String>();

    public ReviewJob(int job_id, String technician_code, String start_time, String finish_time, String customer_code, String terminal_code, String function_code, String symptom_code, List<String> spareparts, List<String> resolutions) {
        this.job_id = job_id;
        this.technician_code = technician_code;
        this.start_time = start_time;
        this.finish_time = finish_time;
        this.customer_code = customer_code;
        this.terminal_code = terminal_code;
        this.function_code = function_code;
        this.symptom_code = symptom_code;
        this.spareparts = spareparts;
        this.resolutions = resolutions;
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

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
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

    public int getJobcustom_id() {
        return jobcustom_id;
    }

    public void setJobcustom_id(int jobcustom_id) {
        this.jobcustom_id = jobcustom_id;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getProblem_description() {
        return problem_description;
    }

    public void setProblem_description(String problem_description) {
        this.problem_description = problem_description;
    }

    public String getResolution_description() {
        return resolution_description;
    }

    public void setResolution_description(String resolution_description) {
        this.resolution_description = resolution_description;
    }

    @Nullable
    public List<String> getSpareparts() {
        return spareparts;
    }

    public void setSpareparts(@Nullable List<String> spareparts) {
        this.spareparts = spareparts;
    }

    public List<String> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<String> resolutions) {
        this.resolutions = resolutions;
    }

}
