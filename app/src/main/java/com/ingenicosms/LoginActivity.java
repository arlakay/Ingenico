package com.ingenicosms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.ingenicosms.api.RestApi;
import com.ingenicosms.api.services.ApiService;
import com.ingenicosms.model.Technician;
import com.ingenicosms.utility.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ILM on 7/25/2016.
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.btn_login)Button login;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.et_user_id)EditText etTechId;
    @BindView(R.id.et_password)EditText etPass;

    private String techId, pass;
    private SessionManager sessionManager;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        setupToolbar();

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    @OnClick(R.id.btn_login)
    public void loginAuth() {
        techId = etTechId.getText().toString();
        pass = etPass.getText().toString();

        memberAuth(techId, pass);
    }

    private void memberAuth(String techCode, final String pass) {

        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<Technician> call = apiService.loginTechnician(techCode, pass, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<Technician>() {
            @Override
            public void onResponse(Call<Technician>call, Response<Technician> response) {
                dialog.dismiss();

//                Log.d(TAG, "Status Code = " + response.code());
//                Log.d(TAG, "Data received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().isStatus() == true) {
                    String tCode = response.body().getTechnician_code();
                    String attId = response.body().getAttendance_id();
                    String fName = response.body().getFirst_name();
                    String lName = response.body().getLast_name();
                    String passw = response.body().getPassword();

                    sessionManager.setLogin(true);
                    sessionManager.createLoginSession(attId, tCode, fName, lName, passw);

                    Intent intent = new Intent(LoginActivity.this, MenuHomeActivity.class);
//                    intent.putExtra("tech_code", tCode);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Login Failed");
                    alertDialog.setMessage("Technician Id and Password Not Match");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<Technician>call, Throwable t) {
                dialog.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Network Error");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();

            }
        });
    }

//    @Override
//    public void onBackPressed() {
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        handler.removeCallbacks(thread);
////        handler.postDelayed(thread, 0);
//        Log.d("LOGIN_LIFECYCLE =====>", "RESUMED");
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d("LOGIN_LIFECYCLE =====>", "STARTED");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
////        handler.removeCallbacks(thread);
//        Log.d("LOGIN_LIFECYCLE =====>", "PAUSED");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d("LOGIN_LIFECYCLE =====>", "RESTARTED");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d("LOGIN_LIFECYCLE =====>", "STOPED");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d("LOGIN_LIFECYCLE =====>", "DESTROYED");
//    }

}
