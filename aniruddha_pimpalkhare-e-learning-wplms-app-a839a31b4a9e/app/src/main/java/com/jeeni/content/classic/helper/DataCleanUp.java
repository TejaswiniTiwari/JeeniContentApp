package com.jeeni.content.classic.helper;

import android.content.Context;
import android.os.AsyncTask;

import com.jeeni.content.classic.Interfaces.DataCleanUpCallBacks;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.utils.Utils;

import static com.jeeni.content.classic.utils.ConstantVariables.GB;

/**
 * An DataCleanUp maintains applications data in set limit for now it is 1GB
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class DataCleanUp extends AsyncTask<Integer, Void, Integer> {
    private Context context;
    private DataCleanUpCallBacks dataCleanUpCallBacks;
    private DatabaseAdapter databaseAdapter;
    private static final String TAG=DataCleanUp.class.getSimpleName();


    public DataCleanUp(Context context, DataCleanUpCallBacks dataCleanUpCallBacks) {
        this.context = context.getApplicationContext();
        this.dataCleanUpCallBacks = dataCleanUpCallBacks;
        databaseAdapter = new DatabaseAdapter(context);
    }

    @Override
    protected Integer doInBackground(Integer... ints) {
        try {
            checkStorage();
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

    //Check if we have exhausted our storage limit or not
    private void checkStorage() {
        try {

            //Get total downloaded bytes
            long totalStorageUsedByApp = databaseAdapter.getTotalFileSize();
            CustomLog.e(TAG,"Total space consumed "+totalStorageUsedByApp);
            long availableExternalStorage = Utils.getAvailableExternalMemorySize(context);
            if (GB > totalStorageUsedByApp) {
                if (availableExternalStorage > (GB - totalStorageUsedByApp)) {
                    CustomLog.e(TAG,"Sufficient Space is available proceed with downloading");
                    callBack(true, null);
                } else {
                    CustomLog.e(TAG,"Low on space delete some files");
                    deleteFiles();
                }
            } else {
                deleteFiles();
            }

        } catch (Exception e) {
            e.printStackTrace();
            callBack(false, e);
        }
    }

    //Delete files using filename
    private void deleteFiles() {

        String fileName = databaseAdapter.getFirstDownloadedFile();
        CustomLog.e(TAG,"Low on space delete "+fileName);

        try {
            boolean isFileDeleted = Utils.deleteFile(context, fileName);
            if (isFileDeleted) {
                databaseAdapter.deleteDownloadedFile(fileName);
                CustomLog.e(TAG,"Low on space delete "+fileName+" deleted successfully");
                checkStorage();
            } else {
                throw new Exception("Unable to delete " + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("File not found") || e.getMessage().contains("Directory not found")) {
                databaseAdapter.deleteDownloadedFile(fileName);
                checkStorage();
            } else
                callBack(false, e);
        }

    }


    private void callBack(boolean isSuccess, Exception e) {
        if (context != null)
            context = null;

        if (isSuccess) {
            if (dataCleanUpCallBacks != null) {
                dataCleanUpCallBacks.onDataCleanUpFinished();
            }
        } else {
            if (dataCleanUpCallBacks != null) {
                dataCleanUpCallBacks.onDataCleanUpFailed(e);
            }
        }
    }
}
