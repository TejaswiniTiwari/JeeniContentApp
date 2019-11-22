package com.jeeni.content.classic.model;

import com.jeeni.content.classic.customviews.CustomLog;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class Course {

    private static final String TAG =Course.class.getSimpleName();
    private String id;
    private String title;
    private String instructor;
    private String description;
    private String url;//To show course icon
    private long timestamp;


    public Course( String id,String title,String instructor, String description, String url,long timestamp) {
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.id = id;
        this.url = url;
        this.timestamp=timestamp;
    }


    /**
     * To get the id of a course
     *
     * @return the id of a course
     */
    public String getId() {
        return id;
    }

    /**
     * To get the title of a course
     *
     * @return the Title of a course
     */
    public String getTitle() {
        return title;
    }

    /**
     * To get the description of a course
     *
     * @return the description of a course
     */
    public String getDescription() {
        return description;
    }


    /**
     * To get the url of a course
     *
     * @return the url of a course
     */
    public String getUrl() {
        return url;
    }

    public String getInstructor() {
        return instructor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void print(){
        CustomLog.i(TAG,this.id);
        CustomLog.i(TAG,this.title);
        CustomLog.i(TAG,this.description);
        CustomLog.i(TAG,this.url);
    }
}
