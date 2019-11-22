package com.jeeni.content.classic.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.model.UsagePattern;
import com.jeeni.content.classic.utils.SharedPref;
import com.jeeni.content.classic.utils.Utils;

import java.util.Arrays;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final SharedPref sharedPref=new SharedPref(this);
        // Splash screen timer
        int SPLASH_TIME_OUT = 400;

//        sharedPref.setUserId(null);

        if(sharedPref.getUserId()==null)
            sharedPref.setIsLoggedIn(false);

        if(!sharedPref.IsLoggedIn()){
            String android_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            new SharedPref(this).setDeviceId(android_id);
        }
////        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        try {
////            @SuppressLint("MissingPermission") String id= telephonyManager.getDeviceId();
//
//             String android_id = Settings.Secure.getString(this.getContentResolver(),
//                    Settings.Secure.ANDROID_ID);
////            id=id;
////            android_id=android_id;
//            new SharedPref(context).setDeviceId(id);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        DatabaseAdapter databaseAdapter=new DatabaseAdapter(this);

        databaseAdapter.getAllUsagePattern();

        if(sharedPref.isFirstLaunch()){
            //Clear all old data
            String versionName="0";
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                 versionName = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if(Float.parseFloat(versionName)<=0.4f){
                try {
                    databaseAdapter.clearData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Utils.deleteDirectory(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            sharedPref.setIsFirstLaunch(false);
        }


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                //TODO check if already logged in I will add that code later
                Intent i=null;
                if(!sharedPref.IsLoggedIn()) {
                   i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }else if(sharedPref.getAuthToken()==null || sharedPref.getUrl()==null){
                    i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }else {
                    i = new Intent(SplashScreenActivity.this, CourseListActivity.class);
                }

                startActivity(i);
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


}
