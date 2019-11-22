package com.jeeni.content.classic.customviews;

import android.util.Log;

/**
 * This class isolates Log functionality so that it can be controlled in one place
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class CustomLog {
    private final static boolean isPrint = true;

    public static void i(String Tag, String msg) {
        if (isPrint) {
            Log.i(Tag, msg);
//            Log.w(Tag, msg);
        }
    }

    public static void e(String Tag, String msg) {
        if (isPrint) {
            Log.e(Tag, msg);
//            Log.w(Tag, msg);
        }
    }
}
