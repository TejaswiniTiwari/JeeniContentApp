package com.jeeni.content.classic.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.jeeni.content.classic.Interfaces.DownloadCallBacks;
import com.jeeni.content.classic.activities.NewContentActivity;
import com.jeeni.content.classic.activities.UnitDetailsActivity;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.model.UnitContent;
import com.jeeni.content.classic.utils.Utils;

import java.io.File;
import java.util.Date;

import javax.crypto.Cipher;

import static com.jeeni.content.classic.services.ForeGroundService.FOREGROUND_ID;
import static com.jeeni.content.classic.services.ForeGroundService.PROGRESS_CURRENT;
import static com.jeeni.content.classic.services.ForeGroundService.PROGRESS_MAX;
import static com.jeeni.content.classic.services.ForeGroundService.builder;
import static com.jeeni.content.classic.services.ForeGroundService.notification;

/**
 * Download data on background thread
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class DownloadAsync extends AsyncTask<Integer, Void, Integer> {
    private static String dirPath;
    private int downloadIdOne;
    private Context context;
    private DownloadCallBacks downloadCallBacks;
    private String URL1;
    private long fileSize;
    private String fileName;
    private  UnitContent unitContent;

    long  startTime;
    long elapsedTime = 0L;

    
    public DownloadAsync(Context context,String unitId,String subjectId,String courseId, String url, DownloadCallBacks downloadCallBacks) {
        URL1 = url;
        try {
            dirPath = new File(Utils.getRootDirPath(context)).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            callBack(false, e);
        }
        this.context = context;
        this.downloadCallBacks = downloadCallBacks;
        unitContent=new UnitContent(unitId,subjectId,courseId,url,"",false,false,false);
    }

    public void cancelDownload() {
        PRDownloader.cancel(downloadIdOne);
    }

    public void download() {
        if (com.downloader.Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
            PRDownloader.pause(downloadIdOne);
            return;
        }

        if (com.downloader.Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
            PRDownloader.resume(downloadIdOne);
            return;
        }

        String[] array = URL1.split("/");
        fileName = array[array.length - 1];
        CustomLog.e("###Downloading", " Downloading " + fileName);
        downloadIdOne = PRDownloader.download(URL1, dirPath, unitContent.getCourseId()+"_"+unitContent.getSubjectId()+"_"+unitContent.getUnitId()+"_"+fileName, Utils.getCipher(context, Cipher.ENCRYPT_MODE))
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        CustomLog.e("####Progress", "setOnStartOrResumeListener");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        CustomLog.e("####Progress", "setOnPauseListener");
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        CustomLog.e("####Progress", "setOnCancelListener");
                        downloadIdOne = 0;
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(final Progress progress) {
                        if (fileSize <= 0)
                            fileSize = progress.totalBytes;


                        if (elapsedTime > 1000) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                    PROGRESS_CURRENT = (int) progressPercent;
                                    builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                                    notification.notify(FOREGROUND_ID, builder.build());

                                    if(UnitDetailsActivity.downloadProgressCallBacks!=null){
                                        UnitDetailsActivity.downloadProgressCallBacks.onDownloadProgressUpdated(fileName,progressPercent);
                                    }

                                    if(NewContentActivity.downloadProgressCallBacks!=null){
                                        NewContentActivity.downloadProgressCallBacks.onDownloadProgressUpdated(fileName,progressPercent);
                                    }

                                    startTime = System.currentTimeMillis();
                                    elapsedTime = 0;
                                }
                            });
                        }
                        else
                            elapsedTime = new Date().getTime() - startTime;

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
                        unitContent.setDownloaded(true);
                        unitContent.setFileName(fileName);
                        databaseAdapter.insertDownloads(unitContent, fileSize);
                        CustomLog.e("####Progress", "onDownloadComplete");
                        callBack(true, null);
                    }

                    @Override
                    public void onError(Error error) {
                        CustomLog.e("####Progress", "onError" + error.toString());
                        CustomLog.e("####Progress", "onError isServerError " + error.isServerError());
                        CustomLog.e("####Progress", "onError isConnectionError" + error.isConnectionError());

                        String errorMessage = "Failed to sync";
                        if (error.isServerError())
                            errorMessage = "Failed to download, Server error occurred";
                        if (error.isConnectionError())
                            errorMessage = "Failed to download, Connection error occurred";

                        downloadIdOne = 0;
                        callBack(false, new Exception(errorMessage));
                    }
                });
    }

    private void callBack(boolean isSuccess, Exception e) {
        if (isSuccess) {
            if (downloadCallBacks != null) {
                downloadCallBacks.onDownloadFinished();
            }

        } else {
            if (downloadCallBacks != null) {
                downloadCallBacks.onDownloadFailed(e);
            }
        }

    }

    @Override
    protected Integer doInBackground(Integer... ints) {
        try {
            download();
        } catch (Exception e) {
            cancel(true);
            e.printStackTrace();
            return null;
        }
        return 1;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }


    @Override
    protected void onPostExecute(Integer output) {
//        callBack(true, null);
    }

    @Override
    protected void onCancelled() {
        callBack(false, new Exception("Data clean-up task canceled"));
    }

}
