package com.jeeni.content.classic.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jeeni.content.classic.R;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * It maintains commonly used methods in one place
 * e.g:
 * isInternetAvailable
 * setSharedPreferences
 * getSharedPreferences
 * startActivity
 * startActivityForResult
 * finishActivity
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class CommonMethods {


    /**
     * Checking Internet is available or not
     *
     * @param context
     * @return
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



    // call this method when you don't have any data via intent
    public static void startActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);

        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_in_from_left,
                R.anim.push_out_to_right);

    }

    // call this method when you have to pass data in intent
    public static void startActivity(Intent intent, Activity activity) {

        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_in_from_left,
                R.anim.push_out_to_right);

    }

    // call this method when you have to pass data in intent
    public static void startActivityForResult(Intent intent, int resultcode, Activity activity) {

        activity.startActivityForResult(intent, resultcode);
        activity.overridePendingTransition(R.anim.push_in_from_left,
                R.anim.push_out_to_right);

    }

    public static void finishActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(0, R.anim.push_out_to_left);
    }


    public static String convertDateFormat(String oldpattern,
                                           String newPattern, String dateString) {
        // SimpleDateFormat sdf = new SimpleDateFormat(oldpattern);
        SimpleDateFormat sdf = new SimpleDateFormat(oldpattern,
                ConstantVariables.LOCALE_USE_DATEFORMAT);
        Date testDate = null;
        try {
            testDate = sdf.parse(dateString);
            SimpleDateFormat formatter = new SimpleDateFormat(newPattern,
                    ConstantVariables.LOCALE_USE_DATEFORMAT);
            String newFormat = formatter.format(testDate);
            System.out.println("" + newFormat);
            return newFormat;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }


//    public static int getDeviceWidth(Activity activity) {
//
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        return width;
//    }

    public static int[] getDeviceDimensions(Activity activity) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        return new int[]{height,width};
    }

    public void SendLogcatMail(Context context){

        // save logcat in file
        File outputFile = new File(Environment.getExternalStorageDirectory(),
                "logcat.txt");
        try {
            Runtime.getRuntime().exec(
                    "logcat -f " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //send file using email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set type to "email"
//        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"rahul@fairshare.tech"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
//        emailIntent.setType("plain/text");
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse( outputFile.getAbsolutePath()));
//        emailIntent .putExtra(Intent.EXTRA_STREAM, outputFile.getAbsolutePath());
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Log file");
        context.startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    /**
     * Loading Image Progress
     */
    public static void loadImageProgess(ImageView imgProgress, Activity activity) {
        imgProgress.setVisibility(View.VISIBLE);
        Animation a = AnimationUtils.loadAnimation(activity, R.anim.scale);
        a.setDuration(1000);
        imgProgress.startAnimation(a);

        a.setInterpolator(new Interpolator() {
            private final int frameCount = 8;

            @Override
            public float getInterpolation(float input) {
                return (float) Math.floor(input * frameCount) / frameCount;
            }
        });
    }

    public static String Validation(String[] edittextName, TextView... edt) {
        String message = null;
        for (int i = 0; i < edt.length; i++) {
            if (edt[i].getText().length() > 0) {
                message = ConstantVariables.Valid;
            } else {
                message = edittextName[i] + " is required.";
                break;
            }
        }
        return message;
    }

    /**
     * Converts string to Json and
     * Returns value of respective key in String format
     *
     * @param Key the key which value you want to fetch
     * @param response Json in string format
     * @return value of given key
     */
    public static String getValueFromJson(String Key, String response) throws NullPointerException{
        // if you use below code then it will throw null pointer exception when
        // key is not found in jsonResponse
        Gson gson = new Gson();
        ByteArrayInputStream io = new ByteArrayInputStream(
                response.getBytes());
        Reader reader = new InputStreamReader(io);
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        return jsonObject.get(Key).getAsString();
    }

    /**
     * It converts Volley JSONObject to JsonObject and
     * Returns value of respective key in String format
     *
     * @param Key the key which value you want to fetch
     * @param response Json in string format
     * @return value of given key
     */
    public static String getValueFromJson(String Key, JSONObject response) throws NullPointerException {
        // if you use below code then it will throw null pointer exception when
        // key is not found in jsonResponse
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
        return jsonObject.get(Key).getAsString();
    }

    public static boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }


    /**
     * @param intMonthAgo
     * @param dateFormat
     * @return
     */
    public static String getMonthAgoDate(int intMonthAgo, String dateFormat) {
        DateFormat formatter = new SimpleDateFormat(dateFormat,
                ConstantVariables.LOCALE_USE_DATEFORMAT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, intMonthAgo);
        return formatter.format(calendar.getTime());
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        // DateFormat formatter = new SimpleDateFormat(dateFormat);
        DateFormat formatter = new SimpleDateFormat(dateFormat,
                ConstantVariables.LOCALE_USE_DATEFORMAT);

        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getCurrentDate(String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        return getDate(calendar.getTimeInMillis(), dateFormat);
    }


    public static Date convertStringtodate(String strDate) {
        // SimpleDateFormat format = new SimpleDateFormat(
        // CommonVariable.DATABASE_DATE_FORMAT);

        SimpleDateFormat format = new SimpleDateFormat(
                ConstantVariables.DATABASE_DATE_FORMAT);
        Date date = null;
        try {
            date = format.parse(strDate);
            System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Http url parameter value divide in map value
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> splitUrlVariable(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static String getDoubleTwoDecimal(double value)
            throws NumberFormatException {

        DecimalFormat decimalFormat = new DecimalFormat(
                "#0.00");

        return decimalFormat.format(value);
    }

    public static String getFormatedData(String unformatedData) {
        if (unformatedData != null) {
            try {
                //unformatedData.replaceAll(",", "");
                Double result = Double.valueOf(unformatedData);
                DecimalFormat myFormatter = new DecimalFormat("#,##,##0.00");
                //DecimalFormat myFormatter = new DecimalFormat("#,###,###");
                //If you don't want to show .00 format
                return myFormatter.format(result);
            } catch (NumberFormatException e) {
                return unformatedData;
            }

        } else {
            return "0.00";
        }
    }


    public static void showMessage(Context context, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        // alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                /*.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });*/

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static void ShowSnackBar(Activity ac, String msg) {
        Snackbar snackbar = Snackbar
                .make(ac.findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static void shareApp(Context mContext) {
        String shareBody = "Here is the share content body";
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Subject Here");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        mContext.startActivity(Intent.createChooser(sharingIntent,
                "Invite Using...."));
    }

    public static void rateApp(Context mContext) {
        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            mContext.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://play.google.com/store/apps/details?id="
                            + mContext.getPackageName())));
        }
    }

}

