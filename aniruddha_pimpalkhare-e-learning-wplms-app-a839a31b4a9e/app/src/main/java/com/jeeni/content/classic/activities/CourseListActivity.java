package com.jeeni.content.classic.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jeeni.content.classic.Interfaces.DownloadCallBacks;
import com.jeeni.content.classic.Interfaces.SyncCallBacks;
import com.jeeni.content.classic.Interfaces.UnitDetailsCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.adapters.CoursesAdapter;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.adapters.RecentAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.helper.ContentViewer;
import com.jeeni.content.classic.helper.MySingleton;
import com.jeeni.content.classic.model.Course;
import com.jeeni.content.classic.model.Unit;
import com.jeeni.content.classic.model.UnitContent;
import com.jeeni.content.classic.model.UsagePattern;
import com.jeeni.content.classic.services.ForeGroundAutoSyncService;
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
import java.util.HashMap;
import java.util.Map;


/**
 * Display subscribed course list
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class CourseListActivity extends AppCompatActivity implements UnitDetailsCallBacks, DownloadCallBacks, SyncCallBacks {

    private Context context;
    private DatabaseAdapter databaseAdapter;

    private RecyclerView recyclerViewCourseList;
    private CoursesAdapter coursesAdapter;
    private RecentAdapter recentAdapter;
    private RecyclerView recyclerViewResentlyAccessed;
    private ArrayList<UnitContent> contents;

    private TextView textNewContent;
    int newContentCount = 0;

    private CardView cardViewTryAgain;
    private SharedPref sharedPref;
    public static SyncCallBacks syncCallBacks;
//    private View viewTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        initializeVariables();
        showCourses(false);
        if (databaseAdapter.getAllCourses().size() > 0) {
            sync();
        }
    }

    private void initializeVariables() {
        context = this;
        databaseAdapter = new DatabaseAdapter(context);
        sharedPref = new SharedPref(context);
        syncCallBacks = (SyncCallBacks) this;
        cardViewTryAgain = (CardView) findViewById(R.id.cardViewTryAgain);
//        viewTryAgain=(View)findViewById(R.id.layoutTryAgain);

        // Remove before production, testing purpose only
//        ArrayList<UnitContent> unitContents=new ArrayList<>();
//        UnitContent unitContent=new UnitContent("733","0","410","http://uzs.3b6.myftpupload.com/wp-content/uploads/2019/05/Physical-World-2.pdf","Physical-World-2.pdf",false,true,false);
//        UnitContent unitContent1=new UnitContent("746","0","410","http://uzs.3b6.myftpupload.com/wp-content/uploads/2019/05/Physical-World-2.pdf","Physical-World-2.pdf",false,true,false);
//        unitContents.add(unitContent);
//        unitContents.add(unitContent1);
//        databaseAdapter.insertUnitContentList(unitContents);

        cardViewTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (sharedPref.getAuthToken() == null)
//                    webCallGetToken(sharedPref.getOrgID());
//                else
                showCourses(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showListOfRecentlyViewed();
        newContentCount = databaseAdapter.getNewUnitContentCount();
        setupBadge();
    }


    private void setupBadge() {

        if (textNewContent != null) {
            if (newContentCount <= 0) {
                if (textNewContent.getVisibility() != View.GONE) {
                    textNewContent.setVisibility(View.GONE);
                }
            } else {
                textNewContent.setText(String.valueOf(Math.min(newContentCount, 99)));
                if (textNewContent.getVisibility() != View.VISIBLE) {
                    textNewContent.setVisibility(View.VISIBLE);
                    textNewContent.invalidate();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content_notification, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_new_content);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textNewContent = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_new_content) {
            CommonMethods.startActivity((Activity) context, NewContentActivity.class);
        } else if (id == R.id.action_my_files) {
            CommonMethods.startActivity((Activity) context, MyFilesActivity.class);
        } else if (id == R.id.action_log_out) {
            showDeleteDialog();
//            CommonMethods.startActivity((Activity) context, NewContentActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDeleteDialog() {//}, final String email) {

        final Dialog dialog = new Dialog(CourseListActivity.this,
                R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_confirm);
//        dialog_confirm.setTitle("Confirm attendance");
        dialog.setCancelable(true);

        // there are a lot of settings, for dialog_confirm, check them all out!
        // set up radiobutton
        TextView textViewMessage = (TextView) dialog.findViewById(R.id.tv_message);
        textViewMessage.setText("Click confirm to logout and delete all downloaded files permanently.");
        Button buttonCancel = (Button) dialog.findViewById(R.id.bt_cancel);
        Button buttonOk = (Button) dialog.findViewById(R.id.bt_ok);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logoutFromServer();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void logout() {

        try {
            Utils.deleteDirectory(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        databaseAdapter.clearData();
        sharedPref.setIsLoggedIn(false);
//        Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(context,SplashScreenActivity.class));
        finish();
    }


    private void logoutFromServer() {
        try {
            VolleyInitialization v = new VolleyInitialization(this, true, true);

            WebService.LogOut(v, sharedPref.getUserId(),sharedPref.getDeviceId()+sharedPref.getUserId(), new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                }

                @Override
                public void onVolleySuccess(JSONObject response) {
                    parseResponse(response);
                }

                @Override
                public void onVolleySuccess(String response) {

                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error);
                    Toast.makeText(context, "Failed to logout, please try again later", Toast.LENGTH_SHORT).show();
                    Log.e("######", error.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void logoutFromServer() {
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("id", id);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return;
//        }
//
////        Gson gson = new Gson();
////        String json = gson.toJson(jsonObject.toString());
//
//
//        try {
//            Log.e("#############", jsonObject.toString(4));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        final Map<String, String> params = new HashMap<>();
//        params.put("user_id", sharedPref.getUserId());
//        params.put("device_id", sharedPref.getDeviceId()+sharedPref.getUserId());
//        final Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//
//
//        String myUrl = URLS.URL_LOGOUT;
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.POST, myUrl, jsonObject, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
////                        mTextView.setText("Response: " + response.toString());
//                        try {
//                            Log.e("######", response.toString(4));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        Log.e("######", response.toString());
//                        parseResponse(response);
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO: Handle error
////                        callBack();
//                        Toast.makeText(context, "Failed to logot, please try again later", Toast.LENGTH_SHORT).show();
//                        Log.e("######", error.toString());
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() {
//                return headers;
//            }
//
//        };
//
//// Access the RequestQueue through your singleton class.
//        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
//    }


    private void parseResponse(JSONObject jsonObject) {

        try {

            if (jsonObject.getBoolean("status")) {
                logout();
            }else{
                Log.e("parseResponse","Failed to upload data");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    //TODO working on sync not finished yet

    /**
     * The Foreground Sync service to get latest data from server
     */
    private void sync() {
        Intent intent = new Intent(this.getApplicationContext(), ForeGroundAutoSyncService.class);
        if (!ForeGroundAutoSyncService.isServiceRunning) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.startForegroundService(intent);
            } else
                this.startService(intent);
        } else {
            Toast.makeText(this, "Sync in progress", Toast.LENGTH_SHORT).show();
        }
    }

    //Get all subscribed courses from database if available offline otherwise get it from server
    private void showCourses(boolean isSyncCompleted) {
        //Check if we have offline data
        ArrayList<Course> courses = databaseAdapter.getAllCourses();

        if (!isSyncCompleted && courses.size() <= 0) {
            //Offline data not available, make network call to fetch data
            webCallGetCourses(new SharedPref(context).getAuthToken(), true, true);
        } else {
            //Show all courses
            showList(courses);
        }

    }


    private void showList(ArrayList<Course> courses) {


        //Initialize recycler view
        recyclerViewCourseList = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerViewCourseList.setHasFixedSize(true);

        if (coursesAdapter == null) {
            recyclerViewCourseList.addItemDecoration(new SpaceItems(20));
        }

        coursesAdapter = new CoursesAdapter(courses, context);


        recyclerViewCourseList.setAdapter(coursesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerViewCourseList.setLayoutManager(layoutManager);
    }

    // 10 Recently viewed Unit content will be shown to user
    private void showListOfRecentlyViewed() {

        contents = databaseAdapter.getAllDownloadedFiles("10");
        //10 for recent 10 entries to increase number of element just change that variable
        recyclerViewResentlyAccessed = (RecyclerView) findViewById(R.id.recyclerViewRecent);
        recyclerViewResentlyAccessed.setHasFixedSize(false);

        if (recentAdapter == null)
            recyclerViewResentlyAccessed.addItemDecoration(new SpaceItems(5));

        recentAdapter = new RecentAdapter(contents, context, (UnitDetailsCallBacks) context);
        recyclerViewResentlyAccessed.setAdapter(recentAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerViewResentlyAccessed.setLayoutManager(layoutManager);
    }

    /**
     * Network call to fetch all subscribed courses
     *
     * @param token     Authentication token of user
     * @param isDisplay boolean value to show custom dialog till network call finish
     * @param isCancel  boolean value to cancel custom dialog
     */
    private void webCallGetCourses(String token, boolean isDisplay, boolean isCancel) {
        try {
            VolleyInitialization v = new VolleyInitialization(this, isDisplay, isCancel);

            WebService.GetAllSubscribedCoursesArray(v, token, URLS.getSubscribedCoursesUrl(context), new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                    //Parse the json data and store returned courses into database
                    JsonParser.parseAndStoreCourses(response, context.getApplicationContext());
                    showList(new DatabaseAdapter(context).getAllCourses());

                    CustomLog.i("WebCalls", response.toString());
                    (findViewById(R.id.layoutTryAgain)).setVisibility(View.GONE);
                }

                @Override
                public void onVolleySuccess(JSONObject response) {

                }

                @Override
                public void onVolleySuccess(String response) {

                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error);

                    if (error.contains("AuthFailure")) {
                        webCallGetToken(sharedPref.getOrgID());
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
                public void onVolleySuccess(String response) {

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
    public void onUnitContentSelected(UnitContent unitContent, int position) {
        (new DatabaseAdapter(context)).updateLastUsedFiles(unitContent.getFileName(), unitContent.getCourseId(), unitContent.getSubjectId(), unitContent.getUnitId(), System.currentTimeMillis());
        if (unitContent.isVideo())
            new ContentViewer().viewVideo(unitContent, unitContent.getCourseId() + "_" + unitContent.getSubjectId() + "_" + unitContent.getUnitId() + "_" + unitContent.getFileName(), context);
        else
            new ContentViewer().viewPdf(unitContent, unitContent.getCourseId() + "_" + unitContent.getSubjectId() + "_" + unitContent.getUnitId() + "_" + unitContent.getFileName(), context);
    }

    @Override
    public void onDownloadFinished() {
        Toast.makeText(context, "Download finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadFailed(Exception e) {
        Toast.makeText(context, "Failed to download", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSyncFinished() {
        showCourses(true);
        newContentCount = databaseAdapter.getNewUnitContentCount();
        setupBadge();
    }

    @Override
    public void onSyncFailed(Exception e) {
        showCourses(true);
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
}
