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

import com.google.gson.Gson;
import com.mybottle.ingenicoversion2.BuildConfig;
import com.mybottle.ingenicoversion2.DetailReviewJobActivity;
import com.mybottle.ingenicoversion2.R;
import com.mybottle.ingenicoversion2.adapter.ReviewJobEDCAdapter;
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


public class FragmentHistoryEDCYesterday extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler_review_edc_yesterday)RecyclerView recyclerViewReviewYesterday;
    @BindView(R.id.swipe_refresh_layout)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_list_kosong)TextView textListKosong;

    private static String TAG = FragmentHistoryEDCYesterday.class.getSimpleName();
    private SessionManager sessionManager;
    private List<ReviewJob> reviewJobList = new ArrayList<>();
    private ReviewJobEDCAdapter adapter;
    private String techCode;

    public static FragmentHistoryEDCYesterday newInstance() {
        return new FragmentHistoryEDCYesterday();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_history_edc_yesterday, container, false);
        ButterKnife.bind(this, rootView);

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

        Call<ReviewJobResponse> call = apiService.getHistoryEDCYesterday(techCode, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<ReviewJobResponse>() {
            @Override
            public void onResponse(Call<ReviewJobResponse>call, Response<ReviewJobResponse> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body().isStatus()) {
                    textListKosong.setVisibility(View.GONE);

                    reviewJobList = response.body().getResults();
                    Log.d(TAG, "Status Code = " + response.code());
                    Log.d(TAG, "EDC_YESTERDAY: " + new Gson().toJson(reviewJobList));
                    adapter = new ReviewJobEDCAdapter(reviewJobList, R.layout.list_item_review_job,
                            getActivity(), new ReviewJobEDCAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ReviewJob model) {
                            int jobId = model.getJob_id();
                            String tecCode = model.getTechnician_code();
                            String startTime = model.getStart_time();
                            String finishTime = model.getFinish_time();
                            String customerCode = model.getCustomer_code();
                            String terminalCode = model.getTerminal_code();
                            String func = model.getFunction_code();
                            String symp = model.getSymptom_code();

                            List<String> test = model.getSpareparts();
//                            Log.e("Var Woi : ", test.toString());
                            String spare_code = test.toString();

                            List<String> resolution = model.getResolutions();
//                            Log.e("Var Woi : ", resolution.toString());
                            String resol_code = resolution.toString();

                            Intent intent = new Intent(getActivity(), DetailReviewJobActivity.class);
                            intent.putExtra("type", "edc");
                            intent.putExtra("job_id", jobId);
                            intent.putExtra("tech_code", tecCode);
                            intent.putExtra("start_time", startTime);
                            intent.putExtra("finish_time", finishTime);
                            intent.putExtra("cust_code", customerCode);
                            intent.putExtra("term_code", terminalCode);
                            intent.putExtra("func_code", func);
                            intent.putExtra("symp_code", symp);
                            intent.putExtra("spare_code", spare_code);
                            intent.putExtra("resol_code", resol_code);

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
