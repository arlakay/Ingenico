package com.ingenicosms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ILM on 8/1/2016.
 */
public class DetailReviewJobActivity extends BaseActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.txt_detail_job_sn)TextView txtSN;
    @BindView(R.id.txt_detail_job_function)TextView txtFunc;
    @BindView(R.id.txt_detail_job_symptom)TextView txtSymp;
    @BindView(R.id.txt_detail_job_sparepart)TextView txtSpare;

    private  String sn, func, symp, spare;
    private String[] separated;
    private Boolean exit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_job_detail);
        ButterKnife.bind(this);

        setupToolbar();
        getData();

        if(spare.length() == 1 || spare == null) {
//
        }else{
//            String kok = spare.toString().replace("[", "").replace("]", "");
//            separated = kok.split("[,]");
//            Log.e("TAGING", new Gson().toJson(separated));
//            separated[0] = separated[0].trim();
//            separated[1] = separated[1].trim();
        }

        txtSN.setText(sn);
        txtFunc.setText(func);
        txtSymp.setText(symp);
        txtSpare.setText(spare);
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
        sn = i.getStringExtra("term_code");
        func = i.getStringExtra("func_code");
        symp = i.getStringExtra("symp_code");
        spare = i.getStringExtra("spare_code");
    }

    @OnClick(R.id.btn_close_reviewjob_detail)
    public void closeDetailJob(Button btnClose) {
        finish();
    }

    @Override
    public void onBackPressed() {
//        if (exit) {
//            finish();
//        } else {
//            Toast.makeText(this, "Press Back again to Exit.",
//                    Toast.LENGTH_SHORT).show();
//            exit = true;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    exit = false;
//                }
//            }, 3 * 1000);
//        }
    }

}
