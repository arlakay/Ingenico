package com.ingenicosms.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ingenicosms.LoginActivity;

import java.util.HashMap;

/**
 * Created by E.R.D on 4/2/2016.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Ingenico Session";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public static final String KEY_ATTENDANCE_ID = "attendance_id";
    public static final String KEY_TECHNICIAN_CODE = "technician_code";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_JOB_ID = "job_id";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_BARCODE = "barcode";
    public static final String KEY_START_TIME = "start_time";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String attendanceId, String technicianCode, String firstName, String lastName, String password){
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        editor.putString(KEY_ATTENDANCE_ID, attendanceId);
        editor.putString(KEY_TECHNICIAN_CODE, technicianCode);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public void createBarcodeSession(String barcode){
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        editor.putString(KEY_BARCODE, barcode);

        editor.commit();
    }

    public void createStartTimeSession(String startTime){
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        editor.putString(KEY_START_TIME, startTime);

        editor.commit();
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ATTENDANCE_ID, pref.getString(KEY_ATTENDANCE_ID, null));
        user.put(KEY_TECHNICIAN_CODE, pref.getString(KEY_TECHNICIAN_CODE, null));
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null));
        user.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, null));
        user.put(KEY_JOB_ID, pref.getString(KEY_JOB_ID, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_BARCODE, pref.getString(KEY_BARCODE, null));
        user.put(KEY_START_TIME, pref.getString(KEY_START_TIME, null));

        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

}
