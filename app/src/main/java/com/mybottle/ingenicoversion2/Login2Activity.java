package com.mybottle.ingenicoversion2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mybottle.ingenicoversion2.api.RestApi;
import com.mybottle.ingenicoversion2.api.services.ApiService;
import com.mybottle.ingenicoversion2.model.Technician;
import com.mybottle.ingenicoversion2.utility.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ILM on 7/25/2016.
 */
public class Login2Activity extends BaseActivity {
    @BindView(R.id.btn_login)Button login;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.et_user_id)EditText etTechId;
    @BindView(R.id.et_password)EditText etPass;
    @BindView(R.id.txt_versioning)TextView versioning;

    private String techId, pass;
    private SessionManager sessionManager;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            Intent i = new Intent(this, MenuHomeActivity.class);
            startActivity(i);
            finish();
        }

        setupToolbar();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        versioning.setText("v "+version);
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
//                Log.d(TAG, "CloseReason received: " + new Gson().toJson(response.body()));

                if (response.code() == 200 && response.body().isStatus() == true) {
                    String tCode = response.body().getTechnician_code();
                    String attId = response.body().getAttendance_id();
                    String fName = response.body().getFirst_name();
                    String lName = response.body().getLast_name();
                    String passw = response.body().getPassword();

                    sessionManager.setLogin(true);
                    sessionManager.createLoginSession(attId, tCode, fName, lName, passw);

                    Intent intent = new Intent(Login2Activity.this, MenuHomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Login2Activity.this).create();
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

                AlertDialog alertDialog = new AlertDialog.Builder(Login2Activity.this).create();
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

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }



}
