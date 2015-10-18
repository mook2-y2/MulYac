package com.mulyac.mulyac_android_client.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.mulyac.mulyac_android_client.R;


public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 2000;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.start();
    }

    private void start() {
        mHandler = new Handler();
        mRunnable =  new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

        };
        mHandler.postDelayed(mRunnable, SPLASH_DISPLAY_LENGHT);
    }

}
