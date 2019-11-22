package com.jeeni.content.classic.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.jeeni.content.classic.activities.PdfActivity;
import com.jeeni.content.classic.activities.VideoActivity;
import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.model.UnitContent;
import com.jeeni.content.classic.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * An content viewer is a helper class for displaying PDF or Video
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class ContentViewer {

    //To open PDF file using pdf renderer we need to pass file descriptor use temp file name to save decrypted file
    private final static String TEMP_FILENAME = "tempPdf.pdf";
    private static Context context;
    private static UnitContent unitContent;
    private static String fileName;
    public static final String COURSE_NAME="course_name";
    public static final String SUBJECT_NAME="subject_name";
    public static final String UNIT_NAME="unit_name";
    public static final String FILE_NAME="filename";
    public static final String USAGE_PATTERN_FILE_NAME="file_name";
    public static final String FILE_TYPE="file_type";
    public static final String INSERT_USAGE_PATTERN="insert_usage_pattern";

    /**
     * The viewVideo responsible of opening videoView activity
     *
     * @param fileName name of file which yu want to open
     * @param context  context of activity
     */
    public void viewVideo(UnitContent unitContent, String fileName, Context context) {

        DatabaseAdapter databaseAdapter=new DatabaseAdapter(context);
        this.fileName=fileName;
        this.unitContent=unitContent;

        CustomLog.e("PDF",new DatabaseAdapter(context).getCourseTitle(unitContent.getCourseId()));
        CustomLog.e("PDF",new DatabaseAdapter(context).getSubjectTitle(unitContent.getSubjectId()));
        CustomLog.e("PDF",new DatabaseAdapter(context).getUnitTitle(unitContent.getUnitId()));
        CustomLog.e("PDF",fileName);

        try {
            databaseAdapter.updateLatestUnitContent(unitContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            (new DatabaseAdapter(context)).updateLastUsedFiles(fileName,System.currentTimeMillis());
            Intent intent = new Intent(context.getApplicationContext(), VideoActivity.class);
            intent.putExtra("path", Utils.getRootDirPath(context));
            intent.putExtra(FILE_NAME, fileName);
            intent.putExtra(USAGE_PATTERN_FILE_NAME, unitContent.getFileName());
            intent.putExtra(COURSE_NAME, databaseAdapter.getCourseTitle(unitContent.getCourseId()));
            intent.putExtra(SUBJECT_NAME, databaseAdapter.getSubjectTitle(unitContent.getSubjectId()));
            intent.putExtra(UNIT_NAME, databaseAdapter.getUnitTitle(unitContent.getUnitId()));
            intent.putExtra(FILE_TYPE, "VIDEO");
            intent.putExtra(INSERT_USAGE_PATTERN, true);

//            intent.putExtra("course", new DatabaseAdapter(context).getCo);

            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to open file", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * To open encrypted PDF we need to decrypt it into apps private memory
     *
     * @param fileName name of file which yu want to open
     * @param ctx      context of activity
     */
    public void viewPdf(UnitContent unitContent, String fileName, Context ctx) {

        this.fileName=fileName;
        this.unitContent=unitContent;

        context = ctx;
        try {
            new DatabaseAdapter(context).updateLatestUnitContent(unitContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        (new DatabaseAdapter(ctx)).updateLastUsedFiles(fileName,System.currentTimeMillis());
        new CopyFile().execute(fileName);
    }

    /**
     * To open non-encrypted PDF we need to decrypt it into apps private memory
     *
     * @param fileName name of file which yu want to open
     * @param ctx      context of activity
     */
    public void viewNonEncryptedPdf(String fileName, Context ctx) {

        context = ctx;
        try {
            showPdf(context, fileName);
//            new DatabaseAdapter(context).updateLatestUnitContent(unitContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        (new DatabaseAdapter(ctx)).updateLastUsedFiles(fileName,System.currentTimeMillis());
//        new CopyFile().execute(fileName);
    }

    //Open PdfView activity
    private static void showPdf(Context context, String fileName1) {
        Intent intent = new Intent(context.getApplicationContext(), PdfActivity.class);
        if(unitContent!=null){
            DatabaseAdapter databaseAdapter=new DatabaseAdapter(context);
            intent.putExtra(FILE_NAME, fileName);
            intent.putExtra(USAGE_PATTERN_FILE_NAME, unitContent.getFileName());
            intent.putExtra(COURSE_NAME, databaseAdapter.getCourseTitle(unitContent.getCourseId()));
            intent.putExtra(SUBJECT_NAME, databaseAdapter.getSubjectTitle(unitContent.getSubjectId()));
            intent.putExtra(UNIT_NAME, databaseAdapter.getUnitTitle(unitContent.getUnitId()));
            intent.putExtra(FILE_TYPE, "PDF");
            intent.putExtra(INSERT_USAGE_PATTERN, true);
        }else intent.putExtra(INSERT_USAGE_PATTERN, false);

        if (fileName1 != null) {
            try {
                intent.putExtra("path", Utils.getRootDirPath(context) + File.separator + fileName1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            intent.putExtra("path", context.getFilesDir() + "/" + TEMP_FILENAME);
        CustomLog.e("######MYPATH", context.getFilesDir() + "/" + TEMP_FILENAME);
        context.startActivity(intent);
        context = null;
    }


    private static class CopyFile extends AsyncTask<String, Void, String> {
        //
//        private Context context;
//        public CopyFile(Context context){
//            this.context = context;
//        }
        @Override
        protected String doInBackground(String... params) {
            try {
                copyFileToInternal(
                        new File(Utils.getRootDirPath(context) + File.separator + params[0]), context);
            } catch (IOException e) {
                e.printStackTrace();
//                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Failed to open file", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            showPdf(context,null);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


        private void copyFileToInternal(File encryptedFile, Context context) throws IOException {
            // In this sample, we read a PDF from the assets directory.
            File file = new File(context.getFilesDir(), TEMP_FILENAME);
//        mEncryptedFile = new File(getFilesDir(), ENCRYPTED_FILE_NAME);
            if (file.exists())
                file.delete();

            if (!file.exists()) {
                // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
                // the cache directory.

                FileInputStream fileInputStream = new FileInputStream(encryptedFile);
//                InputStream inputStream =  openFileInput(encryptedFile.getAbsolutePath());
                FileOutputStream outputStream = new FileOutputStream(file);
                CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, Utils.getCipher(context, Cipher.DECRYPT_MODE));
                final byte[] buffer = new byte[1024 * 2];
                int size;
                while ((size = fileInputStream.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, size);
                }
                fileInputStream.close();
                cipherOutputStream.close();
            }
        }
    }
}
