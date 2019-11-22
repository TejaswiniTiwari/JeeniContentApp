package com.jeeni.content.classic.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.model.UnitContent;
import com.jeeni.content.classic.model.UsagePattern;
import com.jeeni.content.classic.utils.CommonMethods;
import com.jeeni.content.classic.utils.JsonParser;
import com.jeeni.content.classic.utils.SharedPref;
import com.jeeni.content.classic.utils.Utils;
import com.jeeni.content.classic.volly.OnVolleyHandler;
import com.jeeni.content.classic.volly.VolleyInitialization;
import com.jeeni.content.classic.volly.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 11111;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    //    private View mProgressView;
    private View tokenView;
    private View mLoginForm;
    private Context context;
    private SharedPref sharedPref;
    private CheckBox checkBoxTandC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        sharedPref = new SharedPref(context);
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        checkBoxTandC = (CheckBox) findViewById(R.id.checkBoxTandC);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        tokenView = findViewById(R.id.ll_token);
        mLoginForm = findViewById(R.id.login_form);
        if (sharedPref.IsLoggedIn()) {
            tokenView.setVisibility(View.VISIBLE);
            mLoginForm.setVisibility(View.GONE);
            webCallGetToken(sharedPref.getOrgID());
        } else {
            tokenView.setVisibility(View.GONE);
            mLoginForm.setVisibility(View.VISIBLE);
        }

        CardView cardViewTryAgain = (CardView) findViewById(R.id.cardViewTryAgain);
        cardViewTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPref.IsLoggedIn())
                    webCallGetToken(sharedPref.getOrgID());
                else {
                    startActivity(new Intent(context, SplashScreenActivity.class));
                    finish();
                }
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
//                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
//            }
//        }

//        mProgressView = findViewById(R.id.login_progress);
    }


//    private void checkRunTimePermission() {
//        String[] permissionArrays = new String[]{Manifest.permission.READ_PHONE_STATE};
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
//                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
//                return;
//            }
//            requestPermissions(permissionArrays, 11111);
//        } else {
//            // if already permition granted
//            // PUT YOUR ACTION (Like Open cemara etc..)
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 11111) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // user GRANTED the permission
//                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                try {
//                   @SuppressLint("MissingPermission") String id= telephonyManager.getDeviceId();
                    String android_id = Settings.Secure.getString(this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    new SharedPref(context).setDeviceId(android_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    public void signIn(View view) {
//        startActivity(new Intent(LoginActivity.this,CourseListActivity.class));
//    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
//                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
//                return;
//            }
//        }


        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userName address.
        if (TextUtils.isEmpty(userName)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            if (!checkBoxTandC.isChecked()) {
                Toast.makeText(context, "Please click check box if you agree with our Terms and Conditions.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            webCallAuthenticate(userName, password, true, true);
//            mAuthTask = new UserLoginTask(userName, password);
//            mAuthTask.execute((Void) null);
        }
    }

//    private boolean isEmailValid(String email) {
//        //TODO: Replace this with your own logic
//        return email.contains("@");
//    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            }
//        });
//
//        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//        mProgressView.animate().setDuration(shortAnimTime).alpha(
//                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            }
//        });
//    }


    /**
     * Network call to fetch all subscribed courses
     *
     * @param loginId   loginId of user Jeeni Auth server
     * @param password  password of user Jeeni Auth server
     * @param isDisplay boolean value to show custom dialog till network call finish
     * @param isCancel  boolean value to cancel custom dialog
     */
    private void webCallAuthenticate(final String loginId, String password, boolean isDisplay, boolean isCancel) {
        try {
            VolleyInitialization v = new VolleyInitialization(this, isDisplay, isCancel);

            WebService.AuthenticateUserString(v, loginId, password, new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                }

                @Override
                public void onVolleySuccess(JSONObject response) {

                }

                @Override
                public void onVolleySuccess(String response) {
                    CustomLog.i("WebCalls", response.toString());
                    //Parse Jeeni Auth server response
                    boolean isSuccess = false;
                    try {
                        isSuccess = JsonParser.parseAndStoreUserDetails(new JSONObject(response), context.getApplicationContext());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (isSuccess) {
                        sharedPref.setUserId(loginId);
                        webCallGetToken(sharedPref.getOrgID());
                    } else onVolleyError("Unauthorised Student");
                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error);
                    CommonMethods.showMessage(context, error);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Network call to fetch Auth Token from laravel server
     *
     * @param orgId Organization ID
     */
    private void webCallGetToken(String orgId) {

        String deviceId = sharedPref.getDeviceId();

        if (deviceId == null || deviceId.isEmpty()) {
            sharedPref.setIsLoggedIn(false);
            startActivity(new Intent(context, SplashScreenActivity.class));
        }
        String userId = sharedPref.getUserId();
        if (userId == null || userId.isEmpty()) {
            sharedPref.setIsLoggedIn(false);
            startActivity(new Intent(context, SplashScreenActivity.class));
        }

        deviceId += userId;

        try {
            tokenView.setVisibility(View.VISIBLE);
            mLoginForm.setVisibility(View.GONE);
            findViewById(R.id.layoutTryAgain).setVisibility(View.GONE);


            VolleyInitialization v = new VolleyInitialization(this, false, false);

            WebService.GetAuthenticationToken(v, orgId, userId, deviceId, new OnVolleyHandler() {
                @Override
                public void onVolleySuccess(JSONArray response) {

                }

                @Override
                public void onVolleySuccess(String response) {

                }

                @Override
                public void onVolleySuccess(JSONObject response) {
                    CustomLog.i("WebCalls", response.toString());
                    //Parse Jeeni Auth server response
                    boolean isSuccess = JsonParser.parseAndStoreAuthToken(response, context.getApplicationContext());
                    String errorMessage = JsonParser.getErrorMessageFromJson(response);
                    if (isSuccess)
                        logIn();
                    else {

                        sharedPref.setIsLoggedIn(false);

                        if (errorMessage != null) {
                            if (errorMessage.contains("organization does not exist")) {
                                onVolleyError(errorMessage);
                            } else if (errorMessage.contains("reached max limit")) {
                                onVolleyError(errorMessage);
                            } else if (errorMessage.contains("Something went wrong")) {
                                onVolleyError(errorMessage);
                            } else if (errorMessage.contains("Please get latest app")) {
                                onVolleyError(errorMessage);
                            }

                        } else
                            onVolleyError("Failed to initialize, please try again later");
                    }
                }

                @Override
                public void onVolleyError(String error) {
                    CustomLog.i("WebCalls", error);
                    CommonMethods.showMessage(context, error);
                    tokenView.setVisibility(View.GONE);
                    findViewById(R.id.layoutTryAgain).setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logIn() {
        //Check for files and sync with database

        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);

        if (databaseAdapter.getAllCourses().size() <= 0)
            syncDatabase();

        startActivity(new Intent(context, CourseListActivity.class));
        finish();
    }

    public void syncDatabase() {
        try {
            ArrayList<String> offlineFiles = Utils.getAllAvailableFiles(context);
            //Course Subject Unit
            ArrayList<UnitContent> unitContents = new ArrayList<>();

            for (String s : offlineFiles) {
                if (!s.isEmpty()) {
                    String fileName = "";
                    String[] data = s.split("_");

                    if (data.length >= 4) {
                        if (data.length > 4) {
                            for (int i = 3; i < data.length; i++) {
                                if (!fileName.isEmpty())
                                    fileName += "_";
                                fileName += data[i];
                            }
                        } else {
                            fileName = data[3];
                        }


                        UnitContent unitContent = new UnitContent(data[2], data[1], data[0], "", fileName, (!fileName.contains(".pdf")), true, true);
//                        Log.e("OFFLINE DATA",s);
//                        unitContent.print();
                        unitContents.add(unitContent);
                    }

                }

            }

            for (UnitContent unitContent : unitContents) {

                try {
                    (new DatabaseAdapter(context)).insertDownloads(unitContent, Utils.getFileSize(context, unitContent.getFileName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openTermsAndConditions(View view) {
        try {
//            Connect cd = new ConnectionDetector(getApplicationContext());
            if (CommonMethods.isInternetAvailable(context.getApplicationContext())) {
                Uri uri = Uri.parse("https://jeeni.in/terms-of-use/");

                // create an intent builder
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

                // Begin customizing
                // set toolbar colors
                intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                // set start and exit animations
                //            intentBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
                //            intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,
                //                    android.R.anim.slide_out_right);

                // build custom tabs intent
                CustomTabsIntent customTabsIntent = intentBuilder.build();

                // launch the url
                customTabsIntent.launchUrl(LoginActivity.this, uri);
            } else {
                Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.msg_internet_unavailable_msg), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
