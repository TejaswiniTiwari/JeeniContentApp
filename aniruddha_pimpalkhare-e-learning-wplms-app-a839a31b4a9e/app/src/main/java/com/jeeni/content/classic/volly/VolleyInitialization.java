package com.jeeni.content.classic.volly;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.jeeni.content.classic.R;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * To Initialize volley object
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class VolleyInitialization {
    private Activity activity;
    private Context context;
    private boolean isShowPopup;
    private boolean isDismissPopup;
    private CustomProgressDialog customProgressDialog;

    public VolleyInitialization(Activity activity, boolean showPopup, boolean dismissPopup) {
        this.activity = activity;
        this.context = activity;
        isShowPopup = showPopup;
        isDismissPopup = dismissPopup;
    }

    public VolleyInitialization(Context context, boolean showPopup, boolean dismissPopup) {
        this.context = context;
        isShowPopup = showPopup;
        isDismissPopup = dismissPopup;
    }

    public Activity getActivity() {
        return activity;
    }

    public boolean getShowPopup() {
        return isShowPopup;
    }

    public boolean getDismissPopup() {
        return isDismissPopup;
    }

    //Volley Webservice Related Methods //

//    /**
//     * Webservice call with Map Key pair value and after response not
//     * any ws call use this method(single boolean) for dialog dismiss
//     *
//     * @param url
//     * @param requestMethod
//     * @param volleyHandler
//     */
//    public void volleyStringRequestCall(String url, final int requestMethod, final Map<String, String> params, final OnVolleyHandler volleyHandler) throws JSONException {
//        //AS we have to pass Security key in ever webservice we have
////        if (json != null) {
////            json.put("strSecurityKey", CV.SECURITY_KEY);
////        }
//        if (!CommonMethods.isInternetAvailable(activity)) {
//            volleyHandler.onVolleyError(activity.getResources().getString(R.string.msg_internet_unavailable_msg));
//            return;
//        }
//        if (isShowPopup) {
//            showCustomDialog(activity);
//        }
//        Log.i("WebCalls", url);
//        StringRequest stringRequest = new StringRequest(requestMethod,
//                url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("response##", response);
//                        String strResponseStatus = CommonMethods.getValueFromJson(WebServiceTag.WEB_STATUS, response);
//                        if (strResponseStatus.equalsIgnoreCase(WebServiceTag.WEB_STATUS_FAIL)) {
//                            dismissCustomDialog(activity);
//                        } else {
//                            if (isDismissPopup) {
//                                dismissCustomDialog(activity);
//                            }
//                        }
//                        volleyHandler.onVolleySuccess(response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("Webcalls", "error=" + error.getMessage());
//                dismissCustomDialog(activity);
//                String errorSet = getActivity().getResources().getString(R.string.msg_networkerror);
//                volleyHandler.onVolleyError(errorSet);
//
//            }
//        }) {
//            /*@Override
//            public String getBodyContentType() {
//                // TODO Auto-generated method stub
//                return "application/json";
//            }*/
//
//           /* @Override
//            public byte[] getBody() {
//
//                Log.i("Webcalls", "Json=" + json.toString());
//                try {
//                    return json.toString().getBytes("utf-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }*/
//
//            @Override
//            protected Map<String, String> getParams() {
//                Log.i("Webcalls", "params=" + params.toString());
//                return params;
//            }
//        };
//        ((LearningApp) activity.getApplicationContext()).volley.addToRequestQueue(stringRequest);
//
//    }

    /**
     * Webservice call with Map Key pair value and after response not
     * any ws call use this method(single boolean) for dialog dismiss
     *
     * @param url           url to get Json object
     * @param requestMethod method type like POST or GET
     * @param volleyHandler response handler
     */
    public void volleyJsonRequestCall(String url, final int requestMethod, final Map<String, String> params, final Map<String, String> headers, final OnVolleyHandler volleyHandler) {

        if (!CommonMethods.isInternetAvailable(context)) {
            volleyHandler.onVolleyError(context.getResources().getString(R.string.msg_internet_unavailable_msg));
            return;
        }
        if (isShowPopup) {
            showCustomDialog(activity);
        }
        CustomLog.i("WebCalls", url);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(requestMethod,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (isDismissPopup) {
                            dismissCustomDialog(activity);
                        }
                        volleyHandler.onVolleySuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomLog.i("Webcalls", "error=" + error.toString());
                VolleyLog.e("Error: ", error.getMessage());
                VolleyLog.e("Error: ", error.getCause());

                if (isDismissPopup)
                    dismissCustomDialog(activity);
                String errorSet = context.getResources().getString(R.string.msg_networkerror);
                volleyHandler.onVolleyError(errorSet);
            }
        }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json";
//            }

            @Override
            public Map<String, String> getHeaders() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                20_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonRequest);
//        ((LearningApp) activity.getApplicationContext()).volley.addToRequestQueue(jsonRequest);
    }


    public void volleyString(String url, final int requestMethod, final Map<String, String> params, final Map<String, String> headers, final OnVolleyHandler volleyHandler) {

        if (!CommonMethods.isInternetAvailable(context)) {
            volleyHandler.onVolleyError(context.getResources().getString(R.string.msg_internet_unavailable_msg));
            return;
        }
        if (isShowPopup) {
            showCustomDialog(activity);
        }
        CustomLog.i("WebCalls", url);


        StringRequest stringRequest = new StringRequest(requestMethod,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (isDismissPopup) {
                            dismissCustomDialog(activity);
                        }
                        volleyHandler.onVolleySuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { CustomLog.i("Webcalls", "error=" + error.toString());
                        VolleyLog.e("Error: ", error.getMessage());
                        VolleyLog.e("Error: ", error.getCause());

                        if (isDismissPopup)
                            dismissCustomDialog(activity);
                        String errorSet = context.getResources().getString(R.string.msg_networkerror);
                        volleyHandler.onVolleyError(errorSet);CustomLog.i("VOLLEY",error.toString());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
//        ((LearningApp) activity.getApplicationContext()).volley.addToRequestQueue(jsonRequest);
    }

    /**
     * Webservice call with Map Key pair value and after response not
     * any ws call use this method(single boolean) for dialog dismiss
     *
     * @param url           url to get Json object
     * @param requestMethod method type like POST or GET
     * @param volleyHandler response handler
     */
    public void volleyJsonArrayRequestCall(String url, final int requestMethod, final Map<String, String> params, final Map<String, String> headers, final OnVolleyHandler volleyHandler) throws JSONException {
        CustomLog.e("response##", url);
        CustomLog.e("response##", "Making Course list request");
        if (!CommonMethods.isInternetAvailable(context)) {
            volleyHandler.onVolleyError(context.getResources().getString(R.string.msg_internet_unavailable_msg));
            return;
        }
        if (isShowPopup) {
            showCustomDialog(activity);
        }
        CustomLog.i("WebCalls", url);


        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                requestMethod,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            CustomLog.e("response##", response.toString(4));
//                            String strResponseStatus = CommonMethods.getValueFromJson(WebServiceTag.WEB_STATUS, response.getJSONObject(0));
//                            if (strResponseStatus.equalsIgnoreCase(WebServiceTag.WEB_STATUS_FAIL)) {
//                                dismissCustomDialog(activity);
//                            } else {

                            if (isDismissPopup) {
                                dismissCustomDialog(activity);
                            }
//                            }
                            volleyHandler.onVolleySuccess(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorSet = context.getResources().getString(R.string.msg_networkerror);
                        if (error instanceof TimeoutError) {
                            errorSet = "TimeoutError: " + error.toString();
                            CustomLog.e("Webcalls", "TimeoutError: " + error.toString());
                        } else if (error instanceof NoConnectionError) {
                            CustomLog.e("Webcalls", "NoConnectionError: " + error.toString());
                            errorSet = "NoConnectionError: " + error.toString();
                        } else if (error instanceof AuthFailureError) {
                            errorSet = "AuthFailureError: " + error.toString();
                            CustomLog.e("Webcalls", "AuthFailureError: " + error.toString());
                        } else if (error instanceof ServerError) {
                            errorSet = "ServerError: " + error.toString();
                            CustomLog.e("Webcalls", "ServerError: " + error.toString());
                        } else if (error instanceof NetworkError) {
                            errorSet = "NetworkError: " + error.toString();
                            CustomLog.e("Webcalls", "NetworkError: " + error.toString());
                        } else if (error instanceof ParseError) {
                            errorSet = "ParseError: " + error.toString();
                            CustomLog.e("Webcalls", "ParseError: " + error.toString());
                        }
                        CustomLog.e("Webcalls", "error: " + error.toString());
                        VolleyLog.e("Error: ", error.getMessage());
                        VolleyLog.e("Error: ", error.getCause());

                        dismissCustomDialog(activity);

                        volleyHandler.onVolleyError(errorSet);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                20_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
//        ((LearningApp) activity.getApplicationContext()).volley.addToRequestQueue(jsonRequest);
    }


    //Volley Webservice Related Methods End //
    public void showCustomDialog(Activity activity) {
        if (activity.isFinishing()) {
            return;
        }
        if (customProgressDialog == null)
            customProgressDialog = new CustomProgressDialog(activity, "", false);
        if (!customProgressDialog.isShowing())
            customProgressDialog.show();
    }

    private void dismissCustomDialog(Activity activity) {

        if (customProgressDialog != null && customProgressDialog.isShowing())
            customProgressDialog.dismiss();
    }


}
