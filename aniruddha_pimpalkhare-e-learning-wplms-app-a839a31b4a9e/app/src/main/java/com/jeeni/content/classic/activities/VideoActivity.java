package com.jeeni.content.classic.activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.helper.EncryptedFileDataSourceFactory;
import com.jeeni.content.classic.model.UsagePattern;
import com.jeeni.content.classic.utils.SharedPref;
import com.jeeni.content.classic.utils.Utils;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.jeeni.content.classic.helper.ContentViewer.COURSE_NAME;
import static com.jeeni.content.classic.helper.ContentViewer.FILE_NAME;
import static com.jeeni.content.classic.helper.ContentViewer.INSERT_USAGE_PATTERN;
import static com.jeeni.content.classic.helper.ContentViewer.SUBJECT_NAME;
import static com.jeeni.content.classic.helper.ContentViewer.UNIT_NAME;
import static com.jeeni.content.classic.helper.ContentViewer.USAGE_PATTERN_FILE_NAME;

/**
 * An Video activity which displays downloaded encrypted videos using exoPlayer library
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class VideoActivity extends AppCompatActivity {


    //Usage pattern variables
    private String courseName;
    private String subjectName;
    private String unitName;
    private String fileName;
    private String usagePatternFileName;
    private long startTime;
    private boolean isAddUsagePattern;

    //start of Auto generated code for full screen
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
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

    //end of Auto generated code for full screen
    private SimpleExoPlayerView exoPlayer;
    private SimpleExoPlayer player;
    private TrackSelector trackSelector;
    private TrackSelection.Factory videoTrackSelectionFactory;
    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";
    private String encryptedFileName;
    private File mEncryptedFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //start of Auto generated code for full screen
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.exo_player);

        encryptedFileName = getIntent().getStringExtra("filename");

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
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        //end of Auto generated code for full screen

        exoPlayer = (SimpleExoPlayerView) mContentView;

        playVideo();

    }


    //start of Auto generated code for full screen
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void releasePlayer() {
        if (player != null) {
            player.removeListener(null);
            player.stop();
            player.release();
            player = null;
            trackSelector = null;
            videoTrackSelectionFactory = null;
        }

        finish();
    }

    private void pausePlayer() {
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    private void startPlayer() {
        player.setPlayWhenReady(true);
        player.getPlaybackState();
        mVisible = true;
        toggle();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isAddUsagePattern){
            UsagePattern usagePattern=new UsagePattern(startTime+"",unitName,subjectName,courseName,usagePatternFileName,"VIDEO",startTime+"",((int)(System.currentTimeMillis()-startTime))/1000+"");
            new DatabaseAdapter(VideoActivity.this).insertUsagePattern(usagePattern);
        }

        pausePlayer();
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

        startPlayer();
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
    //end of Auto generated code for full screen


    public void playVideo() {
        SharedPref sharedPref = new SharedPref(this);
        try {
            mEncryptedFile = new File(Utils.getRootDirPath(VideoActivity.this) + File.separator, encryptedFileName);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        byte[] key = sharedPref.getKey();
        byte[] iv = sharedPref.getIv();

        SecretKeySpec mSecretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
        IvParameterSpec mIvParameterSpec = new IvParameterSpec(iv);

        Cipher mCipher = Utils.getCipher(this, Cipher.DECRYPT_MODE);


        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        exoPlayer.setPlayer(player);
        DataSource.Factory dataSourceFactory = new EncryptedFileDataSourceFactory(mCipher, mSecretKeySpec, mIvParameterSpec, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        try {
            Uri uri = Uri.fromFile(mEncryptedFile);
            MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
