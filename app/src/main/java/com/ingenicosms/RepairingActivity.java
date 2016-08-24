package com.ingenicosms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ingenicosms.api.RestApi;
import com.ingenicosms.api.services.ApiService;
import com.ingenicosms.model.Function;
import com.ingenicosms.model.FunctionResponse;
import com.ingenicosms.model.Sparepart;
import com.ingenicosms.model.SparepartResponse;
import com.ingenicosms.model.Symptom;
import com.ingenicosms.model.SymptomResponse;
import com.ingenicosms.utility.MultiSpinner;
import com.ingenicosms.utility.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ILM on 8/1/2016.
 */
public class RepairingActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.spin_function)Spinner spinFunc;
    @BindView(R.id.spin_symptom)Spinner spinSymp;
    @BindView(R.id.spin_sparepart)MultiSpinner spinSpare;
    @BindView(R.id.txt_sn_edc)TextView txtEdcSN;

    private String TAG = RepairingActivity.class.getSimpleName();
    private String barcodeValue, techCode, valueFunc, valueSymp, valueSpare, jobId;
    private String testSubStr, startTime;
    private List<Function> funcList;
    private List<Symptom> sympList;
    private List<Sparepart> spareList;
    private ArrayAdapter<String> adapter, adapter2, adapter3;
    private ArrayList<String> funcAr, sympAr, spareAr;
    private SessionManager sessionManager;

    @OnClick(R.id.btn_finish_repairing)
    public void finishRepair() {
        Intent i = new Intent(RepairingActivity.this, ClosingActivity.class);
        i.putExtra("edc_sn", barcodeValue);
        i.putExtra("func", valueFunc);
        i.putExtra("symp", valueSymp);
        i.putExtra("spare", valueSpare);
        i.putExtra("job_id", jobId);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repairing);
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
        barcodeValue = user.get(SessionManager.KEY_BARCODE);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        startTime = String.valueOf(year+"-"+month+"-"+date+" "+hour +":"+ minute +":"+ second);
//        Log.e("waktu Start :" , startTime);
        sessionManager.createStartTimeSession(startTime);

        funcAr = new ArrayList<String>();
        sympAr = new ArrayList<String>();
        spareAr = new ArrayList<String>();

        getFunction();
        getSparepart();

        if (funcAr != null && sympAr != null && spareAr != null) {
            setupSpinner();
        }

        txtEdcSN.setText(barcodeValue);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    private void setupSpinner() {
        spinFunc.setOnItemSelectedListener(this);
        spinSymp.setOnItemSelectedListener(this);
        spinSpare.setOnItemSelectedListener(this);

    }

    private void getFunction() {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<FunctionResponse> call = apiService.getAllFunction(BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<FunctionResponse>() {
            @Override
            public void onResponse(Call<FunctionResponse>call, Response<FunctionResponse> response) {
                dialog.dismiss();

                funcList = response.body().getResults();
//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "Data received: " + new Gson().toJson(funcList));

                if (response.code() == 200 && response.body().isStatus() == true){

                    if(funcList != null) {
                        for (int i = 0; i < funcList.size(); i++) {
                            funcAr.add(funcList.get(i).getFunction_code() + " - " + funcList.get(i).getFunction_description());
                        }
                        adapter = new ArrayAdapter<>(RepairingActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, funcAr);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinFunc.setAdapter(adapter);
                    }
                } else {
//                    Toast.makeText(RepairingActivity.this, "Function gagal", Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Function Tidak Ada");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<FunctionResponse>call, Throwable t) {
                dialog.dismiss();

                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
                //alertDialog.setIcon(R.drawable.tick);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void getSymptom(String func_code_bro) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<SymptomResponse> call = apiService.getAllSymptom(func_code_bro, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<SymptomResponse>() {
            @Override
            public void onResponse(Call<SymptomResponse>call, Response<SymptomResponse> response) {
                dialog.dismiss();

//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "Data received: " + new Gson().toJson(sympList));

                if (response.code() == 200 && response.body().isStatus() == true){
                    sympList = response.body().getResults();

                    if(sympList != null) {
                        for (int i = 0; i < response.body().getResults().size(); i++) {
                            sympAr.add(sympList.get(i).getSymptom_code()+" - "+sympList.get(i).getSymptom_description());
                        }
                        adapter2 = new ArrayAdapter<>(RepairingActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, sympAr);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinSymp.setAdapter(adapter2);
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Symptom Tidak Ada");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<SymptomResponse>call, Throwable t) {
                dialog.dismiss();

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
                //alertDialog.setIcon(R.drawable.tick);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });
    }

    private void getSparepart() {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<SparepartResponse> call = apiService.getAllSparepart(BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<SparepartResponse>() {
            @Override
            public void onResponse(Call<SparepartResponse>call, Response<SparepartResponse> response) {
                dialog.dismiss();

                spareList = response.body().getResults();
//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "Data of Events received: " + new Gson().toJson(spareList));

                if (response.code() == 200 && response.body().isStatus() == true){

                    if(spareList != null) {
                        for (int i = 0; i < spareList.size(); i++) {
                            spareAr.add(spareList.get(i).getSparepart_code() + " - " + spareList.get(i).getSparepart_description());
                        }

//                        adapter3 = new ArrayAdapter<>(RepairingActivity.this,
//                                android.R.layout.simple_spinner_dropdown_item, spareAr);
//                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinSpare.setAdapter(adapter3);

                        final List<String> list = spareAr;
                        TreeMap<String, Boolean> items = new TreeMap<>();
                        for(String item : list) {
                            items.put(item, Boolean.FALSE);
                        }

                        spinSpare.setItems(items, new MultiSpinner.MultiSpinnerListener() {

                            @Override
                            public void onItemsSelected(boolean[] selected) {
                                for (int i = 0; i < selected.length; i++) {
                                    if (selected[i]) {
//                                        Log.i("TAG", i + " : " + list.get(i));
                                    }
                                }
                            }
                        });
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Spareparts Tidak Ada");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<SparepartResponse>call, Throwable t) {
                dialog.dismiss();

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });
                alertDialog.show();
            }
        });
    }

//    private void startJob(String techCode, String termCode) {
//        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");
//
//        ApiService apiService =
//                RestApi.getClient().create(ApiService.class);
//
//        Call<Job> call = apiService.startJob(techCode, termCode, BuildConfig.INGENICO_API_KEY);
//        call.enqueue(new Callback<Job>() {
//            @Override
//            public void onResponse(Call<Job>call, Response<Job> response) {
//                dialog.dismiss();
//
//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "Data received: " + new Gson().toJson(response.body()));
//
//                if (response.code() == 200 && response.body().isStatus() == true){
//                    Toast.makeText(RepairingActivity.this, "Start Job Record", Toast.LENGTH_LONG).show();
//                    jobId = String.valueOf(response.body().getJob_id());
//                    sessionManager.createJobSession(jobId);
//                } else {
//                    Toast.makeText(RepairingActivity.this, "Start Job Failed / gagal / Tidak Berhasil", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Job>call, Throwable t) {
//                dialog.dismiss();
//
//                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
//                alertDialog.setTitle("Error");
//                alertDialog.setMessage("Kesalahan Jaringan");
//                //alertDialog.setIcon(R.drawable.tick);
//                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                        startActivity(getIntent());
//                    }
//                });
//                alertDialog.show();
//            }
//        });
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.spin_function:
                valueFunc = spinFunc.getSelectedItem().toString();
//                Log.e(TAG, valueFunc);

                testSubStr = valueFunc.substring(0,1);
//                Log.e(TAG, testSubStr);

                sympAr.clear();
                spinSymp.setAdapter(null);
                getSymptom(testSubStr);
                break;
            case R.id.spin_symptom:
                valueSymp = adapterView.getSelectedItem().toString();
//                Log.e(TAG, valueSymp);
                break;
            case R.id.spin_sparepart:
                valueSpare = spinSpare.getItemAtPosition(i).toString();
//                Log.e(TAG, valueSpare);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
