package com.ingenicosms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ingenicosms.api.RestApi;
import com.ingenicosms.api.services.ApiService;
import com.ingenicosms.model.Technician;
import com.ingenicosms.utility.SessionManager;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ILM on 7/25/2016.
 */

public class MenuHomeActivity extends BaseActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
    private SessionManager sessionManager;
    private String attId, tecId, passw;
    private Boolean exit = false;
//    private String TAG = MenuHomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_home);
        ButterKnife.bind(this);

        setupToolbar();

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        if (!sessionManager.isLoggedIn()) {
            sessionManager.setLogin(false);
            sessionManager.logoutUser();
        }

        HashMap<String, String> user = sessionManager.getUserDetails();
        attId = user.get(SessionManager.KEY_ATTENDANCE_ID);
        tecId = user.get(SessionManager.KEY_TECHNICIAN_CODE);
        passw = user.get(SessionManager.KEY_PASSWORD);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() == null) {
            throw new IllegalStateException("Activity must implement toolbar");
        }
    }

    private void techLogout(String techCode, String atteId, String pass) {

        final ProgressDialog dialog = ProgressDialog.show(this, "", "loading...");

        ApiService apiService =
                RestApi.getClient().create(ApiService.class);

        Call<Technician> call = apiService.logoutTechnician(techCode, atteId, pass, BuildConfig.INGENICO_API_KEY);
        call.enqueue(new Callback<Technician>() {
            @Override
            public void onResponse(Call<Technician>call, Response<Technician> response) {
                dialog.dismiss();

                if (response.code() == 200 && response.body().isStatus() == true &&
                        response.body().getLogout().equalsIgnoreCase("success")) {
                    if (sessionManager.isLoggedIn()) {
//                        sessionManager.logoutUser();
                        sessionManager.setLogin(false);
                    }
                    // Launching the login activity
                    Intent intent = new Intent(MenuHomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MenuHomeActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Maaf, Log Out Gagal");
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

                AlertDialog alertDialog = new AlertDialog.Builder(MenuHomeActivity.this).create();
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

    @OnClick(R.id.btn_start_job)
    public void startJob(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Posisikan Barcode dalam kotak diatas");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @OnClick(R.id.btn_review_job)
    public void reviewJob(ImageButton buttonReviewJob) {
        Intent i = new Intent(MenuHomeActivity.this, ReviewJobActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                techLogout(tecId, attId, passw);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
//                Log.d("MainActivity", "Cancelled scan");
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String ersa = result.getContents();
//                Log.e("data matrix : ", result.getContents());
//                Log.e("String Length : ", String.valueOf(ersa.length()));

                int panjangBarcode = ersa.length();

                if(panjangBarcode >= 24) {
                    sessionManager.setLogin(true);
                    sessionManager.createBarcodeSession(ersa);
                    Intent i = new Intent(MenuHomeActivity.this, RepairingActivity.class);
                    startActivity(i);
                } else {
                    IntentIntegrator integrator = new IntentIntegrator(this);
                    integrator.setPrompt("Posisikan Barcode dalam kotak diatas");
                    integrator.setOrientationLocked(false);
                    integrator.initiateScan();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.d("LIFECYCLE =====>", "RESUMED");
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d("LIFECYCLE =====>", "STARTED");
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d("LIFECYCLE =====>", "PAUSED");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d("LIFECYCLE =====>", "RESTARTED");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d("LIFECYCLE =====>", "STOPED");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d("LIFECYCLE =====>", "DESTROYED");
//    }

}
