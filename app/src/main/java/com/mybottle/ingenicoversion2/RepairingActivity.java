package com.mybottle.ingenicoversion2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mybottle.ingenicoversion2.api.RestApi;
import com.mybottle.ingenicoversion2.api.services.ApiService;
import com.mybottle.ingenicoversion2.model.CloseReason;
import com.mybottle.ingenicoversion2.model.CloseReasonResponse;
import com.mybottle.ingenicoversion2.model.Customer;
import com.mybottle.ingenicoversion2.model.CustomerResponse;
import com.mybottle.ingenicoversion2.model.Function;
import com.mybottle.ingenicoversion2.model.FunctionResponse;
import com.mybottle.ingenicoversion2.model.Job;
import com.mybottle.ingenicoversion2.model.Resolution;
import com.mybottle.ingenicoversion2.model.ResolutionResponse;
import com.mybottle.ingenicoversion2.model.Sparepart;
import com.mybottle.ingenicoversion2.model.SparepartResponse;
import com.mybottle.ingenicoversion2.model.Symptom;
import com.mybottle.ingenicoversion2.model.SymptomResponse;
import com.mybottle.ingenicoversion2.utility.KeyPairBoolData;
import com.mybottle.ingenicoversion2.utility.MultiSpinner;
import com.mybottle.ingenicoversion2.utility.MultiSpinnerSearch;
import com.mybottle.ingenicoversion2.utility.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class RepairingActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, MultiSpinnerSearch.MultiSpinnerSearchListener {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.spin_function)Spinner spinFunc;
    @BindView(R.id.spin_symptom)Spinner spinSymp;
    @BindView(R.id.spin_category)Spinner spinCtgry;
    @BindView(R.id.spin_sparepart)MultiSpinner spinSpare;
    @BindView(R.id.spin_search_sparepart)MultiSpinnerSearch spinSearchSpare;
    @BindView(R.id.spin_customer)Spinner spinCstmr;
    @BindView(R.id.spin_resolution)MultiSpinner spinRsltn;
    @BindView(R.id.spin_search_resolution)MultiSpinnerSearch spinSearchRsltn;
    @BindView(R.id.spin_close_reason)Spinner spinClsReason;
    @BindView(R.id.txt_sn_edc)TextView txtEdcSN;
    @BindView(R.id.txt_repair_customer)TextView txtRepairCstmr;
    @BindView(R.id.loading)ProgressBar loadingView;

    private String TAG = RepairingActivity.class.getSimpleName();
    private SessionManager sessionManager;
    private String barcodeValue, techCode, valueFunc, valueSymp, valueCtgry, valueSpare, jobId,
            valueCstmr, valueRsltn, valueClsReason, jobidudahdiparse, brandCodeApi, testSubStr,
            startTime, brandCodeSP, currentDateTimeString;

    private List<Customer> custmrList;
    private List<Function> funcList;
    private List<Symptom> sympList;
    private List<Sparepart> spareList;
    private List<Resolution> resolutionList;
    private List<CloseReason> closeReasonList;

    private ArrayAdapter<String> adapter, adapter2, adapter3, adapterCustomer, adapterResolution, adapterCloseReason;
    private ArrayAdapter<CharSequence> adapterCat;

    private ArrayList<String> funcAr, sympAr, spareAr, custAr, resoAr, clsReasonAr;

    private StringBuffer spinnerBuffer, spinnerBufferRsltn, spinnerBufferClsReason;
    private final List<KeyPairBoolData> listArraySparepart = new ArrayList<>();
    private List<String> listSpare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        brandCodeSP = user.get(SessionManager.KEY_BRAND_CODE);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        startTime = String.valueOf(year+"-"+month+"-"+date+" "+hour +":"+ minute +":"+ second);
        sessionManager.createStartTimeSession(startTime);

        spinnerBuffer = new StringBuffer();
        spinnerBufferRsltn = new StringBuffer();

        funcAr = new ArrayList<String>();
        sympAr = new ArrayList<String>();
        spareAr = new ArrayList<String>();
        custAr = new ArrayList<String>();
        resoAr = new ArrayList<String>();
        clsReasonAr = new ArrayList<String>();

        startJob();
//        getFunction();
//        getCustomer();
//        getResolution();
//        getCloseReason();

        if (funcAr != null && sympAr != null && spareAr != null && custAr != null && resoAr != null && clsReasonAr != null) {
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
        adapterCat = ArrayAdapter.createFromResource(this, R.array.category,
                android.R.layout.simple_spinner_item);
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCtgry.setAdapter(adapterCat);
        spinCtgry.setOnItemSelectedListener(this);

        spinFunc.setOnItemSelectedListener(this);
        spinSymp.setOnItemSelectedListener(this);
        spinSearchSpare.setOnItemSelectedListener(this);
        spinCstmr.setOnItemSelectedListener(this);
        spinSearchRsltn.setOnItemSelectedListener(this);
        spinClsReason.setOnItemSelectedListener(this);
    }

    private void getCustomer() {
        loadingView.setVisibility(View.VISIBLE);

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<CustomerResponse> call = apiService.getAllCustomer(BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<CustomerResponse>() {
            @Override
            public void onResponse(Call<CustomerResponse>call, Response<CustomerResponse> response) {
                loadingView.setVisibility(View.GONE);

                custmrList = response.body().getResults();
                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "CloseReason received: " + new Gson().toJson(funcList));

                if (response.code() == 200 && response.body().isStatus() == true){

                    if(custmrList != null) {
                        for (int i = 0; i < custmrList.size(); i++) {
                            custAr.add(custmrList.get(i).getCustomer_code() + "-" + custmrList.get(i).getCustomer_description());
                        }
                        adapterCustomer = new ArrayAdapter<>(RepairingActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, custAr);
                        adapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinCstmr.setAdapter(adapterCustomer);
                    }
                } else {
//                    Toast.makeText(RepairingActivity.this, "Function gagal", Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Customer Tidak Ada");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<CustomerResponse>call, Throwable t) {
                loadingView.setVisibility(View.GONE);

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Network Error");
                alertDialog.setMessage("Failed to connect to the server");
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

    private void getFunction() {
        loadingView.setVisibility(View.VISIBLE);

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<FunctionResponse> call = apiService.getAllFunction(brandCodeSP, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<FunctionResponse>() {
            @Override
            public void onResponse(Call<FunctionResponse>call, Response<FunctionResponse> response) {
                loadingView.setVisibility(View.GONE);

                funcList = response.body().getResults();
                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "CloseReason received: " + new Gson().toJson(funcList));

                if (response.code() == 200 && response.body().isStatus() == true){

                    if(funcList != null) {
                        for (int i = 0; i < funcList.size(); i++) {
                            funcAr.add(funcList.get(i).getFunction_code() + "-" + funcList.get(i).getFunction_description());
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
                loadingView.setVisibility(View.GONE);

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan Func");
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
//        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");
        loadingView.setVisibility(View.VISIBLE);

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<SymptomResponse> call = apiService.getAllSymptom(func_code_bro, brandCodeSP, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<SymptomResponse>() {
            @Override
            public void onResponse(Call<SymptomResponse>call, Response<SymptomResponse> response) {
//                dialog.dismiss();
                loadingView.setVisibility(View.GONE);

                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "CloseReason received: " + new Gson().toJson(sympList));

                if (response.code() == 200 && response.body().isStatus() == true){
                    sympList = response.body().getResults();

                    if(sympList != null) {
                        for (int i = 0; i < response.body().getResults().size(); i++) {
                            sympAr.add(sympList.get(i).getSymptom_code()+"-"+sympList.get(i).getSymptom_description());
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
//                dialog.dismiss();
                loadingView.setVisibility(View.GONE);

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan Symp");
                //alertDialog.setIcon(R.drawable.tick);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });
    }

    private void getSparepartByCategory(String category) {
//        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");
        loadingView.setVisibility(View.VISIBLE);

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<SparepartResponse> call = apiService.getAllSparepartByCategory(category, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<SparepartResponse>() {
            @Override
            public void onResponse(Call<SparepartResponse>call, Response<SparepartResponse> response) {
//                dialog.dismiss();
                loadingView.setVisibility(View.GONE);

                if (response.code() == 200 && response.body().isStatus() == true){
                    spareList = response.body().getResults();
                    Log.d(TAG, "Status Code = " + response.code());
                    Log.e(TAG, "CloseReason of Spareparts received: " + new Gson().toJson(spareList));

                    spareAr.clear();

                    if(spareList != null) {
                        for (int i = 0; i < spareList.size(); i++) {
                            spareAr.add(spareList.get(i).getSparepart_code() + "-" + spareList.get(i).getSparepart_description());
                        }

                        //TODO MultiSpinner
//                        final List<String> list = spareAr;
//                        TreeMap<String, Boolean> items = new TreeMap<>();
//                        for(String item : list) {
//                            items.put(item, Boolean.FALSE);
//                        }
//
//                        spinSpare.setItems(items, new MultiSpinner.MultiSpinnerListener() {
//
//                            @Override
//                            public void onItemsSelected(boolean[] selected) {
//                                for (int i = 0; i < selected.length; i++) {
//                                    if (selected[i]) {
////                                        Log.i("TAG", i + " : " + list.get(i));
//                                    }
//                                }
//                            }
//                        });

                        //TODO MultiSpinner Search
                        listSpare = spareAr;
                        TreeMap<String, Boolean> items = new TreeMap<>();
                        for(String item : listSpare) {
                            items.put(item, Boolean.FALSE);
                        }

//                        listArraySparepart = new ArrayList<>();

                        listArraySparepart.clear();

                        for (int i = 0; i < listSpare.size(); i++) {
                            KeyPairBoolData h = new KeyPairBoolData();
                            h.setId(i + 1);
                            h.setName(listSpare.get(i));
                            h.setSelected(false);
                            listArraySparepart.add(h);
                        }

                        spinSearchSpare.setItems(listArraySparepart, "", -1, new MultiSpinnerSearch.MultiSpinnerSearchListener() {
                            @Override
                            public void onItemsSelected(List<KeyPairBoolData> items) {
                                spinnerBuffer = new StringBuffer();
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).isSelected()) {
                                        spinnerBuffer.append(items.get(i).getName());
                                        spinnerBuffer.append(";\n");
                                    }
                                }
                                valueSpare = spinnerBuffer.toString();
                                Log.e("TAG Sparepart : ", valueSpare);
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
//                dialog.dismiss();
                loadingView.setVisibility(View.GONE);

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan Spare");
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

    private void getResolution() {
        loadingView.setVisibility(View.VISIBLE);
        Log.e(TAG, "before call resolution:"+ brandCodeSP);

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<ResolutionResponse> call = apiService.getAllResolution(brandCodeSP, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<ResolutionResponse>() {
            @Override
            public void onResponse(Call<ResolutionResponse>call, Response<ResolutionResponse> response) {
                loadingView.setVisibility(View.GONE);

                resolutionList = response.body().getResults();
                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "CloseReason of Spareparts received: " + new Gson().toJson(spareList));

                if (response.code() == 200 && response.body().isStatus() == true){
                    resoAr.clear();
                    if(resolutionList != null) {
                        for (int i = 0; i < resolutionList.size(); i++) {
                            resoAr.add(resolutionList.get(i).getResolution_code() + "-" + resolutionList.get(i).getResolution_description());
                        }

                        //TODO Optional
//                        adapter3 = new ArrayAdapter<>(RepairingActivity.this,
//                                android.R.layout.simple_spinner_dropdown_item, spareAr);
//                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinSpare.setAdapter(adapter3);

                        //TODO MultiSpinner
//                        final List<String> list = resoAr;
//                        TreeMap<String, Boolean> items = new TreeMap<>();
//                        for(String item : list) {
//                            items.put(item, Boolean.FALSE);
//                        }
//
//                        spinRsltn.setItems(items, new MultiSpinner.MultiSpinnerListener() {
//
//                            @Override
//                            public void onItemsSelected(boolean[] selected) {
//                                for (int i = 0; i < selected.length; i++) {
//                                    if (selected[i]) {
////                                        Log.i("TAG", i + " : " + list.get(i));
//                                    }
//                                }
//                            }
//                        });

                        //TODO MultiSpinner Search
                        /**
                         * Getting array of String to Bind in Spinner
                         */
                        final List<String> list = resoAr;

                        /**
                         * Search MultiSelection Spinner (With Search/Filter Functionality)
                         *  Using MultiSpinnerSearch class
                         */
                        final List<KeyPairBoolData> listArrayResolution = new ArrayList<>();

                        for (int i = 0; i < list.size(); i++) {
                            KeyPairBoolData h = new KeyPairBoolData();
                            h.setId(i + 1);
                            h.setName(list.get(i));
                            h.setSelected(false);
                            listArrayResolution.add(h);
                        }

                        /**
                         * -1 is no by default selection
                         * 0 to length will select corresponding values
                         */
                        spinSearchRsltn.setItems(listArrayResolution, "", -1, new MultiSpinnerSearch.MultiSpinnerSearchListener() {

                            @Override
                            public void onItemsSelected(List<KeyPairBoolData> items) {
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).isSelected()) {
                                        spinnerBufferRsltn.append(items.get(i).getName());
                                        spinnerBufferRsltn.append(";\n");
                                    }
                                }
                                valueRsltn = spinnerBuffer.toString();
                                Log.e("TAG Sparepart : ", valueRsltn);
                            }
                        });

                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Resolution Tidak Ada");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResolutionResponse>call, Throwable t) {
                loadingView.setVisibility(View.GONE);

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Network Error");
                alertDialog.setMessage("Kesalahan Jaringan Resolution \n" + t.getMessage());
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

    private void getCloseReason() {
        loadingView.setVisibility(View.VISIBLE);

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<CloseReasonResponse> call = apiService.getAllCloseReason(brandCodeSP, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<CloseReasonResponse>() {
            @Override
            public void onResponse(Call<CloseReasonResponse>call, Response<CloseReasonResponse> response) {
                loadingView.setVisibility(View.GONE);

                closeReasonList = response.body().getData();
                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "CloseReason of Spareparts received: " + new Gson().toJson(spareList));

                if (response.code() == 200 && response.body().getStatus() == true){
                    if(closeReasonList != null) {
                        for (int i = 0; i < closeReasonList.size(); i++) {
                            clsReasonAr.add(closeReasonList.get(i).getClosereason_code() + "-" + closeReasonList.get(i).getClosereason_description());
                        }
                        adapterCloseReason = new ArrayAdapter<>(RepairingActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, clsReasonAr);
                        adapterCloseReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinClsReason.setAdapter(adapterCloseReason);
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Close Reason Tidak Ada");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<CloseReasonResponse>call, Throwable t) {
                loadingView.setVisibility(View.GONE);

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Network Error");
                alertDialog.setMessage("404 Close Reason");
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

    private void startJob(){
//        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");
        loadingView.setVisibility(View.VISIBLE);

        ApiService apiService = RestApi.getClient().create(ApiService.class);

        Call<Job> call = apiService.startJob(techCode, barcodeValue, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job>call, Response<Job> response) {
//                dialog.dismiss();
                loadingView.setVisibility(View.GONE);

                Log.d(TAG, "Status Code = " + response.code());
                Log.d(TAG, "Start Job received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().isStatus() ) {
                    int jobIdJson = response.body().getJob_id();
                    jobidudahdiparse = String.valueOf(jobIdJson);
                    sessionManager.createJobIdSession(jobidudahdiparse);

                    brandCodeApi = response.body().getBrandCode();
                    Log.e(TAG, brandCodeApi);
                    sessionManager.createBrandSession(brandCodeApi);

                    HashMap<String, String> user = sessionManager.getUserDetails();
                    brandCodeSP = user.get(SessionManager.KEY_BRAND_CODE);

                    valueCstmr = response.body().getCustomerDescription().getCustomerCode()
                            + "-" + response.body().getCustomerDescription().getCustomerDescription();

                    txtRepairCstmr.setText(valueCstmr);

                    getCloseReason();
                    getFunction();
                    getResolution();

                    currentDateTimeString = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

                    Bundle params = new Bundle();
                    params.putString("technician_id", techCode);
                    params.putString("edc_barcode", barcodeValue);
                    params.putString("edc_start_time", currentDateTimeString);
                    mFirebaseAnalytics.logEvent("start_repair_edc", params);

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                    alertDialog.setTitle("Warning!");
                    alertDialog.setMessage("Product Code Not Found in Job Order");
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
//                dialog.dismiss();
                loadingView.setVisibility(View.GONE);

                AlertDialog alertDialog = new AlertDialog.Builder(RepairingActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Product Code Not Found in Job Order");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });
    }

    @OnClick(R.id.btn_edit_edc_sn)
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
                sessionManager.createBarcodeSession(value);
                onResume();
                startJob();
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

    @OnClick(R.id.btn_finish_repairing)
    public void finishRepair() {
        if (barcodeValue.trim().length() > 0 && valueCstmr.trim().length() > 0 && valueFunc.trim().length() > 0
                && valueSymp.trim().length() > 0 && valueRsltn.trim().length() > 0) {

            Intent i = new Intent(RepairingActivity.this, ClosingActivity.class);
            i.putExtra("edc_sn", barcodeValue);
            i.putExtra("func", valueFunc);
            i.putExtra("symp", valueSymp);
            i.putExtra("spare", valueSpare);
            i.putExtra("job_id", jobId);
            i.putExtra("cstmr", valueCstmr);
            i.putExtra("rsltn", valueRsltn);
            i.putExtra("clsReason", valueClsReason);

            startActivity(i);
            finish();
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Resolution tidak boleh kosong!", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.spin_function:
                valueFunc = spinFunc.getSelectedItem().toString();

                String[] parts = valueFunc.split("\\-"); // explode di android .
                String part1 = parts[0];
                String part2 = parts[1];

                testSubStr = part1;

                sympAr.clear();
                spinSymp.setAdapter(null);
                getSymptom(testSubStr);
                break;
            case R.id.spin_symptom:
                valueSymp = adapterView.getSelectedItem().toString();
                break;
            case R.id.spin_category:
                valueCtgry = adapterView.getSelectedItem().toString();
                spareAr.clear();
                getSparepartByCategory(valueCtgry);
                break;
            case R.id.spin_customer:
                valueCstmr = adapterView.getSelectedItem().toString();
                break;
            case R.id.spin_search_sparepart:
                valueSpare = spinnerBuffer.toString();
                if (valueSpare.length() > 2) {
                    valueSpare = valueSpare.substring(0, valueSpare.length() - 2);
                } else {
                    valueSpare = "";
                }
                Log.e(TAG, valueSpare);
                break;
            case R.id.spin_search_resolution:
//                valueRsltn = spinSearchRsltn.getItemAtPosition(i).toString();
//                valueRsltn = spinSearchRsltn.getSelectedItem().toString();

                valueRsltn = spinnerBufferRsltn.toString();
                if (valueRsltn.length() > 2) {
                    valueRsltn = valueRsltn.substring(0, valueRsltn.length() - 2);
                } else {
                    valueRsltn = "";
                }
                Log.i(TAG, "errornya dimana sih:"+ valueRsltn);
                break;
            case R.id.spin_close_reason:
                valueClsReason = adapterView.getSelectedItem().toString();
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                valueFunc = "";
                valueSymp = "";
                valueSpare = "";
                valueCstmr = "";
                valueRsltn = "";
                valueClsReason = "";
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        HashMap<String, String> user = sessionManager.getUserDetails();
        barcodeValue = user.get(SessionManager.KEY_BARCODE);

        txtEdcSN.invalidate();
        txtEdcSN.setText(barcodeValue);
    }

    @Override
    public void onItemsSelected(List<KeyPairBoolData> items) {

    }

}
