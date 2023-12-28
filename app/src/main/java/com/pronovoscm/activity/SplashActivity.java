package com.pronovoscm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pronovoscm.R;
import com.pronovoscm.utils.SharedPref;


public class SplashActivity extends AppCompatActivity {

    private Handler mWaitHandler = new Handler();
    private int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_view);
        splashVisibilitySession();
          }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Remove all the callbacks otherwise navigation will execute even after activity is killed or closed.
        mWaitHandler.removeCallbacksAndMessages(null);
    }


    public void splashVisibilitySession() {
        mWaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    moveToNextScreen();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, SPLASH_TIME);  // Give a 3 seconds delay.

    }


    private void moveToNextScreen() {
//        int previousProcessID = SharedPref.getInstance(this).readIntegerPrefs(SharedPref.APP_PROCESS_ID);
//        SharedPref.getInstance(this).writeIntegerPref(SharedPref.APP_PROCESS_ID, android.os.Process.myPid());
//        int currentProcessID = android.os.Process.myPid();

        String checkAlreadyLogin = SharedPref.getInstance(this).readPrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF);
        boolean checkFirstLaunch = SharedPref.getInstance(this).readBooleanPrefs(SharedPref.FIRST_LAUNCH_PREF);
       if (!TextUtils.isEmpty(checkAlreadyLogin) && TextUtils.equals(checkAlreadyLogin, SharedPref.LOGIN_SUCCESS_VALUE) && checkFirstLaunch) {
            Intent intent = new Intent(getApplicationContext(), ProjectsActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            SharedPref.getInstance(this).writeBooleanPrefs(SharedPref.FIRST_LAUNCH_PREF, true);
            SharedPref.getInstance(this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
            finish();
        }
    }
}
