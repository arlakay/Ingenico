package com.mybottle.ingenicoversion2.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ILM on 8/2/2016.
 */
public class Technician {
    @SerializedName("status")
    private boolean status;
    @SerializedName("logout")
    private String logout;
    @SerializedName("attendance")
    private String attendance;
    @SerializedName("attendance_id")
    private String attendance_id;
    @SerializedName("technician_id")
    private int technician_id;
    @SerializedName("technician_code")
    private String technician_code;
    @SerializedName("password")
    private String password;
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("last_name")
    private String last_name;

    public Technician() {
    }

    public Technician(boolean status, String attendance, String attendance_id, int technician_id, String technician_code, String password, String first_name, String last_name) {
        this.status = status;
        this.attendance = attendance;
        this.attendance_id = attendance_id;
        this.technician_id = technician_id;
        this.technician_code = technician_code;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getAttendance_id() {
        return attendance_id;
    }

    public void setAttendance_id(String attendance_id) {
        this.attendance_id = attendance_id;
    }

    public int getTechnician_id() {
        return technician_id;
    }

    public void setTechnician_id(int technician_id) {
        this.technician_id = technician_id;
    }

    public String getTechnician_code() {
        return technician_code;
    }

    public void setTechnician_code(String technician_code) {
        this.technician_code = technician_code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
