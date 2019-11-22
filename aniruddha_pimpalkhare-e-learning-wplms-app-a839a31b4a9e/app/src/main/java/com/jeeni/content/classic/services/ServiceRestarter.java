package com.jeeni.content.classic.services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.jeeni.content.classic.customviews.CustomLog;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class ServiceRestarter extends BroadcastReceiver {
    private Intent mServiceIntent;;
    @Override
    public void onReceive(Context context, Intent intent) {
        CustomLog.i(ServiceRestarter.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");

        if (!isMyServiceRunning(ForeGroundService.class,context)) {
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(new Intent(context.getApplicationContext(), ForeGroundService.class));
            } else
                context.startService(new Intent(context.getApplicationContext(), ForeGroundService.class));
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                CustomLog.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        CustomLog.i("isMyServiceRunning?", false + "");
        return false;
    }
}
