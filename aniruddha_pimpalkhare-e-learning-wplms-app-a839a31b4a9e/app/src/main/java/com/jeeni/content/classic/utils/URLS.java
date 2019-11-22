package com.jeeni.content.classic.utils;

import android.content.Context;

/**
 * Declares all APIs used in application
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class URLS {
    //    private static final String URL = "http://139.59.71.105/";
//    public static final String URL_AUTHENTICATION = "https://exam.jeeni.in/Jeeni/rest/jcalogin";
//    public static final String URL_AUTH_TOKEN = "http://jeeni.fairshare.tech/api/token";
//    public static final String URL_USAGE_PATTERN = "http://jeeni.fairshare.tech/api/usage-pattern";
//    public static final String URL_LOGOUT = "http://jeeni.fairshare.tech/api/logout";

    public static final String URL_AUTHENTICATION = "https://exam.jeeni.in/Jeeni/rest/jcalogin";
    public static final String URL_AUTH_TOKEN = "http://c1.jeeni.in/api/token";
    public static final String URL_USAGE_PATTERN = "http://c1.jeeni.in/api/usage-pattern";
    public static final String URL_LOGOUT = "http://c1.jeeni.in/api/logout";


//    private static final String URL_API = URL + "wp-json/wplms/v1/";
    //Url to get list of subscribed courses
//    public static final String URL_GET_ALL_SUBSCRIBED_COURSES = URL_API + "user/profile/?tab=courses";

    public static String url(Context context){
        SharedPref sharedPref=new SharedPref(context);

        String url=sharedPref.getUrl();
        if(url!=null)
        if(url.charAt(url.length()-1)=='/'){
            return url;
        }else{
            return url+"/";
        }

        return url;
    }


    /**
     * Returns url, to get details of particular course
     *
     * @param courseId ID of interested course
     * @return url in string format
     */
    public static String getSubscribedCourseDetailsUrl(String courseId,Context context) {

        return apiUrl(context) + "course/" + courseId;
    }

    public static String apiUrl(Context context) {

        return url(context) + "wp-json/wplms/v1/";
    }

    public static String getSubscribedCoursesUrl(Context context) {

        return apiUrl(context)  + "user/profile/?tab=courses";
    }

    public static String getTrackUrl(Context context) {

        return apiUrl(context) + "track";
    }

    /**
     * Returns url, to get details of particular unit of particular course
     *
     * @param courseId ID of interested course
     * @param unitId   ID of interested course unit
     * @return url in string format
     */
    public static String getSubscribedCourseUnitDetailsUrl(String courseId, String unitId,Context context) {

        return apiUrl(context) + "user/coursestatus/" + courseId + "/item/" + unitId;
    }

}
