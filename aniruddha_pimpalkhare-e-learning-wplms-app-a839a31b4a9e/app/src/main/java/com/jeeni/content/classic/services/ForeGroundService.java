package com.jeeni.content.classic.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.jeeni.content.classic.Interfaces.DataCleanUpCallBacks;
import com.jeeni.content.classic.Interfaces.DownloadCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.activities.NewContentActivity;
import com.jeeni.content.classic.activities.UnitDetailsActivity;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.helper.DataCleanUp;
import com.jeeni.content.classic.helper.DownloadAsync;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class ForeGroundService extends Service implements DownloadCallBacks, DataCleanUpCallBacks {

//    public static boolean isServiceRunning = false;
    public static final long NOTIFY_INTERVAL = 200;
    public static final int FOREGROUND_ID = 1111;
    public static final int NOTIFICATION_ID_FAILED = 1112;

    public static Context context;
    private NotificationManager manager;

    String TAG = ForeGroundService.class.getSimpleName();
    List<String> sArray = new ArrayList<>();
    DownloadCallBacks downloadCallBacks = null;
    public static int PROGRESS_CURRENT;
    public static int PROGRESS_MAX = 100;
    public static NotificationCompat.Builder builder;
    public static NotificationManagerCompat notification;
    private String url;
    private String unitId;
    private String subjectId;
    private String courseId;
    private static String fileName;
    public static String fileNameUnique;

    private String CHANNEL_ID;
    private NotificationChannel chan1;
    public static boolean isRunning;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        init();

        try {

            CHANNEL_ID = "e_learning_app_channel";

            if (Build.VERSION.SDK_INT >= 26) {
                chan1 = new NotificationChannel(CHANNEL_ID,
                        getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                chan1.setLightColor(Color.GREEN);
                chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                getManager().createNotificationChannel(chan1);
            }

            notification = NotificationManagerCompat.from(this);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentTitle("Content Download")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.drawable.app_icon)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(false)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            PROGRESS_CURRENT = 0;
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            notification.notify(FOREGROUND_ID, builder.build());

            if (Build.VERSION.SDK_INT >= 26) {
                startForeground(FOREGROUND_ID, builder.build());
            } else {
                startForeground(FOREGROUND_ID,
                        buildForegroundNotification("e-Learning App Service"));
                getManager().notify(FOREGROUND_ID, buildForegroundNotification("e-Learning App Service"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        url = intent.getStringExtra("url");
        unitId = intent.getStringExtra("unitId");
        subjectId = intent.getStringExtra("subjectId");
        courseId = intent.getStringExtra("courseId");
        fileName = intent.getStringExtra("filename");

        fileNameUnique=courseId+"_"+subjectId+"_"+unitId+"_"+fileName;
        cleanUpDate();

        return super.onStartCommand(intent, flags, startId);
    }

    private void cleanUpDate() {

        new DataCleanUp(context, (DataCleanUpCallBacks) this).execute();
    }

    private void downloadData(String url) {
        new DownloadAsync(context, unitId, subjectId, courseId, url, downloadCallBacks).execute();
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private Notification buildForegroundNotification(String filename) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);

        b.setOngoing(true);

        b.setContentTitle("Content Download")
                .setContentText(filename)
                .setSmallIcon(R.drawable.app_icon)
                .setTicker("Download in progress");

        return (b.build());
    }


    @Override
    public void onDestroy() {
        CustomLog.e("PostDataService", "Destroyed");
        super.onDestroy();
    }

    private void init() {

        if (context == null)
            context = getApplicationContext();

        if (downloadCallBacks == null)
            downloadCallBacks = this;
    }

    public void stopService(boolean isSuccess, Exception exception) {

        isRunning = false;

        if (!isSuccess) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentTitle("Content Download")
                    .setContentText(exception.getMessage())
                    .setSmallIcon(R.drawable.app_icon)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            notification.notify(NOTIFICATION_ID_FAILED, builder.build());
        }


        try {
//            isRunning = false;
            Intent serviceIntent = new Intent(context, ForeGroundService.class);
            context.stopService(serviceIntent);
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadFinished() {
        if (UnitDetailsActivity.downloadCallBacks != null)
            UnitDetailsActivity.downloadCallBacks.onDownloadFinished();
        if (NewContentActivity.downloadCallBacks != null)
            NewContentActivity.downloadCallBacks.onDownloadFinished();
        stopService(true, null);
    }

    @Override
    public void onDownloadFailed(Exception e) {
        isRunning = false;
        fileName = null;
        if (UnitDetailsActivity.downloadCallBacks != null)
            UnitDetailsActivity.downloadCallBacks.onDownloadFailed(e);
        if (NewContentActivity.downloadCallBacks != null)
            NewContentActivity.downloadCallBacks.onDownloadFailed(e);
        stopService(false, e);
    }

    @Override
    public void onDataCleanUpFinished() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO your background code
                        downloadData(url);
                    }
                });
            }
        });
        thread.start();

    }

    @Override
    public void onDataCleanUpFailed(Exception e) {
        stopService(false, e);
    }
}
