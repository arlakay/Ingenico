package com.mybottle.ingenicoversion2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by ILM on 7/25/2016.
 */
public class BaseActivity extends AppCompatActivity {
    public FirebaseAnalytics mFirebaseAnalytics;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }
}
