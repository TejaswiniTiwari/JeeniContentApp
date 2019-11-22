package com.jeeni.content.classic.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.jeeni.content.classic.Interfaces.SyncCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.activities.UnitDetailsActivity;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.helper.SyncData;
import com.jeeni.content.classic.utils.SharedPref;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class ForeGroundAutoSyncService extends Service implements SyncCallBacks {

    /*
     * Logic for Syncing
     * WPLMS maintains timestamp for courses and units
     * I will add one more filed in my database to store downloaded timestamp
     * So when I will request latest timestamp than I can compare them if local timestamp is lower then I will fetch data for that
     * */
    public static boolean isServiceRunning = false;
    public static final long NOTIFY_INTERVAL = 200;
    public static final int FOREGROUND_ID = 1122;
    public static final int NOTIFICATION_ID_FAILED = 1123;

    public static Context context;
    private NotificationManager manager;

    String TAG = ForeGroundAutoSyncService.class.getSimpleName();
    SyncCallBacks syncCallBacks = null;
    public static NotificationCompat.Builder builder;
    public static NotificationManagerCompat notification;
    private String url;
    public static String fileName;
    private String CHANNEL_ID;
    private NotificationChannel chan1;
    public static boolean isRunning;
    private SharedPref sharedPref;

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
            builder.setContentTitle("Jeeni Content")
                    .setContentText("Sync in progress")
                    .setSmallIcon(R.drawable.app_icon)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(false)
                    .setOnlyAlertOnce(true)
//                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

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

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        //TODO your background code
//                        cleanUpDate();
//                    }
//                });
//            }
//        });
//        thread.start();

        downloadData();

        return super.onStartCommand(intent, flags, startId);
    }


    private void downloadData() {

//        final String url1=url;
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                //TODO your background code
//                new DownloadAsync(context, url1, downloadCallBacks).execute();
//            }
//        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                new SyncData(context,sharedPref.getAuthToken(), syncCallBacks).sync();
            }
        }).start();

//        new DownloadData((Activity) context,downloadCallBacks).execute();
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
//        if (databaseAdapter == null)
//            databaseAdapter = new DatabaseAdapter(context);

        if (sharedPref == null)
            sharedPref = new SharedPref(context);
        if (syncCallBacks == null)
            syncCallBacks = this;
    }

    public void stopService(boolean isSuccess, Exception exception) {

        isRunning = false;

        if (!isSuccess) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentTitle("Data Sync")
                    .setContentText(exception.getMessage())
                    .setSmallIcon(R.drawable.app_icon)
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            notification.notify(NOTIFICATION_ID_FAILED, builder.build());
        }


        try {
            isServiceRunning = false;
            Intent serviceIntent = new Intent(context, ForeGroundAutoSyncService.class);
            context.stopService(serviceIntent);
//            stopForeground(true);
//            myHandler.removeCallbacksAndMessages(null);
//            myHandlerDelayRequest.removeCallbacksAndMessages(null);
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSyncFinished() {
        if (UnitDetailsActivity.downloadCallBacks != null)
            UnitDetailsActivity.downloadCallBacks.onDownloadFinished();

        stopService(true, null);
    }

    @Override
    public void onSyncFailed(Exception e) {
        stopService(false, e);
    }
}
