package com.jeeni.content.classic.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeeni.content.classic.Interfaces.DownloadCallBacks;
import com.jeeni.content.classic.Interfaces.DownloadProgressCallBacks;
import com.jeeni.content.classic.Interfaces.UnitDetailsCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.adapters.LatestContentAdapter;
import com.jeeni.content.classic.adapters.UnitDetailsAdapter;
import com.jeeni.content.classic.helper.ContentViewer;
import com.jeeni.content.classic.model.UnitContent;
import com.jeeni.content.classic.services.ForeGroundService;
import com.jeeni.content.classic.utils.Utils;

import java.util.ArrayList;

import static com.jeeni.content.classic.utils.ConstantVariables.MY_PERMISSIONS_REQUEST_STORAGE;

public class NewContentActivity extends AppCompatActivity implements UnitDetailsCallBacks, DownloadCallBacks,DownloadProgressCallBacks {

    private Context context;
    private DatabaseAdapter databaseAdapter;
    private LatestContentAdapter latestContentAdapter;
    private ArrayList<UnitContent> unitContents;
    private UnitContent selectedUnitContent;
    private int selectedPosition;

    private long progress;
    private String downloadingFile;

    public static DownloadProgressCallBacks downloadProgressCallBacks;

    public static DownloadCallBacks downloadCallBacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_content);
        initializeVariables();
        showCourses();
    }

    private void initializeVariables() {
        context = this;
        downloadProgressCallBacks=(DownloadProgressCallBacks) context;
        downloadCallBacks = (DownloadCallBacks) this;
        databaseAdapter = new DatabaseAdapter(context);
        unitContents = new ArrayList<>();
        TextView textViewSubTitle = (TextView) findViewById(R.id.textViewSubTitle);
        textViewSubTitle.setText("Recently Added Content");


    }

    private void showCourses() {
        //Check if we have offline data
        unitContents.clear();
        unitContents = databaseAdapter.getLatestUnitContent();

        if (unitContents.size() <= 0) {
            Toast.makeText(context, "All clear", Toast.LENGTH_SHORT).show();
        } else {
            //Show all courses
            showList(true);
        }

    }

    private void showList(boolean chechDownloadStatus) {
        if (chechDownloadStatus)
            checkDownloadStatus();

        if (latestContentAdapter != null) {
            latestContentAdapter.notifyDataSetChanged();
            return;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        latestContentAdapter = new LatestContentAdapter(unitContents, context, (UnitDetailsCallBacks) this);

        recyclerView.addItemDecoration(new NewContentActivity.SpaceItems(10));
        recyclerView.setAdapter(latestContentAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Set unit contents download status based on following conditions
     * If file name exist in directory than that content is downloaded
     * If file name match with the file which is being downloaded than its status is downloading
     * If both conditions fails than that file needs to be downloaded
     * */
    private void checkDownloadStatus() {
        for (UnitContent unitContent : unitContents) {
            try {
                if (Utils.isFileExist(NewContentActivity.this, unitContent.getCourseId()+"_"+unitContent.getSubjectId()+"_"+unitContent.getUnitId()+"_"+unitContent.getFileName())) {
                    unitContent.setDownloaded(true);
                    unitContent.setDownloading(false);
                } else {
                    if (ForeGroundService.fileNameUnique != null && ForeGroundService.fileNameUnique.equals(unitContent.getCourseId()+"_"+unitContent.getSubjectId()+"_"+unitContent.getUnitId()+"_"+unitContent.getFileName())) {
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
                downloadData(unitContent.getUrl(), unitContent.getFileName(),unitContent.getUnitId(),unitContent.getSubjectId(),unitContent.getCourseId());
                unitContents.get(position).setDownloading(true);
                showList(false);
            }
        } else {

            (new DatabaseAdapter(context)).updateLastUsedFiles(unitContent.getFileName(),unitContent.getCourseId(),unitContent.getSubjectId(),unitContent.getUnitId(),System.currentTimeMillis());

            if (unitContent.isVideo()) {
                new ContentViewer().viewVideo(unitContent,unitContent.getCourseId()+"_"+unitContent.getSubjectId()+"_"+unitContent.getUnitId()+"_"+unitContent.getFileName(), context);
            } else {
                new ContentViewer().viewPdf(unitContent,unitContent.getCourseId()+"_"+unitContent.getSubjectId()+"_"+unitContent.getUnitId()+"_"+unitContent.getFileName(), context);
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

    public void clearAll(View view) {
        databaseAdapter.updateAllLatestUnitContent();
        unitContents.clear();
        unitContents = databaseAdapter.getLatestUnitContent();
        if(latestContentAdapter!=null)
            latestContentAdapter.notifyDataSetChanged();
        if(unitContents.size()<=0)
            Toast.makeText(context, "All cleared successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadProgressUpdated(String fileName, long percentage) {
        downloadingFile=fileName;
        progress=percentage;
        updateDownloadProgress();
        showList(false);
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
     * @param url URL of file which you want to download
     * @param filename name of file which is being downloaded
     * */
    private void downloadData(String url, String filename,String unitId,String subjectId,String courseId) {

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
