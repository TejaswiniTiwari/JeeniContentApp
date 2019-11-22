package com.jeeni.content.classic.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.jeeni.content.classic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * This class is used to store key value pair in SharedPrefs
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */

public class SharedPref {

    private Context context;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SharedPref(Context ctx) {
        context = ctx;
        sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public void setUserName(String userName) {
        editor = sharedPref.edit();
        editor.putString(context.getString(R.string.key_user_name), userName);
        editor.commit();
        editor = null;
    }

    public void setAuthToken(String authToken) {
        editor = sharedPref.edit();
        editor.putString(context.getString(R.string.key_auth_token), authToken);
        editor.commit();
        editor = null;
    }

    public String getAuthToken() {
        return sharedPref.getString(context.getString(R.string.key_auth_token), null);
    }


    public void setUrl(String url) {
        editor = sharedPref.edit();
        editor.putString(context.getString(R.string.key_url), url);
        editor.commit();
        editor = null;
    }

    public String getUrl() {
        return sharedPref.getString(context.getString(R.string.key_url), null);
    }


    public void setUserId(String userId) {
        editor = sharedPref.edit();
        editor.putString(context.getString(R.string.key_user_id), userId);
        editor.commit();
        editor = null;
    }

    public String getUserId() {
        return sharedPref.getString(context.getString(R.string.key_user_id), null);
    }

    public void setDeviceId(String deviceId) {
        editor = sharedPref.edit();
        editor.putString(context.getString(R.string.key_device_id), deviceId);
        editor.commit();
        editor = null;
    }

    public String getDeviceId() {
        return sharedPref.getString(context.getString(R.string.key_device_id), null);
    }

    public String getUserName() {
        return sharedPref.getString(context.getString(R.string.key_user_name), null);
    }

    public void setOrgList(JSONArray orgList) {
        editor = sharedPref.edit();
        editor.putString(context.getString(R.string.key_org_list), orgList.toString());
        editor.commit();
        editor = null;
    }

    public JSONArray getOrgList() {
        try {
            return new JSONArray(sharedPref.getString(context.getString(R.string.key_org_list), null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getOrgID() {
        try {
            return (new JSONArray(sharedPref.getString(context.getString(R.string.key_org_list), null))).getJSONObject(0).getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void setIsFirstLaunch(boolean isFirstLaunch) {
        editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.key_is_first_launch), isFirstLaunch);
        editor.commit();
        editor = null;
    }

    public boolean isFirstLaunch() {
        return sharedPref.getBoolean(context.getString(R.string.key_is_first_launch), true);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.key_is_logged_in), isLoggedIn);
        editor.commit();
        editor = null;
    }

    public boolean IsLoggedIn() {
        return sharedPref.getBoolean(context.getString(R.string.key_is_logged_in), false);
    }

//    public void setIsCorrectionMade(boolean isLoggedIn) {
//        editor = sharedPref.edit();
//        editor.putBoolean(context.getString(R.string.key_is_correction_made), isLoggedIn);
//        editor.commit();
//        editor = null;
//    }
//
//    public boolean IsCorrectionMade() {
//        return sharedPref.getBoolean(context.getString(R.string.key_is_correction_made), false);
//    }

    private void setKey(String key) {
        editor = sharedPref.edit();
        editor.putString(context.getString(R.string.key_encryption_key), key);
        editor.commit();
        editor = null;
    }


    private void setIv(String iv) {
        editor = sharedPref.edit();
        editor.putString(context.getString(R.string.key_Iv), iv);
        editor.commit();
        editor = null;
    }

    public byte[] getKey() {
        String s="74,51,35,110,33,67,48,110,84,101,110,116,65,112,112,126";
        String key = s;//sharedPref.getString(context.getString(R.string.key_encryption_key), null);

        if (key != null)
            return parseKey(key);
        else
            return generateKey();
    }

    public byte[] getIv() {
        String s="74,35,51,110,49,67,48,110,84,51,110,116,65,112,112,126";
        String iv =s;// sharedPref.getString(context.getString(R.string.key_Iv), null);

        if (iv != null)
            return parseKey(iv);
        else
            return generateIv();
    }

    private byte[] parseKey(String sKey) {
        String[] sKeyArray = sKey.split(",");

        byte[] key = new byte[16];

        for (int i = 0; i < sKeyArray.length; i++)
            key[i] = Byte.parseByte(sKeyArray[i]);

        return key;
    }

    private byte[] parseIv(String sIv) {
        String[] sIvArray = sIv.split(",");

        byte[] iv = new byte[16];

        for (int i = 0; i < sIvArray.length; i++)
            iv[i] = Byte.parseByte(sIvArray[i]);

        return iv;
    }

    private byte[] generateKey() {

//        SecureRandom secureRandom = new SecureRandom();
//        byte[] key = new byte[16];
//        secureRandom.nextBytes(key);
//        setKey(Arrays.toString(key).replace(" ","").replace("[", "").replace("]", ""));
//        return key;
        String s="J3#n!C0nTentApp~";
        byte[] key = new byte[16];
        key=s.getBytes();
        setKey(s);
        return key;
    }

    private byte[] generateIv() {
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] iv = new byte[16];
//        secureRandom.nextBytes(iv);
//        setIv(Arrays.toString(iv).replace(" ","").replace("[", "").replace("]", ""));
//        return iv;

//        SecureRandom secureRandom = new SecureRandom();
        String s="J#3n1C0nT3ntApp~";
        byte[] iv = new byte[16];
        iv=s.getBytes();
//        secureRandom.nextBytes(iv);
        setIv(s);
        return iv;
    }
}
