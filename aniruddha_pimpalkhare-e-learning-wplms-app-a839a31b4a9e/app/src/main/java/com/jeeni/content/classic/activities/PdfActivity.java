package com.jeeni.content.classic.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.jeeni.content.classic.Interfaces.PageCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.adapters.PageAdapter;
import com.jeeni.content.classic.model.Page;
import com.jeeni.content.classic.model.UsagePattern;
import com.jeeni.content.classic.utils.CommonMethods;
import com.jeeni.content.classic.volly.CustomProgressDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.jeeni.content.classic.helper.ContentViewer.COURSE_NAME;
import static com.jeeni.content.classic.helper.ContentViewer.FILE_NAME;
import static com.jeeni.content.classic.helper.ContentViewer.INSERT_USAGE_PATTERN;
import static com.jeeni.content.classic.helper.ContentViewer.SUBJECT_NAME;
import static com.jeeni.content.classic.helper.ContentViewer.UNIT_NAME;
import static com.jeeni.content.classic.helper.ContentViewer.USAGE_PATTERN_FILE_NAME;

/**
 * An PDF view activity which reads pdf file from internal storage and displays it as image
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class PdfActivity extends AppCompatActivity implements View.OnClickListener, PageCallBacks {
    //start of default generated code for full screen activity
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 10000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    //end of default generated code for full screen activity

    //Usage pattern variables
    private String courseName;
    private String subjectName;
    private String unitName;
    private String fileName;
    private String usagePatternFileName;
    private long startTime;
    private boolean isAddUsagePattern;

    /**
     * Key string for saving the state of current page index.
     */
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    /**
     * The filename of the PDF.
     */
    public String FILEPATH;

    private static CustomProgressDialog customProgressDialog;
    private static PdfActivity pdfActivity;
    private RecyclerView recyclerViewPage;
    private PageAdapter pageAdapter;
    private static ArrayList<Page> pages;

    /**
     * File descriptor of the PDF.
     */
    private ParcelFileDescriptor mFileDescriptor;

    /**
     * {@link PdfRenderer} to render the PDF.
     */
    private static PdfRenderer mPdfRenderer;

    /**
     * Page that is currently shown on the screen.
     */
    private PdfRenderer.Page mCurrentPage;

    /**
     * {@link PhotoView} that shows a PDF page as a {@link Bitmap}
     */
    PhotoView photoView;

    /**
     * {@link Button} to move to the previous page.
     */
    private Button mButtonPrevious;
    //    private ImageView mButtonZoomin;
//    private ImageView mButtonZoomout;
    private Button mButtonNext;
    private float currentZoomLevel = 10;

    /**
     * PDF page index
     */
    private int mPageIndex;


    //start of default generated code for full screen activity
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private final void mDelayHide() {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
    }

    //end of default generated code for full screen activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pdf);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initializePdfViews(savedInstanceState);

        mVisible = false;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.iv_photo);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //ToDo Handle this
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isAddUsagePattern=getIntent().getBooleanExtra(INSERT_USAGE_PATTERN,false);

        if(isAddUsagePattern){
            courseName=getIntent().getStringExtra(COURSE_NAME);
            subjectName=getIntent().getStringExtra(SUBJECT_NAME);
            unitName=getIntent().getStringExtra(UNIT_NAME);
            fileName=getIntent().getStringExtra(FILE_NAME);
            usagePatternFileName=getIntent().getStringExtra(USAGE_PATTERN_FILE_NAME);
            startTime=System.currentTimeMillis();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isAddUsagePattern){
            UsagePattern usagePattern=new UsagePattern(startTime+"",unitName,subjectName,courseName,usagePatternFileName,"PDF",startTime+"",((int)(System.currentTimeMillis()-startTime))/1000+"");
            new DatabaseAdapter(PdfActivity.this).insertUsagePattern(usagePattern);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(8000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    public void initializePdfViews(Bundle savedInstanceState) {
        // Retain view references.
        pdfActivity = this;
        photoView = (PhotoView) findViewById(R.id.iv_photo);
        mButtonPrevious = (Button) findViewById(R.id.previous);
        mButtonNext = (Button) findViewById(R.id.next);
        recyclerViewPage = (RecyclerView) findViewById(R.id.recyclerViewPages);
        pages = new ArrayList<>();
        // Bind events.
        mButtonPrevious.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mPageIndex = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            mPageIndex = savedInstanceState.getInt(STATE_CURRENT_PAGE_INDEX, 0);
        }

        FILEPATH = getIntent().getExtras().getString("path");
        showPageList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            openRenderer(this);

            new PageGenerator().execute(mPageIndex);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    @Override
    public void onStop() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mCurrentPage) {
            outState.putInt(STATE_CURRENT_PAGE_INDEX, mCurrentPage.getIndex());
        }
    }

    /**
     * Sets up a {@link PdfRenderer} and related resources.
     */
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(FILEPATH);

        if (!file.exists()) {

            Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
            this.finish();
        }

        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        // This is the PdfRenderer we use to render the PDF.
        if (mFileDescriptor != null) {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
        }
    }

    /**
     * Closes the {@link PdfRenderer} and related resources.
     *
     * @throws IOException When the PDF file cannot be closed.
     */
    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            mCurrentPage.close();
            mCurrentPage = null;
        }
        if (null != mPdfRenderer) {
            try {
                mPdfRenderer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != mFileDescriptor) {
            try {
                mFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Zoom level for zoom matrix depends on screen density (dpiAdjustedZoomLevel), but width and height of bitmap depends only on pixel size and don't depend on DPI
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */
    private void showPage(int index) {


        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != mCurrentPage) {
            mCurrentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        mCurrentPage = mPdfRenderer.openPage(index);

        pages.get(index).setSelected(true);
        showPageList();

        //The deviceDimension stores dimension in a int array [0]=height, [1]=width
        int[] deviceDimension = CommonMethods.getDeviceDimensions(PdfActivity.this);

        //Calculate zoom level so that image can fit device perfectly and to avoid Memory error
        currentZoomLevel = (float) (((float) deviceDimension[0] / mCurrentPage.getHeight()) + 0.5);
        currentZoomLevel = currentZoomLevel * currentZoomLevel;
        // Important: the destination bitmap must be ARGB (not RGB).

        float dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_MEDIUM / getResources().getDisplayMetrics().densityDpi;

        int newWidth = (int) (mCurrentPage.getWidth() * (dpiAdjustedZoomLevel / 1));//(getResources().getDisplayMetrics().widthPixels * mCurrentPage.getWidth() / 72 * currentZoomLevel / 40);
        int newHeight = (int) (mCurrentPage.getHeight() * (dpiAdjustedZoomLevel / 1));// (getResources().getDisplayMetrics().heightPixels * mCurrentPage.getHeight() / 72 * currentZoomLevel / 64);


        if (newHeight < newWidth) {
            int tempW = newWidth;
            newWidth = newHeight;
            newHeight = tempW;
        }


        final Bitmap bitmap = Bitmap.createBitmap(
                newWidth,
                newHeight,
                Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();

        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel);

        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        mCurrentPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // We are ready to show the Bitmap to user.
        //Only UI thread can update views so call UpdateUi on UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                photoView.setImageBitmap(bitmap);
                updateUi();
            }
        });

    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUi() {
        try {
            int index = mCurrentPage.getIndex();
            int pageCount = mPdfRenderer.getPageCount();
            if (pageCount == 1) {
                mButtonPrevious.setVisibility(View.GONE);
                mButtonNext.setVisibility(View.GONE);
            } else {
                mButtonPrevious.setEnabled(0 != index);
                mButtonNext.setEnabled(index + 1 < pageCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPageList() {

        if (pageAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pageAdapter.notifyDataSetChanged();
                }
            });
            return;
        }

        recyclerViewPage.setHasFixedSize(true);
        pageAdapter = new PageAdapter(pages, pdfActivity, (PageCallBacks) this);
        recyclerViewPage.setAdapter(pageAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(pdfActivity);
        layoutManager.setOrientation(LinearLayout.HORIZONTAL);
        recyclerViewPage.setLayoutManager(layoutManager);
    }

    /**
     * Gets the number of pages in the PDF. This method is marked as public for testing.
     *
     * @return The number of pages.
     */
    public static int getPageCount() {
        return mPdfRenderer.getPageCount();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previous: {
                // Move to the previous page
                mDelayHide();
                new PageGenerator().execute(mCurrentPage.getIndex() - 1);
                break;
            }
            case R.id.next: {
                // Move to the next page
                mDelayHide();
                new PageGenerator().execute(mCurrentPage.getIndex() + 1);
                break;
            }
        }
    }

    @Override
    public void onPageSelected(Page page, int position) {
        mDelayHide();
        new PageGenerator().execute(page.getNumber() - 1);
    }

    private static class PageGenerator extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... integers) {

            if (pages.size() <= 0)
                for (int i = 0; i < getPageCount(); i++) {
                    Page page = new Page(i + 1);
                    pages.add(page);
                }

            for (Page page : pages) {
                page.setSelected(false);
            }

            pdfActivity.showPage(integers[0]);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            customProgressDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            customProgressDialog = new CustomProgressDialog(pdfActivity, "Loading, please wait...", false);
            customProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
