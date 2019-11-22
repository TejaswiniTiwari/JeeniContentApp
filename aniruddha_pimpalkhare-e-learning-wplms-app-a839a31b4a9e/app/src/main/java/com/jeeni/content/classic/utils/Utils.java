/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.jeeni.content.classic.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.jeeni.content.classic.R;
import com.jeeni.content.classic.customviews.CustomLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.jeeni.content.classic.activities.VideoActivity.AES_ALGORITHM;
import static com.jeeni.content.classic.activities.VideoActivity.AES_TRANSFORMATION;

/**
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public final class Utils {

    private static final String DIRECTORY_NAME = "e-LearningApplication";
    public static final String DIRECTORY_MY_FILES = "my_files";
    private static final String LOG_TAG = "Utils";

    private Utils() {
        // no instance
    }

    public static String getRootDirPath(Context context) throws Exception {

        if (externalMemoryAvailable()) {
            File file = new File(context.getExternalFilesDir(null), DIRECTORY_NAME);

            if(!file.exists())
            if (!file.mkdirs()) {
                CustomLog.e(LOG_TAG, "Directory not created");
                throw new Exception("Unable to create directory");
            }

            if(file.exists()){
                File dir = new File(file.getAbsolutePath(),DIRECTORY_MY_FILES);
                if(!dir.exists())
                    if (!dir.mkdirs()) {
                        CustomLog.e(LOG_TAG, "Directory not created");
//                        throw new Exception("Unable to create directory");
                    }
            }

            CustomLog.e("*****###FILE",file.getAbsolutePath());

            return file.getAbsolutePath();
        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));

        }
    }

    public static boolean deleteFile(Context context,String fileName) throws Exception {

        if (externalMemoryAvailable()) {
            File file = new File(context.getExternalFilesDir(null), DIRECTORY_NAME);

            if(file.exists())
            {
                File file1=new File(file,fileName);

                if(file1.exists())
                {
                   return file1.delete();
                }else{
                    throw new Exception("File not found");
                }
            }else{
                throw new Exception("Directory not found");
            }

        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));

        }
    }

    public static void deleteDirectory(Context context) throws Exception {

        if (externalMemoryAvailable()) {
            File dir = new File(context.getExternalFilesDir(null), DIRECTORY_NAME);

            if (dir.isDirectory())
            {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++)
                {

                    File dir1 = new File(dir, children[i]);

//                    if(dir1.isDirectory()){
//                        String[] children1 = dir.list();
//                        for (int i = 0; i < children.length; i++)
//                        {
//                            new File(dir, children[i]).delete();
//                        }
//                    }
//                        else
                            dir1.delete();
                }
            }

        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));

        }
    }

    public static ArrayList<String> getAllAvailableFiles(Context context) throws Exception {

        ArrayList<String> files=new ArrayList<>();
        if (externalMemoryAvailable()) {
            File dir = new File(context.getExternalFilesDir(null), DIRECTORY_NAME);

            if (dir.isDirectory())
            {
                String[] children = dir.list();
                files.addAll(Arrays.asList(children));
            }

        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));

        }

        return files;
    }

//    Get all unencrypted files
    public static ArrayList<String> getAllMyFiles(Context context) throws Exception {

        ArrayList<String> files=new ArrayList<>();
        if (externalMemoryAvailable()) {
            File dir = new File(new File(context.getExternalFilesDir(null), DIRECTORY_NAME).getAbsolutePath(),DIRECTORY_MY_FILES);

            if (dir.isDirectory())
            {
                String[] children = dir.list();
                files.addAll(Arrays.asList(children));
            }

        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));

        }

        return files;
    }


    public static boolean isFileExist(Context context,String fileName) throws Exception {

        if (externalMemoryAvailable()) {
            File file = new File(context.getExternalFilesDir(null), DIRECTORY_NAME);

            if(file.exists())
            {
                File file1=new File(file,fileName);

                if(file1.exists())
                {
                    return true;
                }else{
                    return false;
                }
            }else{
                throw new Exception("Directory not found");
            }

        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));

        }
    }

    public static long getFileSize(Context context,String fileName) throws Exception {

        if (externalMemoryAvailable()) {
            File file = new File(context.getExternalFilesDir(null), DIRECTORY_NAME);

            if(file.exists())
            {
                File file1=new File(file,fileName);

                if(file1.exists())
                {
                    return file1.length();
                }else{
                    return 0;
                }
            }else{
                throw new Exception("Directory not found");
            }

        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));

        }
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableExternalMemorySize(Context context) throws Exception {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;
        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));
        }
    }

    public static long getTotalExternalMemorySize(Context context) throws Exception {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
//            return formatSize(totalBlocks * blockSize);
            return totalBlocks * blockSize;
        } else {
            throw new Exception(context.getApplicationContext().getString(R.string.external_storage_not_available_error));
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

    public boolean hasFile(File mEncryptedFile) {
        return mEncryptedFile != null
                && mEncryptedFile.exists()
                && mEncryptedFile.length() > 0;
    }

    public static Cipher getCipher(Context context, int mode) {

        SharedPref sharedPref = new SharedPref(context.getApplicationContext());

        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(sharedPref.getKey(), AES_ALGORITHM);
            IvParameterSpec mIvParameterSpec = new IvParameterSpec(sharedPref.getIv());

            cipher.init(mode, mSecretKeySpec, mIvParameterSpec);
            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
