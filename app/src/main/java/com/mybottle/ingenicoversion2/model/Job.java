package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ILM on 8/4/2016.
 */
public class Job {
    private boolean status;
    private String message;
    private int job_id;
    private int jobcustom_id;
    @SerializedName("customer_description")
    @Expose
    private JobCustomerDescription customerDescription;
    @SerializedName("brand_code")
    @Expose
    private String brandCode;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public int getJobcustom_id() {
        return jobcustom_id;
    }

    public void setJobcustom_id(int jobcustom_id) {
        this.jobcustom_id = jobcustom_id;
    }

    public JobCustomerDescription getCustomerDescription() {
        return customerDescription;
    }

    public void setCustomerDescription(JobCustomerDescription customerDescription) {
        this.customerDescription = customerDescription;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }
}
