package com.jeeni.content.classic.model;


import com.jeeni.content.classic.customviews.CustomLog;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class UserActivity {
    private static final String TAG =UserActivity.class.getSimpleName();
    private String id;
    private String courseId;
    private String courseTitle;
    private String unitID;
    private String unitTitle;
    private int counter;


    public UserActivity(String id, String courseId, String courseTitle, String unitID, String unitTitle, int counter) {
        this.id = id;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.unitID = unitID;
        this.unitTitle = unitTitle;
        this.counter = counter;
    }

    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getUnitID() {
        return unitID;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public int getCounter() {
        return counter;
    }

    public void print(){
        CustomLog.i(TAG,this.id);
        CustomLog.i(TAG,this.courseId);
        CustomLog.i(TAG,this.courseTitle);
        CustomLog.i(TAG,this.unitID);
        CustomLog.i(TAG,this.unitTitle);
        CustomLog.i(TAG,String.valueOf(this.counter));
    }
}
