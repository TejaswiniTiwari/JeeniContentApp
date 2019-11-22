package com.jeeni.content.classic.volly;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Interface for volley request
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */

public interface OnVolleyHandler {

//    public abstract void onVolleySuccess(String response);
    public abstract void onVolleySuccess(JSONArray response);
    public abstract void onVolleySuccess(JSONObject response);
    public abstract void onVolleySuccess(String response);

    public abstract void onVolleyError(String error);

}
