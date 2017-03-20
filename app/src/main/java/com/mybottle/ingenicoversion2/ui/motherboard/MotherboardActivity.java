package com.mybottle.ingenicoversion2.ui.motherboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mybottle.ingenicoversion2.BaseActivity;
import com.mybottle.ingenicoversion2.BuildConfig;
import com.mybottle.ingenicoversion2.MenuHomeActivity;
import com.mybottle.ingenicoversion2.R;
import com.mybottle.ingenicoversion2.api.RestApi;
import com.mybottle.ingenicoversion2.api.services.ApiService;
import com.mybottle.ingenicoversion2.model.Job;
import com.mybottle.ingenicoversion2.utility.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MotherboardActivity extends BaseActivity {
    @BindView(R.id.loading)ProgressBar loadingView;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.part_number)TextView et_partnumber;
    @BindView(R.id.problem)EditText et_problem;
    @BindView(R.id.resolution)EditText et_resolution;

    private String TAG = MotherboardActivity.class.getSimpleName();
    private SessionManager sessionManager;
    private String techCode, motherboardCodeFromScan, jobIdFromApiStartMotherboard;

    TextView textView ;
    Button start, pause, reset, lap ;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, MilliSeconds ;
    ListView listView ;
    String[] ListElements = new String[] {  };
    List<String> ListElementsArrayList ;
    ArrayAdapter<String> adapter ;
    String motherboardValue, jobIdFromSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motherboard);
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

        textView = (TextView)findViewById(R.id.textView);
        listView = (ListView)findViewById(R.id.listview1);

        handler = new Handler() ;

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));

        adapter = new ArrayAdapter<String>(MotherboardActivity.this,
                android.R.layout.simple_list_item_1,
                ListElementsArrayList
        );

        listView.setAdapter(adapter);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    public Runnable runnable = new Runnable() {

        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            textView.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));
            handler.postDelayed(this, 0);
        }

    };

    @OnClick(R.id.part_number)
    public void editEDCSN() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_edit_serial_number, null);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(promptsView);

        final EditText input = (EditText) promptsView
                .findViewById(R.id.et_dialog_edit_serialnumber);

        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
//                Log.d(TAG, "New Serial Number: " + value);
                sessionManager.setLogin(true);
                sessionManager.createMotherboardBarcodeSession(value);
                onResume();
                startJob(motherboardValue);

                return;
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog b = alertDialog.create();
        b.show();
    }

    @OnClick(R.id.button4)
    public void saveMotherboardWork(View v){
        HashMap<String, String> user = sessionManager.getUserDetails();
        jobIdFromSP = user.get(SessionManager.KEY_JOB_ID);

        finishJob(et_partnumber.getText().toString(), jobIdFromSP, et_problem.getText().toString(), et_resolution.getText().toString());

    }

    @Override
    protected void onResume() {
        super.onResume();

        HashMap<String, String> user = sessionManager.getUserDetails();
        motherboardValue = user.get(SessionManager.KEY_BARCODE_MOTHERBOARD);

        et_partnumber.invalidate();
        et_partnumber.setText(motherboardValue);

        et_problem.requestFocus();

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
        super.onBackPressed();
    }

    private void startJob(String motherboard){
        loadingView.setVisibility(View.VISIBLE);

        ApiService apiService = RestApi.getClient().create(ApiService.class);

        Call<Job> call = apiService.startJobMotherboard(techCode, motherboard, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job>call, Response<Job> response) {
                loadingView.setVisibility(View.GONE);

                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "Start MB received: " + new Gson().toJson(response.body()));

                if (response.isSuccessful() && response.body().isStatus()) {
                    int jobIdJson = response.body().getJobcustom_id();
                    jobIdFromApiStartMotherboard = String.valueOf(jobIdJson);
                    Log.e(TAG, "Start Job Custom id = "+ jobIdFromApiStartMotherboard);
                    sessionManager.createJobIdSession(jobIdFromApiStartMotherboard);

                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
//                    reset.setEnabled(false);

                    Bundle params = new Bundle();
                    params.putString("name", techCode);
                    mFirebaseAnalytics.logEvent("start_motherboard", params);

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MotherboardActivity.this).create();
                    alertDialog.setTitle("Warning!");
                    alertDialog.setMessage("Failed Save Start Time, Please Retry!");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<Job>call, Throwable t) {
                loadingView.setVisibility(View.GONE);

                AlertDialog alertDialog = new AlertDialog.Builder(MotherboardActivity.this).create();
                alertDialog.setTitle("Network Error");
                alertDialog.setMessage("Make sure , you're connected to the internet.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });
    }

    private void finishJob(String motherboardCode, String jobId, String problem, String resolution) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService = RestApi.getClient().create(ApiService.class);

        Call<Job> call = apiService.finishJobMotherboard(techCode, motherboardCode, jobId, BuildConfig.INGENICO_API_KEY, problem, resolution);
        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job>call, Response<Job> response) {
                dialog.dismiss();

                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "Finish MB received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().isStatus()){

                    AlertDialog alertDialog = new AlertDialog.Builder(MotherboardActivity.this).create();
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Finish Job Success");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sessionManager.createMotherboardBarcodeSession("");

                            Bundle params = new Bundle();
                            params.putString("name", techCode);
                            mFirebaseAnalytics.logEvent("finish_motherboard", params);

                            finish();
                        }
                    });
                    alertDialog.show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MotherboardActivity.this).create();
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

                AlertDialog alertDialog = new AlertDialog.Builder(MotherboardActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MotherboardActivity.this, MenuHomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                alertDialog.show();
            }
        });
    }

}
