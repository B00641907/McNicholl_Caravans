package com.example.b00641907.mcnicholl_caravans;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CustomerHomeActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        findViewById(R.id.btnAboutUs).setOnClickListener(this);
        findViewById(R.id.btnOrderGas).setOnClickListener(this);
        findViewById(R.id.btnViewCaravans).setOnClickListener(this);
        findViewById(R.id.btnEvents).setOnClickListener(this);
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

        startActivity(new Intent(CustomerHomeActivity.this, ParksActivity.class));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnAboutUs) {
            Intent openMap = new Intent(CustomerHomeActivity.this, MapsActivity.class);
            startActivity(openMap);
        } else if (viewId == R.id.btnOrderGas) {
            startActivity(new Intent(CustomerHomeActivity.this, CustomerGasOrderActivity.class));
        } else if (viewId == R.id.btnViewCaravans) {
            startActivity(new Intent(CustomerHomeActivity.this, CustomerCaravansActivity.class));
        } else if (viewId == R.id.btnEvents) {
            startActivity(new Intent(CustomerHomeActivity.this, CustomerEventActivity.class));
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
