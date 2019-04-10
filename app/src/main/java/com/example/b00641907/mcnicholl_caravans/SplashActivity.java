package com.example.b00641907.mcnicholl_caravans;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends BaseActivity {

    private long showStartTime;
    private final static long DELAY_TIME = 1500;
    private boolean isRunning;

    ImageView ivLogo;
    TextView tvSlogon;
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        tvSlogon = (TextView) findViewById(R.id.tvSlogon);
        tvVersion = (TextView) findViewById(R.id.tvVersion);

        startAnimation();
        startSplash();
    }

    private void startAnimation() {
        ivLogo.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ivLogo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(ivLogo, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(ivLogo, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(ivLogo, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(1500);
                        mAnimatorSet.start();
                    }
                });

        tvSlogon.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        tvSlogon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(tvSlogon, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(tvSlogon, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(tvSlogon, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(1500);
                        mAnimatorSet.start();
                    }
                });

        tvVersion.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        tvVersion.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(tvVersion, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(tvVersion, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(tvVersion, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(1500);
                        mAnimatorSet.start();
                    }
                });

    }

    private void startSplash() {

        showStartTime = System.currentTimeMillis();
        isRunning = true;

        Thread background = new Thread() {
            public void run() {
                try {
                    // Delay Time
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - showStartTime < DELAY_TIME) {
                        try {
                            // Delay for DELAY_TIME
                            Thread.sleep(showStartTime + DELAY_TIME
                                    - currentTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                } catch (Exception e) {
                    return;
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            doFinish();
                        }
                    });
                }
            }
        };

        background.start();
    }

    private void doFinish() {
        if (this.isRunning) {

            // Check Permissions
            if (checkPermissions(mContext, PERMISSION_REQUEST_LOCATION_STRING, true, PERMISSION_REQUEST_CODE_LOCATION)) {
                checkToken();
            }
        }
    }

    private void checkToken() {



        startActivity(new Intent(mContext, SignInActivity.class));
        //startActivity(new Intent(mContext, MainActivity.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (this.isRunning) {
            this.isRunning = false;
        }
        finish();
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

        if (bAllGranted) {
            checkToken();
        } else {
            showAlert(R.string.request_permission_hint);
        }
    }
}
