package com.jeeni.content.classic.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeeni.content.classic.Interfaces.CourseDetailsCallBacks;
import com.jeeni.content.classic.Interfaces.SyncCallBacks;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.adapters.SubjectsAdapter;
import com.jeeni.content.classic.adapters.UnitsAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.model.Subject;
import com.jeeni.content.classic.model.Unit;
import com.jeeni.content.classic.utils.CommonMethods;
import com.jeeni.content.classic.utils.JsonParser;
import com.jeeni.content.classic.utils.SharedPref;
import com.jeeni.content.classic.utils.URLS;
import com.jeeni.content.classic.volly.OnVolleyHandler;
import com.jeeni.content.classic.volly.VolleyInitialization;
import com.jeeni.content.classic.volly.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Display course details
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class CourseDetailsActivity extends AppCompatActivity implements CourseDetailsCallBacks, SyncCallBacks {

    //To store context of the activity
    private Context context;
    private DatabaseAdapter databaseAdapter;
    //The Id of user selected course
    private String courseId;
    //The ArrayList of All Units which belongs to selected course
    private ArrayList<Unit> allUnits;
    //The ArrayList of All Subjects which belongs to selected course
    public static ArrayList<Subject> subjects;
    //The ArrayList of filtered Units which belongs to selected subject
    private ArrayList<Unit> filteredUnits;
    //The selected subjectId whose units being display
    private String subjectId;
    private RecyclerView recyclerViewUnit;
    private RecyclerView recyclerViewSubject;
    private SubjectsAdapter subjectsAdapter;
    private UnitsAdapter unitsAdapter;
    //The Name of selected course and subject so that we can pass it to UnitDetails.class
    private String courseName;
    private String subjectName;

    private CardView cardViewTryAgain;

    public static SyncCallBacks syncCallBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        initializeVariables();
        showCoursesDetails(false);
    }

    /**
     * Initialize all the global variables, it will be executed only once.
     */
    private void initializeVariables() {
        context = this;
        syncCallBacks=(SyncCallBacks)this;
        recyclerViewUnit = (RecyclerView) findViewById(R.id.recyclerViewUnits);
        recyclerViewSubject = (RecyclerView) findViewById(R.id.recyclerViewSubjects);
        TextView textViewSubTitle = (TextView) findViewById(R.id.textViewSubTitle);
        cardViewTryAgain=(CardView)findViewById(R.id.cardViewTryAgain);

        courseId = getIntent().getStringExtra("courseId");
        databaseAdapter = new DatabaseAdapter(context);
        filteredUnits = new ArrayList<>();
        allUnits = new ArrayList<>();
        courseName = getIntent().getStringExtra("courseName");
        textViewSubTitle.setText(courseName);

        cardViewTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCoursesDetails(false);
            }
        });
    }

    private void showCoursesDetails(boolean isSyncCompleted) {
        //Check if we have offline data
        allUnits = databaseAdapter.getUnits(courseId);
        subjects = databaseAdapter.getSubjects(courseId);

        if (!isSyncCompleted && (allUnits.size() <= 0 || subjects.size() <= 0)) {
            //Offline data not available, make network call to fetch data
            webCallGetCourseDetails(courseId, true, true);
        } else {
            //Data available filter it based on selected subject
            filterUnits();
        }

    }

    /**
     * unit belongs to subject and subject belongs to course
     * We are fetching all the filteredUnits which belongs to specific course
     * to filter them subject wise I am using filterUnits method
     */
    private void filterUnits() {

        //Show filteredUnits which belongs to given subject id
        try {

            if(filteredUnits!=null)
                filteredUnits.clear();

            if (allUnits.size() <= 0 || subjects.size() <= 0) {
                showSubjectList();
                showUnitList(filteredUnits);
                return;
            }


            if (subjectId == null) {
                subjectId = subjects.get(0).getId();
                subjectName = subjects.get(0).getTitle();
            }

            for (Subject subject : subjects) {
                if (subject.getId().equals(subjectId))
                    subject.setSelected(true);
                else subject.setSelected(false);
            }

            filteredUnits.clear();
            if (subjectId != null)
                for (Unit unit : allUnits) {
                    if (unit.getSubjectId().equals(subjectId))
                        filteredUnits.add(unit);
                }


        } catch (Exception e) {
            e.printStackTrace();
        }

        showSubjectList();
        showUnitList(filteredUnits);
    }

    /**
     * List is being displayed using Adapter and respective viewHolder class
     */
    private void showUnitList(ArrayList<Unit> units) {

        if (unitsAdapter != null) {
            unitsAdapter.notifyDataSetChanged();
            return;
        }

        recyclerViewUnit.setHasFixedSize(true);
        unitsAdapter = new UnitsAdapter(units, courseName, subjectName, context);
        recyclerViewUnit.setAdapter(unitsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerViewUnit.setLayoutManager(layoutManager);
    }

    /**
     * List is being displayed using Adapter and respective viewHolder class
     */
    private void showSubjectList() {

        if (subjectsAdapter != null) {
            subjectsAdapter.notifyDataSetChanged();
            return;
        }

        recyclerViewSubject.setHasFixedSize(true);
        subjectsAdapter = new SubjectsAdapter(subjects, context, (CourseDetailsCallBacks) this);
        recyclerViewSubject.setAdapter(subjectsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayout.HORIZONTAL);
        recyclerViewSubject.setLayoutManager(layoutManager);
    }

    /**
     * Networking call to get course details of specific course
     */
    private void webCallGetCourseDetails(final String courseId, boolean isDisplay, boolean isCancel) {
        try {
            VolleyInitialization v = new VolleyInitialization(this, isDisplay, isCancel);

            WebService.GetCourseDetails(v, URLS.getSubscribedCourseDetailsUrl(courseId,context), new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                    JsonParser.parseAndStoreCoursesDetails(courseId, response, context.getApplicationContext());
                    allUnits.clear();
                    allUnits = new DatabaseAdapter(context).getUnits(courseId);
                    subjects = new DatabaseAdapter(context).getSubjects(courseId);
                    filterUnits();

                    CustomLog.i("WebCalls", response.toString());
                    (findViewById(R.id.layoutTryAgain)).setVisibility(View.GONE);
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

    /*
     * Call back method to get selected subject so that we can filter and show respective units
     * */
    @Override
    public void onSubjectSelected(Subject subject, int position) {
        subjectName = subject.getTitle();
        unitsAdapter.setSubjectName(subjectName);
        subjectId = subject.getId();
        filterUnits();
    }

    @Override
    public void onSyncFinished() {
        showCoursesDetails(true);
    }

    @Override
    public void onSyncFailed(Exception e) {
        showCoursesDetails(true);
    }
}
