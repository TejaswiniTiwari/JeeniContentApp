package com.jeeni.content.classic.model;


import com.jeeni.content.classic.customviews.CustomLog;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class Subject {
    private static final String TAG =Subject.class.getSimpleName();
    private String id;//subject id, i.e section id in WPLMS
    private String courseId;
    private String title;
    private boolean isSelected;

    /**
     * Initializes immutable unit object
     * @param title Title of unit
     * @param subjectId Id of unit
     * @param courseId Id of course
     * @return the id of a unit
     */
    public Subject(String title,String subjectId, String courseId) {
        this.title = title;
        this.id = subjectId;
        this.courseId = courseId;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void print(){
        CustomLog.i(TAG,this.id);
        CustomLog.i(TAG,this.courseId);
        CustomLog.i(TAG,this.title);
    }
}
