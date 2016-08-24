package com.ingenicosms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ingenicosms.api.RestApi;
import com.ingenicosms.api.services.ApiService;
import com.ingenicosms.model.Job;
import com.ingenicosms.utility.SessionManager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ILM on 8/2/2016.
 */
public class ClosingActivity extends BaseActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.txt_closing_edc_sn)TextView txtCloseEdcSn;
    @BindView(R.id.txt_closing_function)TextView txtCloseFunc;
    @BindView(R.id.txt_closing_symptom)TextView txtCloseSymp;
    @BindView(R.id.txt_closing_sparepart)TextView txtCloseSpare;
    private String edcSN, valFunc, valSymp, valSpare, techCode, jobId, start;
    private String TAG = ClosingActivity.class.getSimpleName();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing);
        ButterKnife.bind(this);

        setupToolbar();

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        if (!sessionManager.isLoggedIn()) {
            sessionManager.setLogin(false);
            sessionManager.logoutUser();
        }

        HashMap<String, String> user = sessionManager.getUserDetails();
        techCode = user.get(SessionManager.KEY_TECHNICIAN_CODE);
        jobId = user.get(SessionManager.KEY_JOB_ID);
        start = user.get(SessionManager.KEY_START_TIME);
        edcSN = user.get(SessionManager.KEY_BARCODE);

        getData();

        txtCloseEdcSn.setText(edcSN);
        txtCloseFunc.setText(valFunc);
        txtCloseSymp.setText(valSymp);
        txtCloseSpare.setText(valSpare);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    private void getData(){
        Intent i = getIntent();
        valFunc = i.getStringExtra("func");
        valSymp = i.getStringExtra("symp");
        valSpare = i.getStringExtra("spare");
    }

    private void closingEndJob(String techCode, String termCode, String func, String symp, String spare, String startTime) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<Job> call = apiService.endJob(techCode, termCode, func, symp, spare, startTime, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job>call, Response<Job> response) {
                dialog.dismiss();

//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "Data received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().isStatus() == true){

                    AlertDialog alertDialog = new AlertDialog.Builder(ClosingActivity.this).create();
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Finish Job Success");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            Intent i = new Intent(ClosingActivity.this, MenuHomeActivity.class);
//                            startActivity(i);
                            finish();
                        }
                    });
                    alertDialog.show();
                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(ClosingActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Finish Job Failed");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }

            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                dialog.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(ClosingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
                //alertDialog.setIcon(R.drawable.tick);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ClosingActivity.this, MenuHomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                alertDialog.show();
            }
        });
    }

    @OnClick(R.id.btn_closing_submit)
    public void submit() {
//        Toast.makeText(ClosingActivity.this, "submit", Toast.LENGTH_LONG).show();

        String fn = txtCloseFunc.getText().toString();
        String sy = txtCloseSymp.getText().toString();
        String sp = txtCloseSpare.getText().toString();

        String subStrFunc = fn.substring(0,1);
        String subStrSymp = sy.substring(0,2);
//        String subStrSpare = sp.substring(0,9);

//        Log.e(TAG, "func : " + subStrFunc + " symp : " + subStrSymp + " spare : " + sp);

        if (techCode.trim().length() > 0 && fn.trim().length() > 0 &&
                sy.trim().length() > 0 ) {

            closingEndJob(techCode, edcSN, subStrFunc, subStrSymp, sp, start);

        }
    }

    @OnClick(R.id.btn_closing_cancel)
    public void cancel() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Do Nothing
    }
}
