package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

public class CloseReason {
    @SerializedName("closereason_id")
    private int closereason_id;
    @SerializedName("closereason_code")
    private String closereason_code;
    @SerializedName("closereason_description")
    private String closereason_description;

    public int getClosereason_id() {
        return closereason_id;
    }

    public void setClosereason_id(int closereason_id) {
        this.closereason_id = closereason_id;
    }

    public String getClosereason_code() {
        return closereason_code;
    }

    public void setClosereason_code(String closereason_code) {
        this.closereason_code = closereason_code;
    }

    public String getClosereason_description() {
        return closereason_description;
    }

    public void setClosereason_description(String closereason_description) {
        this.closereason_description = closereason_description;
    }
}
