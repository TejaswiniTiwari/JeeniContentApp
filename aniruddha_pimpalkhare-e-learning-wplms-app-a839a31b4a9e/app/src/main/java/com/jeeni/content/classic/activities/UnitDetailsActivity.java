package com.jeeni.content.classic.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeeni.content.classic.Interfaces.DownloadCallBacks;
import com.jeeni.content.classic.Interfaces.DownloadProgressCallBacks;
import com.jeeni.content.classic.Interfaces.SyncCallBacks;
import com.jeeni.content.classic.Interfaces.UnitDetailsCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.adapters.UnitDetailsAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.helper.ContentViewer;
import com.jeeni.content.classic.model.UnitContent;
import com.jeeni.content.classic.services.ForeGroundService;
import com.jeeni.content.classic.utils.CommonMethods;
import com.jeeni.content.classic.utils.ConstantVariables;
import com.jeeni.content.classic.utils.JsonParser;
import com.jeeni.content.classic.utils.SharedPref;
import com.jeeni.content.classic.utils.URLS;
import com.jeeni.content.classic.utils.Utils;
import com.jeeni.content.classic.volly.OnVolleyHandler;
import com.jeeni.content.classic.volly.VolleyInitialization;
import com.jeeni.content.classic.volly.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.jeeni.content.classic.utils.ConstantVariables.MY_PERMISSIONS_REQUEST_STORAGE;

/**
 * An UnitDetailsActivity activity which displays unit content
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class UnitDetailsActivity extends AppCompatActivity implements UnitDetailsCallBacks, DownloadCallBacks, DownloadProgressCallBacks, SyncCallBacks {

    //arbitrary number for permission
    private Context context;
    private DatabaseAdapter databaseAdapter;
    private String courseId;
    private String subjectId;
    private String unitId;
    private UnitDetailsAdapter unitDetailsAdapter;
    private ArrayList<UnitContent> unitContents;
    private UnitContent selectedUnitContent;
    private int selectedPosition;

    private long progress;
    private String downloadingFile;

    public static DownloadCallBacks downloadCallBacks;
    public static DownloadProgressCallBacks downloadProgressCallBacks;
    public static SyncCallBacks syncCallBacks;

    private CardView cardViewTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_details);

        initializeVariables();
        showUnitDetails(false);
    }

    private void initializeVariables() {
        context = this;
        downloadProgressCallBacks = (DownloadProgressCallBacks) context;
        downloadCallBacks = (DownloadCallBacks) this;
        syncCallBacks=(SyncCallBacks)this;
        databaseAdapter = new DatabaseAdapter(context);
        unitContents = new ArrayList<>();
        //Get data from intent
        courseId = getIntent().getStringExtra("courseId");
        subjectId = getIntent().getStringExtra("subjectId");
        unitId = getIntent().getStringExtra("unitId");
        //Get selected course and subject and set it as subtitle
        String courseName = getIntent().getStringExtra("courseName");
        String subjectName = getIntent().getStringExtra("subjectName");
        TextView textViewSubTitle = (TextView) findViewById(R.id.textViewSubTitle);
        textViewSubTitle.setText(courseName + " : " + subjectName);

        cardViewTryAgain = (CardView) findViewById(R.id.cardViewTryAgain);
        cardViewTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnitDetails(false);
            }
        });
    }

    private void showUnitDetails(boolean isSyncCompleted) {
        //Check if we have offline data
        unitContents.clear();
        unitContents = databaseAdapter.getUnitContent(unitId, subjectId, courseId);


        if (!isSyncCompleted && unitContents.size() <= 0) {
            //Offline data not available, make network call to fetch data
            webCallGetUnitDetails(new SharedPref(context).getAuthToken(), courseId, subjectId, unitId, true, true);
        } else {
            //Show all courses
            showList(true);
        }

    }

    private void showList(boolean chechDownloadStatus) {
        if (chechDownloadStatus)
            checkDownloadStatus();

        if (unitDetailsAdapter != null) {
            unitDetailsAdapter.notifyDataSetChanged();
            return;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        unitDetailsAdapter = new UnitDetailsAdapter(unitContents, context, (UnitDetailsCallBacks) this);

        recyclerView.addItemDecoration(new UnitDetailsActivity.SpaceItems(20));
        recyclerView.setAdapter(unitDetailsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Set unit contents download status based on following conditions
     * If file name exist in directory than that content is downloaded
     * If file name match with the file which is being downloaded than its status is downloading
     * If both conditions fails than that file needs to be downloaded
     */
    private void checkDownloadStatus() {
        for (UnitContent unitContent : unitContents) {
            try {
                if (Utils.isFileExist(UnitDetailsActivity.this, courseId + "_" + subjectId + "_" + unitId + "_" + unitContent.getFileName())) {
                    unitContent.setDownloaded(true);
                    unitContent.setDownloading(false);
                } else {
                    if (ForeGroundService.isRunning && ForeGroundService.fileNameUnique.equals(courseId + "_" + subjectId + "_" + unitId + "_" + unitContent.getFileName())) {
                        unitContent.setDownloading(true);
                        unitContent.setProgress(progress);
                        unitContent.setDownloaded(false);
                    } else {
                        unitContent.setDownloaded(false);
                        unitContent.setDownloading(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDownloadProgress() {
        for (UnitContent unitContent : unitContents) {
            try {

                if (unitContent.isDownloading()) {
                    unitContent.setDownloading(true);
                    unitContent.setProgress(progress);
                    unitContent.setDownloaded(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Network call to fetch unit details
    private void webCallGetUnitDetails(String token, final String courseId, final String subjectId, final String unitId, boolean isDisplay, boolean isCancel) {
        try {
            VolleyInitialization v = new VolleyInitialization(this, isDisplay, isCancel);

            WebService.GetUnitDetails(v, token, URLS.getSubscribedCourseUnitDetailsUrl(courseId, unitId, context), new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                    //Ignore this will never gona execute because we are making jsonObjectRequest
                }

                @Override
                public void onVolleySuccess(JSONObject response) {
                    JsonParser.parseAndStoreUnitDetails(courseId, subjectId, unitId, response, context.getApplicationContext());
                    unitContents.clear();
                    unitContents = new DatabaseAdapter(context).getUnitContent(unitId, subjectId, courseId);
                    showList(true);

                    (findViewById(R.id.layoutTryAgain)).setVisibility(View.GONE);
                }

                @Override
                public void onVolleySuccess(String response) {

                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error);
                    if (error.contains("AuthFailure")) {
                        webCallGetToken(new SharedPref(context).getOrgID());
                    } else CommonMethods.showMessage(context, error);
                    (findViewById(R.id.layoutTryAgain)).setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Network call to fetch Auth Token
     *
     * @param orgId Organization ID
     */
    private void webCallGetToken(String orgId) {

        SharedPref sharedPref=new SharedPref(context);
        String id=sharedPref.getDeviceId();
        String userId=sharedPref.getUserId();

        if(id==null||id.isEmpty()||userId==null||userId.isEmpty()){
            return;
        }else{
            id+=userId;
        }

        try {
            findViewById(R.id.layoutTryAgain).setVisibility(View.GONE);


            VolleyInitialization v = new VolleyInitialization(this, true, true);

            WebService.GetAuthenticationToken(v,userId, orgId,id, new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                }

                @Override
                public void onVolleySuccess(JSONObject response) {
                    CustomLog.i("WebCalls", response.toString());
                    //Parse Jeeni Auth server response
                    boolean isSuccess = JsonParser.parseAndStoreAuthToken(response, context.getApplicationContext());

                    if (!isSuccess)
                        onVolleyError("Failed to initialize, please try again later");
                }

                @Override
                public void onVolleySuccess(String response) {

                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error);
                    CommonMethods.showMessage(context, error);
                    findViewById(R.id.layoutTryAgain).setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUnitContentSelected(final UnitContent unitContent, int position) {

        //Check if we have storage permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            selectedUnitContent = unitContent;
            selectedPosition = position;
            askPermission();
            return;
        }

        //Only one download at a time is allowed so check if we are downloading any content
        if (unitContent.isDownloading())
            Toast.makeText(context, "Downloading in progress, please try again later", Toast.LENGTH_SHORT).show();
        else if (!unitContent.isDownloaded()) {
            if (ForeGroundService.isRunning) {
                Toast.makeText(context, "Downloading in progress, please try again later", Toast.LENGTH_SHORT).show();
            } else {
                downloadData(unitContent.getUrl(), unitContent.getFileName());
                unitContents.get(position).setDownloading(true);
                showList(false);
            }
        } else {

            (new DatabaseAdapter(context)).updateLastUsedFiles(unitContent.getFileName(), unitContent.getCourseId(), unitContent.getSubjectId(), unitContent.getUnitId(), System.currentTimeMillis());

            if (unitContent.isVideo()) {
                new ContentViewer().viewVideo(unitContent,courseId + "_" + subjectId + "_" + unitId + "_" + unitContent.getFileName(), context);
            } else {
                new ContentViewer().viewPdf(unitContent,courseId + "_" + subjectId + "_" + unitId + "_" + unitContent.getFileName(), context);
            }
        }

    }

    private void askPermission() {
        // No explanation needed; request the permission
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onUnitContentSelected(selectedUnitContent, selectedPosition);
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDownloadFinished() {
        showList(true);
    }

    @Override
    public void onDownloadFailed(Exception e) {
        showList(true);
    }

    @Override
    public void onDownloadProgressUpdated(String fileName, long percentage) {
        downloadingFile = fileName;
        progress = percentage;

        updateDownloadProgress();
        showList(false);
    }

    @Override
    public void onSyncFinished() {
        showUnitDetails(true);
    }

    @Override
    public void onSyncFailed(Exception e) {
        showUnitDetails(true);
    }


    class SpaceItems extends RecyclerView.ItemDecoration {

        private final int spacer;

        SpaceItems(int spacer) {
            this.spacer = spacer;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = spacer;
        }
    }


    /**
     * Start service to download content
     *
     * @param url      URL of file which you want to download
     * @param filename name of file which is being downloaded
     */
    private void downloadData(String url, String filename) {

        Intent intent = new Intent(this.getApplicationContext(), ForeGroundService.class);
        intent.putExtra("url", url);
        intent.putExtra("unitId", unitId);
        intent.putExtra("subjectId", subjectId);
        intent.putExtra("courseId", courseId);
        intent.putExtra("filename", filename);
        if (!ForeGroundService.isRunning) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.startForegroundService(intent);
            } else
                this.startService(intent);
        } else {
            Toast.makeText(this, "Downloading in progress", Toast.LENGTH_SHORT).show();
        }
    }

}
