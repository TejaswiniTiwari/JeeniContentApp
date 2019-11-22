package com.jeeni.content.classic.model;

import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.utils.CommonMethods;
import com.jeeni.content.classic.utils.Utils;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class UsagePattern {
    private static final String TAG = UsagePattern.class.getSimpleName();
    private String id;
    private String unitName;
    private String subjectName;
    private String courseName;
    private String fileName;
    private String fileType;
    private String timeStamp;
    private String duration;


    public UsagePattern(String id, String unitName, String subjectName, String courseName, String fileName, String fileType, String timeStamp, String duration) {
        this.id = id;
        this.unitName = unitName;
        this.subjectName = subjectName;
        this.courseName = courseName;
        this.fileName=fileName;
        this.fileType = fileType;
        this.timeStamp=timeStamp;
        this.duration=duration;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String isFileType() {
        return fileType;
    }

    public void setFileType(String video) {
        fileType = video;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void print(){
        CustomLog.e(TAG,"id "+id +" "+courseName+" "+subjectName+" "+unitName+" "+ fileName +" "+fileType+" "+CommonMethods.getDate(Long.parseLong(timeStamp),"dd/MM/yyyy hh:mm:ss.SSS")+" "+duration);
//        CustomLog.e(TAG,"unitName "+unitName);
//        CustomLog.e(TAG,"subjectName "+subjectName);
//        CustomLog.e(TAG,"courseName "+courseName);
//        CustomLog.e(TAG,"fileName "+fileName);
//        CustomLog.e(TAG,"fileType "+fileType);
//        CustomLog.e(TAG,"timeStamp "+timeStamp);
//        CustomLog.e(TAG,"duration "+duration);
    }
}
