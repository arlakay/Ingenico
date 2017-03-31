package com.mybottle.ingenicoversion2.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.mybottle.ingenicoversion2.BuildConfig;
import com.mybottle.ingenicoversion2.DetailReviewJobActivity;
import com.mybottle.ingenicoversion2.R;
import com.mybottle.ingenicoversion2.adapter.ReviewJobMainboardAdapter;
import com.mybottle.ingenicoversion2.api.RestApi;
import com.mybottle.ingenicoversion2.api.services.ApiService;
import com.mybottle.ingenicoversion2.model.ReviewJob;
import com.mybottle.ingenicoversion2.model.ReviewJobResponse;
import com.mybottle.ingenicoversion2.utility.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentHistoryMainboardYesterday extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler_review_mainboard_yesterday)RecyclerView recyclerViewReviewYesterday;
    @BindView(R.id.swipe_refresh_layout)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_list_kosong)TextView textListKosong;

    private static String TAG = FragmentHistoryMainboardYesterday.class.getSimpleName();
    private SessionManager sessionManager;
    private List<ReviewJob> reviewJobList = new ArrayList<>();
    private ReviewJobMainboardAdapter adapter;
    private String techCode;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static FragmentHistoryMainboardYesterday newInstance() {
        return new FragmentHistoryMainboardYesterday();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_history_mainboard_yesterday, container, false);
        ButterKnife.bind(this, rootView);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        sessionManager = new SessionManager(getActivity());
        sessionManager.checkLogin();

        if (!sessionManager.isLoggedIn()) {
            sessionManager.setLogin(false);
            sessionManager.logoutUser();
        }

        HashMap<String, String> user = sessionManager.getUserDetails();
        techCode = user.get(SessionManager.KEY_TECHNICIAN_CODE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerViewReviewYesterday.setLayoutManager(linearLayoutManager);
        recyclerViewReviewYesterday.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getHistoryTechnicianYesterday();
            }
        });


        return rootView;
    }

    @Override
    public void onRefresh() {
        if (reviewJobList != null) {
            reviewJobList.clear();
        }
        swipeRefreshLayout.setRefreshing(true);
        getHistoryTechnicianYesterday();
    }

    private void getHistoryTechnicianYesterday() {

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<ReviewJobResponse> call = apiService.getHistoryMBYesterday(techCode, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<ReviewJobResponse>() {
            @Override
            public void onResponse(Call<ReviewJobResponse>call, Response<ReviewJobResponse> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body().isStatus()) {
                    textListKosong.setVisibility(View.GONE);

                    reviewJobList = response.body().getResults();
                    Log.d(TAG, "Status Code = " + response.code());
                    Log.d(TAG, "Mainboard_Yesterday: " + new Gson().toJson(reviewJobList));

                    Bundle params = new Bundle();
                    params.putString("name", techCode);
                    mFirebaseAnalytics.logEvent("history_mainboard_yesterday", params);

                    adapter = new ReviewJobMainboardAdapter(reviewJobList, R.layout.list_item_review_job, getActivity(), new ReviewJobMainboardAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ReviewJob model) {
                            int jobCustomId = model.getJobcustom_id();
                            String techCode = model.getTechnician_code();
                            String startTime = model.getStart_time();
                            String finishTime = model.getFinish_time();
                            String itemCode = model.getItem_code();
                            String problemDesc = model.getProblem_description();
                            String resolutionDesc = model.getResolution_description();

                            Intent intent = new Intent(getActivity(), DetailReviewJobActivity.class);
                            intent.putExtra("type", "mainboard");
                            intent.putExtra("job_custom_id", jobCustomId);
                            intent.putExtra("tech_code", techCode);
                            intent.putExtra("start_time", startTime);
                            intent.putExtra("finish_time", finishTime);
                            intent.putExtra("item_code", itemCode);
                            intent.putExtra("problem_desc", problemDesc);
                            intent.putExtra("resolution_desc", resolutionDesc);

                            startActivity(intent);
                        }
                    });
                    recyclerViewReviewYesterday.setAdapter(adapter);
                }else {
//                    Toast.makeText(getActivity(), "Kosong", Toast.LENGTH_SHORT).show();
                    recyclerViewReviewYesterday.setVisibility(View.GONE);
                    textListKosong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ReviewJobResponse>call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

//                Log.e(TAG, t.toString());

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Kesalahan Jaringan");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                });
                alertDialog.show();
            }
        });
    }

}
