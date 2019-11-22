package com.jeeni.content.classic.volly;


import com.android.volley.Request;
import com.jeeni.content.classic.utils.URLS;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Initialize volley requests
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class WebService {

    /**
     * @param volleyInit
     * @param accessToken
     * @param volleyHandler
     * @throws JSONException
     */

    public static void GetAllSubscribedCoursesArray(VolleyInitialization volleyInit, String accessToken,String subscribedCoursesUrl, OnVolleyHandler volleyHandler) throws JSONException {

        String url = subscribedCoursesUrl;
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", accessToken);
        volleyInit.volleyJsonArrayRequestCall(url, Request.Method.GET, params, headers, volleyHandler);
    }


    public static void GetCourseDetails(VolleyInitialization volleyInit,String subscribedCourseDetailsUrl, OnVolleyHandler volleyHandler) throws JSONException {

        String url = subscribedCourseDetailsUrl;
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        volleyInit.volleyJsonArrayRequestCall(url, Request.Method.GET, params, headers, volleyHandler);
    }


    public static void GetUnitDetails(VolleyInitialization volleyInit,String accessToken,String subscribedCourseUnitDetailsUrl, OnVolleyHandler volleyHandler) throws JSONException {

        String url = subscribedCourseUnitDetailsUrl;
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", accessToken);
        volleyInit.volleyJsonRequestCall(url, Request.Method.GET, params, headers, volleyHandler);
    }

    public static void LogOut(VolleyInitialization volleyInit,String user_id,String device_id, OnVolleyHandler volleyHandler) throws JSONException {

        String url = URLS.URL_LOGOUT;
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("device_id", device_id);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        volleyInit.volleyJsonRequestCall(url, Request.Method.POST, params, headers, volleyHandler);
    }

    public static void GetRecentChangesTimestamp(VolleyInitialization volleyInit,String trackUrl,OnVolleyHandler volleyHandler) throws JSONException {

        String url = trackUrl;
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        volleyInit.volleyJsonRequestCall(url, Request.Method.GET, params, headers, volleyHandler);
    }

    public static void AuthenticateUserString(VolleyInitialization volleyInit,String loginId,String password, OnVolleyHandler volleyHandler) throws JSONException {

        String url = URLS.URL_AUTHENTICATION;
        Map<String, String> params = new HashMap<>();
        params.put("loginId", loginId);
        params.put("password", password);
        Map<String, String> headers = new HashMap<>();
        volleyInit.volleyString(url, Request.Method.POST, params, headers, volleyHandler);
    }

    public static void GetAuthenticationToken(VolleyInitialization volleyInit,String orgId,String userId,String deviceId,OnVolleyHandler volleyHandler) throws JSONException {

        String url = URLS.URL_AUTH_TOKEN;
        Map<String, String> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("device_id", deviceId);
        params.put("user_id", userId);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        volleyInit.volleyJsonRequestCall(url, Request.Method.POST, params, headers, volleyHandler);
    }
}

