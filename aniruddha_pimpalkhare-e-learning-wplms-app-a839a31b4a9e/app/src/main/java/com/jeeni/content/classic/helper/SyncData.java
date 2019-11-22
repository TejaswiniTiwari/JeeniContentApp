package com.jeeni.content.classic.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jeeni.content.classic.Interfaces.SyncCallBacks;
import com.jeeni.content.classic.activities.CourseDetailsActivity;
import com.jeeni.content.classic.activities.CourseListActivity;
import com.jeeni.content.classic.activities.UnitDetailsActivity;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.model.Course;
import com.jeeni.content.classic.model.Unit;
import com.jeeni.content.classic.model.UsagePattern;
import com.jeeni.content.classic.utils.CommonMethods;
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
import java.util.Iterator;
import java.util.Map;

/**
 * Sync data from server on every launch of application
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class SyncData {

    /*
     * Get timestamp for recent changes
     * Get Subscribed course list
     * If course is updated then add course unit do not fetch unit data
     * If subscribed to new course then add that course to list do not fetch units or its content
     * If Unit is updated then fetch new content list and show as recently added content
     * */
    private static final String TAG = SyncData.class.getSimpleName();
    private Context context;
    private SyncCallBacks syncCallBacks;
    private ArrayList<Course> coursesNew;
    private ArrayList<Unit> unitsNew;
    private ArrayList<Object> objects;//Store all Courses and Units which need update
    private static int index = -1;

    private String token;

    private DatabaseAdapter databaseAdapter;
    private SharedPref sharedPref;
    long startTime;
    long elapsedTime = 0L;


    public SyncData(Context context, String token, SyncCallBacks syncCallBacks) {
        this.context = context;
        this.syncCallBacks = syncCallBacks;
        this.databaseAdapter = new DatabaseAdapter(context);
        this.sharedPref = new SharedPref(context);
        this.token = token;
        objects = new ArrayList<>();
    }

    public void sync() {
//        webCallGetContentTimestamp();
        syncUsagePattern();
    }

    private void syncUsagePattern() {
        UsagePattern usagePattern = getNextUsagePattern();
        if (usagePattern != null)
            logUsagePattern(usagePattern);
        else webCallGetCourses(token);
    }

    private UsagePattern getNextUsagePattern() {
        return databaseAdapter.getUsagePattern();
    }

    //    public static java.lang.String quote(java.lang.String string)

    public void logUsagePattern(final UsagePattern usagePattern) {

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectUsagePattern = new JSONObject();
        try {
            jsonObjectUsagePattern.put("courseName", usagePattern.getCourseName());
            jsonObjectUsagePattern.put("subjectName", usagePattern.getSubjectName());
            jsonObjectUsagePattern.put("unitName", usagePattern.getUnitName());
            jsonObjectUsagePattern.put("fileName", usagePattern.getFileName());
            jsonObjectUsagePattern.put("fileType", usagePattern.isFileType());
            jsonObjectUsagePattern.put("timeStamp", usagePattern.getTimeStamp());
            jsonObjectUsagePattern.put("date", CommonMethods.getDate(Long.parseLong(usagePattern.getTimeStamp()),"dd-MMMM-yyyy"));
            jsonObjectUsagePattern.put("duration", usagePattern.getDuration());
            jsonObjectUsagePattern.put("userId", sharedPref.getUserId());
            jsonObject.put("usagePattern",jsonObjectUsagePattern);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

//        Gson gson = new Gson();
//        String json = gson.toJson(jsonObject.toString());


        try {
            Log.e("#############", jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Map<String, String> params = new HashMap<>();
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

//        JSONObject obj = null;
//        try {
//            obj = new JSONObject(attendance.getJson());
//        } catch (JSONException e) {
//            e.printStackTrace();
//            uploadCallBacks.onUploadFailed(attendance.getId());
//        }


        String myUrl = URLS.URL_USAGE_PATTERN;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, myUrl, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        mTextView.setText("Response: " + response.toString());
                        try {
                            Log.e("######", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("######", response.toString());
                        parseResponse(response,usagePattern.getId());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
//                        callBack();
                        Log.e("######", error.toString());
                        webCallGetCourses(token);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    private void parseResponse(JSONObject jsonObject,String usagePatternId) {

        try {

            if (jsonObject.getBoolean("status")) {

                databaseAdapter.deleteUsagePattern(usagePatternId);
                UsagePattern usagePattern = getNextUsagePattern();
                if (usagePattern != null)
                    logUsagePattern(usagePattern);
                else webCallGetCourses(token);
            }else{
                webCallGetCourses(token);
                Log.e("parseResponse","Failed to upload data");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * Network call to fetch all subscribed courses
     *
     * @param token Authentication token of user
     */
    private void webCallGetCourses(String token) {
        try {
            VolleyInitialization v = new VolleyInitialization(context, false, false);

            WebService.GetAllSubscribedCoursesArray(v, token, URLS.getSubscribedCoursesUrl(context), new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(final JSONArray response) {

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    //Parse the json data and store returned courses into database
                    JsonParser.parseAndStoreCourses(response, context.getApplicationContext());
                    CustomLog.i("WebCalls", response.toString());
                    webCallGetContentTimestamp();
//                        }
//                    }).start();

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
                    //Unable to fetch latest course list work with what we have locally
                    webCallGetContentTimestamp();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void webCallGetContentTimestamp() {
        try {
            VolleyInitialization v = new VolleyInitialization(context, false, false);

            WebService.GetRecentChangesTimestamp(v, URLS.getTrackUrl(context), new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                    //Ignore this will never gona execute because we are making jsonObjectRequest
                }

                @Override
                public void onVolleySuccess(final JSONObject response) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    CustomLog.i("WebCalls", response.toString());
                    parseResentTimestampData(response);
//                        }
//                    }).start();


                }

                @Override
                public void onVolleySuccess(String response) {

                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error.toString());
                    callBack(false, new Exception(error));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Networking call to get course details of specific course
     */
    private void webCallGetCourseDetails(final String courseId) {
        try {
            VolleyInitialization v = new VolleyInitialization(context, false, false);

            WebService.GetCourseDetails(v, URLS.getSubscribedCourseDetailsUrl(courseId, context), new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(final JSONArray response) {

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    JsonParser.parseAndStoreCoursesDetails(courseId, response, context.getApplicationContext());
                    CustomLog.i("WebCalls", response.toString());
                    updateNextData();
//                        }
//                    }).start();

                }

                @Override
                public void onVolleySuccess(String response) {

                }

                @Override
                public void onVolleySuccess(JSONObject response) {
                    //Ignore never going to execute
                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (error.contains("AuthFailure")) {
                        webCallGetToken(new SharedPref(context).getOrgID());
                    } else
                        updateSameData();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Network call to fetch unit details
    private void webCallGetUnitDetails(final String courseId, final String subjectId, final String unitId, boolean isDisplay, boolean isCancel) {
        try {
            VolleyInitialization v = new VolleyInitialization(context, isDisplay, isCancel);

            WebService.GetUnitDetails(v, token, URLS.getSubscribedCourseUnitDetailsUrl(courseId, unitId, context), new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                    //Ignore this will never gona execute because we are making jsonObjectRequest
                }

                @Override
                public void onVolleySuccess(JSONObject response) {
                    JsonParser.parseAndStoreUnitDetails(courseId, subjectId, unitId, response, context.getApplicationContext());
                    updateNextData();
//                    updateSameData();//To simulate long sync
                }

                @Override
                public void onVolleySuccess(String response) {

                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (error.contains("AuthFailure")) {
                        webCallGetToken(new SharedPref(context).getOrgID());
                    } else
                        updateSameData();
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

        try {

            String id=sharedPref.getDeviceId();
            String userId=sharedPref.getUserId();

            if(id==null||id.isEmpty()||userId==null||userId.isEmpty()){
                return;
            }else{
                id+=userId;
            }

            VolleyInitialization v = new VolleyInitialization(context, false, false);

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
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void parseResentTimestampData(JSONObject jsonObject) {

        coursesNew = new ArrayList<>();
        unitsNew = new ArrayList<>();
        try {
            JSONObject jsonObjectCourses = jsonObject.getJSONObject("courses");
            Iterator<String> iteratorCourses = jsonObjectCourses.keys();
            while (iteratorCourses.hasNext()) {
                String key = iteratorCourses.next();
                try {
                    long timestamp = Long.parseLong(jsonObjectCourses.get(key) + "");
                    Course course = new Course(key, "", "", "", "", timestamp);
                    coursesNew.add(course);
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObjectStatusItems = jsonObject.getJSONObject("statusitems");
            Iterator<String> iteratorUnits = jsonObjectStatusItems.keys();
            while (iteratorUnits.hasNext()) {
                String key = iteratorUnits.next();
                try {
                    long timestamp = Long.parseLong(jsonObjectStatusItems.get(key) + "");
                    Unit unit = new Unit(key, "", "", "", "", timestamp);
                    unitsNew.add(unit);
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        compareTimestamp();
    }

    private void compareTimestamp() {
        ArrayList<Course> coursesToBeUpdate = new ArrayList<>();//All courses which needs update
        ArrayList<Unit> unitsToBeUpdate = new ArrayList<>();//All units which needs update
        //Get Local data
        ArrayList<Course> coursesOld = databaseAdapter.getAllCourses();
        ArrayList<Unit> unitsOld = databaseAdapter.getAllUnits();

        //Compare local data with latest data

        for (Course courseOld : coursesOld) {
            for (Course courseNew : coursesNew) {
                if (courseNew.getId().equals(courseOld.getId())) {
                    if (courseOld.getTimestamp() > 0 && courseNew.getTimestamp() > courseOld.getTimestamp())
                        coursesToBeUpdate.add(courseOld);
                }
            }
        }

        for (Unit unitOld : unitsOld) {
            for (Unit unitNew : unitsNew) {
                if (unitNew.getId().equals(unitOld.getId())) {
                    if (unitOld.getTimestamp() > 0 && unitNew.getTimestamp() > unitOld.getTimestamp())
                        unitsToBeUpdate.add(unitOld);
                }
            }
        }

        objects.addAll(coursesToBeUpdate);
        objects.addAll(unitsToBeUpdate);

        updateNextData();

    }

    private void updateNextData() {
        index++;
        updateData();
    }

    private void updateSameData() {
        updateData();
    }

    private void updateData() {

        CustomLog.e(TAG, "Index " + index);
        CustomLog.e(TAG, "Total objects " + objects.size());

        if (index >= objects.size()) {
            callBack(true, null);
            return;
        }


        if (objects.get(index) instanceof Course) {
            //Get course details
            webCallGetCourseDetails(((Course) objects.get(index)).getId());
        } else if (objects.get(index) instanceof Unit) {
            Unit unit = (Unit) objects.get(index);
            webCallGetUnitDetails(unit.getCourseId(), unit.getSubjectId(), unit.getId(), false, false);
        } else {
            CustomLog.i("####**####", "updateData is not working");
        }
    }

    private void callBack(boolean isSuccess, Exception e) {
        if (isSuccess) {
            if (syncCallBacks != null) {
                syncCallBacks.onSyncFinished();
            }

            if (CourseListActivity.syncCallBacks != null)
                CourseListActivity.syncCallBacks.onSyncFinished();

            if (CourseDetailsActivity.syncCallBacks != null)
                CourseDetailsActivity.syncCallBacks.onSyncFinished();

            if (UnitDetailsActivity.syncCallBacks != null)
                UnitDetailsActivity.syncCallBacks.onSyncFinished();

        } else {
            if (syncCallBacks != null) {
                syncCallBacks.onSyncFailed(e);
            }

            if (CourseListActivity.syncCallBacks != null)
                CourseListActivity.syncCallBacks.onSyncFailed(e);

            if (CourseDetailsActivity.syncCallBacks != null)
                CourseDetailsActivity.syncCallBacks.onSyncFailed(e);

            if (UnitDetailsActivity.syncCallBacks != null)
                UnitDetailsActivity.syncCallBacks.onSyncFailed(e);
        }

    }

}
