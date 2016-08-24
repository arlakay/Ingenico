package com.ingenicosms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.ingenicosms.adapter.ReviewJobAdapter;
import com.ingenicosms.api.RestApi;
import com.ingenicosms.api.services.ApiService;
import com.ingenicosms.model.ReviewJob;
import com.ingenicosms.model.ReviewJobResponse;
import com.ingenicosms.utility.SessionManager;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ILM on 7/29/2016.
 */
public class ReviewJobActivity extends BaseActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.recycler_reviewjob)RecyclerView recyclerView;
    private String TAG = ReviewJobActivity.class.getSimpleName();
    private List<ReviewJob> reviewJobList;
    private String techCode;
    private Boolean exit = false;
    private ReviewJobAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_job);
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
        Log.e(TAG, techCode);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        getHistoryTechnician();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    private void getHistoryTechnician() {
        final ProgressDialog dialog = ProgressDialog.show(ReviewJobActivity.this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<ReviewJobResponse> call = apiService.getAllHistory(techCode, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<ReviewJobResponse>() {
            @Override
            public void onResponse(Call<ReviewJobResponse>call, Response<ReviewJobResponse> response) {
                dialog.dismiss();

                reviewJobList = response.body().getResults();

//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "Data of Events received: " + new Gson().toJson(reviewJobList));

                    adapter = new ReviewJobAdapter(reviewJobList, R.layout.list_item_review_job, getApplicationContext(), new ReviewJobAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ReviewJob model) {
                            int jobId = model.getJob_id();
                            String tecCode =  model.getTechnician_code();
                            String startTime = model.getStart_time();
                            String finishTime = model.getFinish_time();
                            String terminalCode = model.getTerminal_code();
                            String func = model.getFunction_code();
                            String symp = model.getSymptom_code();

                            List<String> test = model.getSpareparts();
//                        Log.e("Var Woi : ", test.toString());
                            String spare_code = test.toString();

                            Intent intent = new Intent(ReviewJobActivity.this, DetailReviewJobActivity.class);
                            intent.putExtra("job_id", jobId);
                            intent.putExtra("tech_code", tecCode);
                            intent.putExtra("start_time", startTime);
                            intent.putExtra("finish_time", finishTime);
                            intent.putExtra("term_code", terminalCode);
                            intent.putExtra("func_code", func);
                            intent.putExtra("symp_code", symp);
                            intent.putExtra("spare_code", spare_code);

                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ReviewJobResponse>call, Throwable t) {
                dialog.dismiss();

                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(ReviewJobActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                        startActivity(getIntent());
                    }
                });
                alertDialog.show();
            }
        });
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
