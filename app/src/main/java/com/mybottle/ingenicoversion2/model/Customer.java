package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ERD on 23/09/2016.
 */
public class Customer {
    @SerializedName("customer_id")
    private int customer_id;
    @SerializedName("customer_code")
    private String customer_code;
    @SerializedName("customer_description")
    private String customer_description;

    public Customer(int customer_id, String customer_code, String customer_description) {
        this.customer_id = customer_id;
        this.customer_code = customer_code;
        this.customer_description = customer_description;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public String getCustomer_description() {
        return customer_description;
    }

    public void setCustomer_description(String customer_description) {
        this.customer_description = customer_description;
    }
}
