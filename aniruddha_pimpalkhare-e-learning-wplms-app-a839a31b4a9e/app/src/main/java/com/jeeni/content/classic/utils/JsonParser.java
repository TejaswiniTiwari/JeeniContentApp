package com.jeeni.content.classic.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jeeni.content.classic.adapters.DatabaseAdapter;
import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.model.Course;
import com.jeeni.content.classic.model.Subject;
import com.jeeni.content.classic.model.Unit;
import com.jeeni.content.classic.model.UnitContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Parse Json response and store it in database
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class JsonParser {

    public static void parseAndStoreCourses(JSONArray JsonArray, Context context) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
        ArrayList<Course> coursesOld = new ArrayList<>();
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<Course> coursesToBeInserted = new ArrayList<>();
        long timestamp = 0;//Initially timestamp will be 0 I will update it once user fetch its data
        coursesOld = databaseAdapter.getAllCourses();

        for (int i = 0; i < JsonArray.length(); i++) {
            try {
                JSONObject jsonObject = JsonArray.getJSONObject(i);
                JSONObject jsonObjectInstructor = jsonObject.getJSONObject("instructor");
                Course course = new Course(
                        jsonObject.getString("id"),
                        jsonObject.getString("name"),
                        jsonObjectInstructor.getString("name"),
                        "",
                        jsonObject.getString("featured_image"),
                        timestamp
                );

                courses.add(course);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Check if update is required else store as is
        if (coursesOld.size() > 0) {
            //Update required
            //Check if timestamp is latest

            boolean isFound = false;
            for (Course courseNew : courses) {
                isFound = false;
                for (Course courseOld : coursesOld) {
                    if (courseOld.getId().equals(courseNew.getId())) {
                        isFound = true;
//                        if (courseOld.getTimestamp() < courseNew.getTimestamp()) {
//                            coursesToBeInserted.add(courseNew);
//                        }
                        break;
                    }
                }

                if (!isFound) {
                    coursesToBeInserted.add(courseNew);
                }
            }

            databaseAdapter.insertCourseList(coursesToBeInserted);


            for (Course courseOld : coursesOld) {
                isFound = false;

                for (Course courseNew : courses) {
                    if (courseOld.getId().equals(courseNew.getId())) {
                        isFound = true;
                        break;
                    }
                }

                if (!isFound) {
                    databaseAdapter.deleteCourse(courseOld.getId());
                }
            }

//            return coursesToBeInserted;
        } else {
            //Insert as is
            databaseAdapter.insertCourseList(courses);
//            return courses;
        }
    }


    public static void parseAndStoreCoursesDetails(String courseId, JSONArray JsonArray, Context context) {

        long timestampSeconds = (long) (System.currentTimeMillis() / 1000);
        try {
            CustomLog.e("CourseDetails", JsonArray.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Subject> subjects = new ArrayList<>();
        ArrayList<Unit> units = new ArrayList<>();
        JSONArray jsonArrayCurriculum = null;
        String subjectId = null;

        if (JsonArray.length() > 0) {
            try {
                JSONObject jsonObject = JsonArray.getJSONObject(0);
                JSONObject jsonObjectData = jsonObject.getJSONObject("data");
                jsonArrayCurriculum = jsonObjectData.getJSONArray("curriculum");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
                //#TODO Need to handle this
            }
        } else {
            return;
        }


        for (int i = 0; i < jsonArrayCurriculum.length(); i++) {
            try {
                JSONObject jsonObject = jsonArrayCurriculum.getJSONObject(i);
                long timestamp = 0;//Initial timestamp will be 0 update it when user fetch its data

                if (jsonObject.getString("type").contains("section")) {
                    subjectId = jsonObject.getString("key");
                    Subject subject = new Subject(jsonObject.getString("title"), subjectId, courseId);
                    subjects.add(subject);
                } else if (jsonObject.getString("type").contains("unit")) {
                    Unit unit = new Unit(
                            jsonObject.getString("id"),
                            subjectId,
                            courseId,
                            jsonObject.getString("title"),
                            "",
                            timestamp
                    );
                    units.add(unit);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Subject> subjectsToBeInserted = new ArrayList<>();
        ArrayList<Unit> unitsToBeInserted = new ArrayList<>();
        ArrayList<Subject> subjectsOld = new ArrayList<>();
        ArrayList<Unit> unitsOld = new ArrayList<>();
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
        subjectsOld = databaseAdapter.getSubjects(courseId);
        unitsOld = databaseAdapter.getUnits(courseId);

        boolean isUnitFound = false;
        boolean isSubjectFound = false;

        for (Subject subjectNew : subjects) {
            isSubjectFound = false;
            for (Subject subjectOld : subjectsOld) {
                if (subjectNew.getId().equals(subjectOld.getId()))
                    isSubjectFound = true;
            }

            if (!isSubjectFound)
                subjectsToBeInserted.add(subjectNew);
        }

        for (Unit unitNew : units) {
            isUnitFound = false;
            for (Unit unitOld : unitsOld) {
                if (unitNew.getId().equals(unitOld.getId()))
                    isUnitFound = true;
            }

            if (!isUnitFound)
                unitsToBeInserted.add(unitNew);
        }


        databaseAdapter.insertSubjectList(subjectsToBeInserted);
        databaseAdapter.insertUnitList(unitsToBeInserted);
        databaseAdapter.updateCourseTimeStamp(courseId, timestampSeconds);

        boolean isFound=false;

        for (Subject subjectOld : subjectsOld) {
            isFound = false;

            for (Subject subjectNew : subjects) {
                if (subjectOld.getId().equals(subjectNew.getId())) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                databaseAdapter.deleteSubject(subjectOld.getCourseId(),subjectOld.getId());
            }
        }


        for (Unit unitOld : unitsOld) {
            isFound = false;

            for (Unit unitNew : units) {
                if (unitOld.getId().equals(unitNew.getId())) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                databaseAdapter.deleteUnit(unitOld.getCourseId(),unitOld.getSubjectId(),unitOld.getId());
            }
        }

    }


    public static void parseAndStoreUnitDetails(String courseId, String subjectId, String unitId, JSONObject jsonObject, Context context) {
        ArrayList<UnitContent> unitContents = new ArrayList<>();
        ArrayList<UnitContent> unitContentsToInsert = new ArrayList<>();
        ArrayList<UnitContent> unitContentsOld = new ArrayList<>();
        long timestampSeconds = (long) (System.currentTimeMillis() / 1000);
        boolean isFirstTime = true;
        JSONArray jsonArrayVideo = null;
        JSONArray jsonArrayAttachments = null;
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
        unitContentsOld = databaseAdapter.getUnitContent(unitId, subjectId, courseId);
        if (unitContentsOld.size() > 0)
            isFirstTime = false;

        try {
            CustomLog.e("JSONOBJECT", jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObjectMeta = jsonObject.getJSONObject("meta");
            try {
                jsonArrayVideo = jsonObjectMeta.getJSONArray("video");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                jsonArrayAttachments = jsonObjectMeta.getJSONArray("attachments");
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            boolean isContentExist = false;
            if (jsonArrayVideo != null)
                for (int i = 0; i < jsonArrayVideo.length(); i++) {
                    String url = jsonArrayVideo.get(i).toString();
                    String[] urlData = url.split("/");
                    String fileName = urlData[urlData.length - 1];
                    if (fileName.toLowerCase().contains(".mp4")) {
//                        isContentExist = databaseAdapter.isUnitContentExist(unitId, subjectId, courseId, fileName);
//                        if (!isContentExist) {
                            UnitContent unitContent = new UnitContent(unitId, subjectId, courseId, url, fileName, true, !isFirstTime, false);
                            unitContents.add(unitContent);
                            unitContent.print();
//                        }

                    }
                }
            if (jsonArrayAttachments != null)
                for (int i = 0; i < jsonArrayAttachments.length(); i++) {
                    JSONObject objectPdf = jsonArrayAttachments.getJSONObject(i);
                    String url = objectPdf.getString("link");
                    String[] urlData = url.split("/");
                    String fileName = urlData[urlData.length - 1];
                    if (fileName.toLowerCase().contains(".pdf")) {
//                        isContentExist = databaseAdapter.isUnitContentExist(unitId, subjectId, courseId, fileName);
//                        if (!isContentExist) {
                            UnitContent unitContent = new UnitContent(unitId, subjectId, courseId, url, fileName, false, !isFirstTime, false);
                            unitContents.add(unitContent);
                            unitContent.print();
//                        }
                    } else if (fileName.toLowerCase().contains(".mp4")) {
//                        isContentExist = databaseAdapter.isUnitContentExist(unitId, subjectId, courseId, fileName);
//                        if (!isContentExist) {
                            UnitContent unitContent = new UnitContent(unitId, subjectId, courseId, url, fileName, true, !isFirstTime, false);
                            unitContents.add(unitContent);
                            unitContent.print();
//                        }
                    }
                }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
            //#TODO Need to handle this
        }



        boolean isFound=false;
        for (UnitContent unitContentOld : unitContentsOld) {
            isFound = false;

            for (UnitContent unitContentNew : unitContents) {
                if (unitContentOld.getFileName().equals(unitContentNew.getFileName())) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                databaseAdapter.deleteUnitContent(unitContentOld.getUnitId(),unitContentOld.getSubjectId(),unitContentOld.getCourseId(),unitContentOld.getFileName());
            }
        }


        for (UnitContent unitContentNew : unitContents) {
            isFound = false;

            for (UnitContent unitContentOld : unitContentsOld) {
                if (unitContentNew.getFileName().equals(unitContentOld.getFileName())) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                unitContentsToInsert.add(unitContentNew);
            }
        }



        databaseAdapter.insertUnitContentList(unitContentsToInsert);
        databaseAdapter.updateUnitTimeStamp(courseId, subjectId, unitId, timestampSeconds);

    }

    public static boolean parseAndStoreUserDetails(@NonNull JSONObject jsonObject, Context context) {
        boolean isAuthorised = false;
        SharedPref sharedPref = new SharedPref(context);

        try {
            CustomLog.e("JSONOBJECT", jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            isAuthorised = jsonObject.getString("status").equals("AUTHORIZED");
            sharedPref.setIsLoggedIn(isAuthorised);

            if (isAuthorised) {
                try {
                    sharedPref.setUserName(jsonObject.getString("firstName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    sharedPref.setOrgList(jsonObject.getJSONArray("orgList"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isAuthorised;
    }


    public static String getErrorMessageFromJson(@NonNull JSONObject jsonObject) {
        boolean isSuccess = false;
        String errorMessage=null;
        try {
            isSuccess = jsonObject.getBoolean("status");

            if (!isSuccess) {
                try {
                   errorMessage= jsonObject.getString("error_message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return errorMessage;
    }

    public static boolean parseAndStoreAuthToken(@NonNull JSONObject jsonObject, Context context) {
        boolean isSuccess = false;
        SharedPref sharedPref = new SharedPref(context);

        try {
            CustomLog.e("JSONOBJECT", jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            isSuccess = jsonObject.getBoolean("status");

            if (isSuccess) {
                try {
                    sharedPref.setAuthToken(jsonObject.getString("auth_token"));
                    isSuccess = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }

                try {
                    sharedPref.setUrl(jsonObject.getString("lms_url"));
                    isSuccess = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isSuccess;
    }
}
