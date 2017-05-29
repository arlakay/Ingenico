package com.mybottle.ingenicoversion2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.mybottle.ingenicoversion2.api.RestApi;
import com.mybottle.ingenicoversion2.api.services.ApiService;
import com.mybottle.ingenicoversion2.model.Job;
import com.mybottle.ingenicoversion2.utility.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    @BindView(R.id.txt_closing_customer)TextView txtCloseCstmr;
    @BindView(R.id.txt_closing_resolution)TextView txtCloseRsltn;
    @BindView(R.id.txt_closing_close_reason)TextView txtCloseClsReason;

    private String edcSN, valFunc, valSymp, valSpare, techCode, jobId, start, valCstmr, valRsltn,
            valClsReason, currentDateTimeString, brandCodeSP;
    private String cs, fn, sy, sp, rs, cr;
    private String TAG = ClosingActivity.class.getSimpleName();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        brandCodeSP = user.get(SessionManager.KEY_BRAND_CODE);

        getData();

        Log.e(TAG, "Sparepart:"+valSpare);
        Log.e(TAG, "Resolution:"+valRsltn);

        txtCloseEdcSn.setText(edcSN);
        txtCloseFunc.setText(valFunc);
        txtCloseSymp.setText(valSymp);
        txtCloseSpare.setText(valSpare);
        txtCloseCstmr.setText(valCstmr);
        txtCloseRsltn.setText(valRsltn);
        txtCloseClsReason.setText(valClsReason);
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
        valCstmr = i.getStringExtra("cstmr");
        valRsltn = i.getStringExtra("rsltn");
        valClsReason = i.getStringExtra("clsReason");
    }

    private void closingEndJob(final String techCode, String func, String symp, String spare, String jobId,
                               String resolution, String customer, final String terminalCode, String closeReason) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService = RestApi.getClient().create(ApiService.class);

        Call<Job> call = apiService.endJob2Hit(techCode, func, symp, spare, jobId, resolution, customer, terminalCode, BuildConfig.INGENICO_API_KEY, closeReason, brandCodeSP);
        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job>call, Response<Job> response) {
                dialog.dismiss();

//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "CloseReason received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().isStatus() == true){

                    AlertDialog alertDialog = new AlertDialog.Builder(ClosingActivity.this).create();
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Finish Job Success");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            currentDateTimeString = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

                            Bundle params = new Bundle();
                            params.putString("technician_id", techCode);
                            params.putString("edc_barcode", terminalCode);
                            params.putString("edc_finish_time", currentDateTimeString);
                            mFirebaseAnalytics.logEvent("finish_repair_edc", params);

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

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                AlertDialog alertDialog = new AlertDialog.Builder(ClosingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
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
        cs = "";
        fn = "";
        sy = "";
        sp = "";
        rs = "";
        cr = "";

        cs = txtCloseCstmr.getText().toString();
        fn = txtCloseFunc.getText().toString();
        sy = txtCloseSymp.getText().toString();
        sp = txtCloseSpare.getText().toString();
        rs = txtCloseRsltn.getText().toString();
        cr = txtCloseClsReason.getText().toString();

        Log.e(TAG, "test print sparepart: "+sp);

        String[] cstmrparts = cs.split("\\-"); // explode di android .
        String cspart1 = cstmrparts[0];
        String cspart2 = cstmrparts[1];
        String subStrCstmr = cspart1;

        String[] funcparts = fn.split("\\-"); // explode di android .
        String fupart1 = funcparts[0];
        String fupart2 = funcparts[1];
        String subStrFunc = fupart1;

        String[] sympparts = sy.split("\\-"); // explode di android .
        String symppart1 = sympparts[0];
        String symppart2 = sympparts[1];
        String subStrSymp = symppart1;

        String[] clsreasonparts = cr.split("\\-"); // explode di android .
        String clsreasonpart1 = sympparts[0];
        String clsreasonpart2 = sympparts[1];
        String subStrClsReason = clsreasonpart1;


//        String subStrSpare = sp.substring(0,9);

//        Log.e(TAG, "func : " + subStrFunc + " symp : " + subStrSymp + " spare : " + sp);

//        Log.e(TAG, "End Job Id = " + jobId);

        if (techCode.trim().length() > 0 && fn.trim().length() > 0 &&
                sy.trim().length() > 0 ) {

            closingEndJob(techCode, subStrFunc, subStrSymp, sp, jobId, rs, subStrCstmr, edcSN, subStrClsReason);

        }
    }

    @OnClick(R.id.btn_closing_cancel)
    public void cancel() {
        Intent i = new Intent(ClosingActivity.this, RepairingActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
