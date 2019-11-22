package com.jeeni.content.classic.model;


import com.jeeni.content.classic.customviews.CustomLog;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class UnitContent {
    private static final String TAG =UnitContent.class.getSimpleName();
    private String unitId;
    private String subjectId;
    private String courseId;
    private String url;
    private String fileName;
    private boolean isVideo;
    private boolean isNew;
    private boolean isDownloaded;
    private boolean isDownloading;
    private long progress;

    /**
     * Initializes immutable unit object
     * @param subjectId Id of unit
     * @param courseId Id of course
     * @return the id of a unit
     */
    public UnitContent(String unitId, String subjectId, String courseId,String url,String fileName,boolean isVideo,boolean isNew,boolean isDownloaded) {
        this.unitId = unitId;
        this.subjectId = subjectId;
        this.courseId = courseId;
        this.url=url;
        this.fileName=fileName;
        this.isVideo=isVideo;
        this.isNew=isNew;
        this.isDownloaded=isDownloaded;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getUnitId() {
        return unitId;
    }

    public String getUrl() {
        return url;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isNew() {
        return isNew;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public void print(){
        CustomLog.i(TAG,"Unit fileName: "+this.fileName);
        CustomLog.i(TAG,"Unit ID: "+this.unitId);
        CustomLog.i(TAG,"Subject ID: "+this.subjectId);
        CustomLog.i(TAG,"Course ID: "+this.courseId);
        CustomLog.i(TAG,"URL: "+this.url);
        CustomLog.i(TAG,"isVideo: "+this.isVideo);
    }
}
