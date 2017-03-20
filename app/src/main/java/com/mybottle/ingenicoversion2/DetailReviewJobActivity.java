package com.mybottle.ingenicoversion2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mybottle.ingenicoversion2.api.RestApi;
import com.mybottle.ingenicoversion2.api.services.ApiService;
import com.mybottle.ingenicoversion2.model.Function;
import com.mybottle.ingenicoversion2.model.FunctionResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * Created by ILM on 8/1/2016.
 */
public class DetailReviewJobActivity extends BaseActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.txt_detail_job_sn)TextView txtSN;
    @BindView(R.id.txt_detail_customer_code)TextView txtCust;
    @BindView(R.id.txt_detail_job_function)TextView txtFunc;
    @BindView(R.id.txt_detail_job_symptom)TextView txtSymp;
    @BindView(R.id.txt_detail_job_sparepart)TextView txtSpare;
    @BindView(R.id.txt_detail_resolution_code)TextView txtRsltn;
    @BindView(R.id.loading)ProgressBar loadingView;
    @BindView(R.id.layout_edc)LinearLayout layoutEdc;
    @BindView(R.id.layout_motherboard)LinearLayout layoutMainboard;
    @BindView(R.id.txt_part_number)TextView txtItemCode;
    @BindView(R.id.txt_problem_description)TextView txtProblem;
    @BindView(R.id.txt_resolution_description)TextView txtResolution;

    private String sn, func, symp, spare, cust, rsltn, type, jobCustomId, itemCode, problemDesc, resolutionDesc;
    private String[] separated;
    private List<Function> funcList;
    private ArrayList<String> funcAr, sympAr, spareAr, custAr, resoAr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_job_detail);
        ButterKnife.bind(this);

        setupToolbar();
        getData();

        if(type.equalsIgnoreCase("edc")){
            layoutEdc.setVisibility(View.VISIBLE);
            layoutMainboard.setVisibility(GONE);

            txtSN.setText(sn);
            txtCust.setText(cust);
            txtFunc.setText(func);
            txtSymp.setText(symp);
            txtSpare.setText(spare);
            txtRsltn.setText(rsltn);

            loadingView.setVisibility(GONE);
        }else{
            layoutMainboard.setVisibility(View.VISIBLE);
            layoutEdc.setVisibility(GONE);

            txtItemCode.setText(itemCode);
            txtProblem.setText(problemDesc);
            txtResolution.setText(resolutionDesc);

            loadingView.setVisibility(GONE);
        }
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
        type = i.getStringExtra("type");
        cust = i.getStringExtra("cust_code");
        sn = i.getStringExtra("term_code");
        func = i.getStringExtra("func_code");
        symp = i.getStringExtra("symp_code");
        spare = i.getStringExtra("spare_code");
        rsltn = i.getStringExtra("resol_code");

        jobCustomId = i.getStringExtra("job_custom_id");
        itemCode = i.getStringExtra("item_code");
        problemDesc = i.getStringExtra("problem_desc");
        resolutionDesc = i.getStringExtra("resolution_desc");
    }

    @OnClick(R.id.btn_close_reviewjob_detail)
    public void closeDetailJob(Button btnClose) {
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    private void getFunctionDetailByCode(String functionCode) {
        loadingView.setVisibility(View.VISIBLE);

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<FunctionResponse> call = apiService.getFunctionDetailByCode(functionCode, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<FunctionResponse>() {
            @Override
            public void onResponse(Call<FunctionResponse>call, Response<FunctionResponse> response) {
                loadingView.setVisibility(GONE);

                funcList = response.body().getResults();
                Log.e("TAG", "Status Code = " + response.code());
                Log.e("TAG", "CloseReason received: " + new Gson().toJson(funcList));

                if (response.code() == 200 && response.body().isStatus() == true){

                    if(funcList != null) {
                        for (int i = 0; i < funcList.size(); i++) {
                            funcAr.add(funcList.get(i).getFunction_code() + "-" + funcList.get(i).getFunction_description());
                        }
//                        adapter = new ArrayAdapter<>(DetailReviewJobActivity.this,
//                                android.R.layout.simple_spinner_dropdown_item, funcAr);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        spinFunc.setAdapter(adapter);
//                        txtFunc.setText((CharSequence) funcAr);
                    }
                } else {
//                    Toast.makeText(RepairingActivity.this, "Function gagal", Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog = new AlertDialog.Builder(DetailReviewJobActivity.this).create();
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
                loadingView.setVisibility(GONE);

//                Log.e("TAG", t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(DetailReviewJobActivity.this).create();
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

}
