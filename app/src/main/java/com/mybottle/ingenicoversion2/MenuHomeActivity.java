package com.mybottle.ingenicoversion2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mybottle.ingenicoversion2.api.RestApi;
import com.mybottle.ingenicoversion2.api.services.ApiService;
import com.mybottle.ingenicoversion2.model.Technician;
import com.mybottle.ingenicoversion2.ui.motherboard.MotherboardActivity;
import com.mybottle.ingenicoversion2.utility.SessionManager;

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
    @BindView(R.id.txt_versioning)TextView versioning;

    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private SessionManager sessionManager;
    private String attId, tecId, passw;
    private Boolean exit = false;
    private String TAG = MenuHomeActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_home);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        if (!sessionManager.isLoggedIn()) {
            sessionManager.setLogin(false);
            sessionManager.logoutUser();
            finish();
        }

        HashMap<String, String> user = sessionManager.getUserDetails();
        attId = user.get(SessionManager.KEY_ATTENDANCE_ID);
        tecId = user.get(SessionManager.KEY_TECHNICIAN_CODE);
        passw = user.get(SessionManager.KEY_PASSWORD);

        Bundle params = new Bundle();
        params.putString("name", tecId);
        mFirebaseAnalytics.logEvent("main_menu", params);

        mFirebaseAnalytics.setUserProperty("home", tecId);

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
        getSupportActionBar().setTitle(tecId);

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
                        sessionManager.setLogin(false);
                        sessionManager.logoutUser();
                        finish();
                    }
                    // Launching the login activity
//                    Intent intent = new Intent(MenuHomeActivity.this, Login2Activity.class);
//                    startActivity(intent);
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
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");

            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Posisikan Barcode dalam kotak diatas");
            integrator.setOrientationLocked(false);
            integrator.setBarcodeImageEnabled(true);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        }
    }

    @OnClick(R.id.btn_review_job)
    public void reviewJob(ImageButton buttonReviewJob) {
        Intent i = new Intent(MenuHomeActivity.this, ScrollableTabsReviewJobActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.btn_motherboard)
    public void motherboard(View view) {
        Intent i = new Intent(MenuHomeActivity.this, MotherboardActivity.class);
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
                Log.d("MainActivity", "Cancelled scan");
//                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String ersa = result.getContents();
                Log.e("data matrix : ", result.getContents());
                Log.e("String Length : ", String.valueOf(ersa.length()));

                int panjangBarcode = ersa.length();

                sessionManager.setLogin(true);
                sessionManager.createBarcodeSession(ersa);
                Intent i = new Intent(MenuHomeActivity.this, RepairingActivity.class);
                startActivity(i);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
//            Snackbar.make(mLayout, R.string.permission_camera_rationale,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MenuHomeActivity.this,
                                    new String[]{android.Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
//                        }
//                    })
//                    .show();
        } else {
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
        // END_INCLUDE(camera_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
            }
            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {

    }

}
