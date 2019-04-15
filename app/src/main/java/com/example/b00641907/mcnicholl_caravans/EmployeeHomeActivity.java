package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class EmployeeHomeActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnAboutUs).setOnClickListener(this);
        findViewById(R.id.btnOrderGas).setOnClickListener(this);
        findViewById(R.id.btnViewCaravans).setOnClickListener(this);
        findViewById(R.id.btnEvents).setOnClickListener(this);

        findViewById(R.id.btnBarcodeReader).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.SignOutMenu:
                Signout();
                return true;
            case R.id.ParksMenu:
                Parks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Parks() {

        startActivity(new Intent(EmployeeHomeActivity.this, ParksActivity.class));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnAboutUs) {
            Intent openMap = new Intent(EmployeeHomeActivity.this, MapsActivity.class);
            EmployeeHomeActivity.this.startActivity(openMap);
        } else if (viewId == R.id.btnOrderGas) {
            startActivity(new Intent(EmployeeHomeActivity.this, EmployeeGasActivity.class));
        } else if (viewId == R.id.btnViewCaravans) {
            startActivity(new Intent(EmployeeHomeActivity.this, EmployeeCaravansActivity.class));
        } else if (viewId == R.id.btnEvents) {
            startActivity(new Intent(EmployeeHomeActivity.this, EmployeeEventActivity.class));
        } else if (viewId == R.id.btnBarcodeReader) {
            if (checkPermissions(this, PERMISSION_REQUEST_CAMERA_STRING, false, PERMISSION_REQUEST_CODE_CAMERA)) {
                startActivity(new Intent(EmployeeHomeActivity.this, FullScannerActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (requestCode == PERMISSION_REQUEST_CODE_CAMERA && bAllGranted) {
            startActivity(new Intent(EmployeeHomeActivity.this, FullScannerActivity.class));
        }
    }

    boolean isFinish = false;
    class FinishTimer extends CountDownTimer {
        public FinishTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            isFinish = true;
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            isFinish = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFinish) {
            showToastMessage(R.string.finish_message);
            FinishTimer timer = new FinishTimer(2000, 1);
            timer.start();
        } else {
            finish();
        }
    }
}
