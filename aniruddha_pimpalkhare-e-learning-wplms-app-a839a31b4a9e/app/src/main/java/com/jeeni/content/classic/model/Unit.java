package com.jeeni.content.classic.model;


import com.jeeni.content.classic.customviews.CustomLog;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class Unit {
    private static final String TAG =Unit.class.getSimpleName();
    private String id;
    private String subjectId;
    private String courseId;
    private String title;
    private String description;
    private long timestamp;

    /**
     * Initializes immutable unit object
     * @param unitId Id of unit
     * @param subjectId Id of subject
     * @param courseId Id of course
     * @param title Title of unit
     * @param description Description of unit
     * @return the id of a unit
     */
    public Unit(String unitId, String subjectId,String courseId,String title, String description,long timestamp) {
        this.id = unitId;
        this.subjectId = subjectId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.timestamp=timestamp;
    }


    /**
     * To get the id of a unit
     *
     * @return the id of a unit
     */
    public String getId() {
        return id;
    }

    /**
     * To get the id of a course
     *
     * @return the id of a course
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * To get the title of a unit
     *
     * @return the Title of a unit
     */
    public String getTitle() {
        return title;
    }

    /**
     * To get the description of a unit
     *
     * @return the description of a unit
     */
    public String getDescription() {
        return description;
    }


    public String getSubjectId() {
        return subjectId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void print(){
        CustomLog.i(TAG,this.id);
        CustomLog.i(TAG,this.courseId);
        CustomLog.i(TAG,this.title);
        CustomLog.i(TAG,this.description);
    }
}
