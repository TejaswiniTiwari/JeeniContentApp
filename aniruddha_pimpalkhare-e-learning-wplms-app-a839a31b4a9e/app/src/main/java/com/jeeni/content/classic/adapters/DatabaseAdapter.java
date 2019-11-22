package com.jeeni.content.classic.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jeeni.content.classic.customviews.CustomLog;
import com.jeeni.content.classic.model.Course;
import com.jeeni.content.classic.model.Subject;
import com.jeeni.content.classic.model.Unit;
import com.jeeni.content.classic.model.UnitContent;
import com.jeeni.content.classic.model.UsagePattern;
import com.jeeni.content.classic.model.UserActivity;

import java.util.ArrayList;


/**
 * Maintains offline data related to user and courses
 *
 * @author Fair Share IT Services Pvt. Ltd
 * @author Rahul Pawar
 * @version 1
 * @since 1.0
 */
public class DatabaseAdapter {
    private final static String TAG = DatabaseAdapter.class.getSimpleName();
    private DatabaseHelper helper;


    /**
     * Initialize DatabaseHelper object
     *
     * @param context activity context to initialize DatabaseHelper
     */
    public DatabaseAdapter(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    /**
     * Insert courses in a single transaction to minimize write time
     *
     * @param courses ArrayList of Course objects to be inserted
     */
    public void insertCourseList(ArrayList<Course> courses) {

        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (Course course : courses) {
                contentValues.put(DatabaseHelper.COURSE_ID, course.getId());
                contentValues.put(DatabaseHelper.COURSE_TITLE, course.getTitle());
                contentValues.put(DatabaseHelper.COURSE_INSTRUCTOR, course.getInstructor());
                contentValues.put(DatabaseHelper.COURSE_DESCRIPTION, course.getDescription());
                contentValues.put(DatabaseHelper.COURSE_URL, course.getUrl());
                contentValues.put(DatabaseHelper.TABLE_TIMESTAMP, course.getTimestamp());
                db.insert(DatabaseHelper.TABLE_COURSES, null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateCourseTimeStamp(String courseId, long timeStamp) {

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.TABLE_TIMESTAMP, timeStamp);
            db.update(DatabaseHelper.TABLE_COURSES, contentValues, DatabaseHelper.COURSE_ID + " =" + courseId, null);
        } finally {
            db.close();
        }
    }

    /**
     * Deletes all course list
     */
    public void deleteCourseData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_COURSES, "1", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteCourse(String courseId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_COURSES, DatabaseHelper.COURSE_ID + " ="+courseId, null);
            db.delete(DatabaseHelper.TABLE_UNITS, DatabaseHelper.COURSE_ID + " ="+courseId, null);
            db.delete(DatabaseHelper.TABLE_SUBJECTS, DatabaseHelper.COURSE_ID + " ="+courseId, null);
            db.delete(DatabaseHelper.TABLE_UNIT_CONTENTS, DatabaseHelper.COURSE_ID + " ="+courseId, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteUnit(String courseId,String subjectId,String unitId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_UNITS, DatabaseHelper.UNIT_ID + " ="+unitId+" AND " + DatabaseHelper.SUBJECT_ID + " ="+subjectId+" AND " + DatabaseHelper.COURSE_ID + " ="+courseId, null);
            db.delete(DatabaseHelper.TABLE_UNIT_CONTENTS, DatabaseHelper.UNIT_ID + " ="+unitId+" AND " + DatabaseHelper.SUBJECT_ID + " ="+subjectId+" AND " + DatabaseHelper.COURSE_ID + " ="+courseId, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }



//    public void deleteUnitContent(String unitId,String subjectId,String courseId) {
//        SQLiteDatabase db = helper.getWritableDatabase();
//        try {
//            db.delete(DatabaseHelper.TABLE_UNITS, DatabaseHelper.UNIT_ID + " ="+unitId, null);
//            db.delete(DatabaseHelper.TABLE_UNIT_CONTENTS, DatabaseHelper.UNIT_ID + " ="+unitId, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            db.close();
//        }
//    }

    /**
     * Insert subjects in a single transaction to minimize write time
     *
     * @param subjects ArrayList of Unit objects to be inserted
     */
    public void insertSubjectList(ArrayList<Subject> subjects) {

        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (Subject subject : subjects) {
                contentValues.put(DatabaseHelper.SUBJECT_ID, subject.getId());
                contentValues.put(DatabaseHelper.COURSE_ID, subject.getCourseId());
                contentValues.put(DatabaseHelper.SUBJECT_TITLE, subject.getTitle());
                db.insert(DatabaseHelper.TABLE_SUBJECTS, null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Deletes all units related to specific course
     *
     * @param courseId  Id of particular course whose subjects you want to delete
     * @param subjectId Id of particular subject
     */
    public void deleteSubject(String courseId,String subjectId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_SUBJECTS,  DatabaseHelper.SUBJECT_ID + " ="+subjectId+" AND " + DatabaseHelper.COURSE_ID + " ="+courseId, null);
            db.delete(DatabaseHelper.TABLE_UNITS, DatabaseHelper.SUBJECT_ID + " ="+subjectId+" AND " + DatabaseHelper.COURSE_ID + " ="+courseId, null);
            db.delete(DatabaseHelper.TABLE_UNIT_CONTENTS, DatabaseHelper.SUBJECT_ID + " ="+subjectId+" AND " + DatabaseHelper.COURSE_ID + " ="+courseId, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /**
     * Insert units in a single transaction to minimize write time
     *
     * @param units ArrayList of Unit objects to be inserted
     */
    public void insertUnitList(ArrayList<Unit> units) {

        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (Unit unit : units) {
                contentValues.put(DatabaseHelper.UNIT_ID, unit.getId());
                contentValues.put(DatabaseHelper.SUBJECT_ID, unit.getSubjectId());
                contentValues.put(DatabaseHelper.COURSE_ID, unit.getCourseId());
                contentValues.put(DatabaseHelper.UNIT_TITLE, unit.getTitle());
                contentValues.put(DatabaseHelper.UNIT_DESCRIPTION, unit.getDescription());
                contentValues.put(DatabaseHelper.TABLE_TIMESTAMP, unit.getTimestamp());
                db.insert(DatabaseHelper.TABLE_UNITS, null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateUnitTimeStamp(String courseId, String subjectId, String unitId, long timestamp) {

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.TABLE_TIMESTAMP, timestamp);
            db.update(DatabaseHelper.TABLE_UNITS, contentValues, DatabaseHelper.COURSE_ID + " =" + courseId + " AND " + DatabaseHelper.SUBJECT_ID + " =" + subjectId + " AND " + DatabaseHelper.UNIT_ID + " =" + unitId, null);
        } finally {
            db.close();
        }
    }

    /**
     * Deletes all units related to specific course
     *
     * @param courseId Id of particular course whose units you want to delete
     */
    public void deleteUnitData(String courseId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_UNITS, DatabaseHelper.COURSE_ID + " = ?", new String[]{courseId});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    public void insertDownloads(UnitContent unitContent, long bytes) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.UNIT_ID, unitContent.getUnitId());
            contentValues.put(DatabaseHelper.SUBJECT_ID, unitContent.getSubjectId());
            contentValues.put(DatabaseHelper.COURSE_ID, unitContent.getCourseId());
            contentValues.put(DatabaseHelper.DOWNLOAD_FILENAME, unitContent.getFileName());
            contentValues.put(DatabaseHelper.DOWNLOAD_TIMESTAMP, System.currentTimeMillis());
            contentValues.put(DatabaseHelper.DOWNLOAD_FILE_SIZE, bytes);
            contentValues.put(DatabaseHelper.DOWNLOAD_LAST_USED_TIMESTAMP, 0);
            db.insert(DatabaseHelper.TABLE_DOWNLOADS, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLastUsedFiles(String fileName, String courseId, String subjectId, String unitId, long lastUsed) {

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.DOWNLOAD_LAST_USED_TIMESTAMP, lastUsed);
            db.update(DatabaseHelper.TABLE_DOWNLOADS, contentValues, DatabaseHelper.DOWNLOAD_FILENAME + " =" + "'" + fileName + "' AND " + DatabaseHelper.COURSE_ID + " =" + courseId + " AND " + DatabaseHelper.SUBJECT_ID + " =" + subjectId + " AND " + DatabaseHelper.UNIT_ID + " =" + unitId, null);
        } finally {
            db.close();
        }
    }

    /**
     * Insert unit content in a single transaction to minimize write time
     *
     * @param unitContents ArrayList of UnitContents objects to be inserted
     */
    public void insertUnitContentList(ArrayList<UnitContent> unitContents) {

        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (UnitContent unitContent : unitContents) {
                contentValues.put(DatabaseHelper.UNIT_ID, unitContent.getUnitId());
                contentValues.put(DatabaseHelper.SUBJECT_ID, unitContent.getSubjectId());
                contentValues.put(DatabaseHelper.COURSE_ID, unitContent.getCourseId());
                contentValues.put(DatabaseHelper.UNIT_CONTENT_URL, unitContent.getUrl());
                contentValues.put(DatabaseHelper.UNIT_CONTENT_FILENAME, unitContent.getFileName());
                contentValues.put(DatabaseHelper.UNIT_CONTENT_IS_VIDEO, unitContent.isVideo());
                contentValues.put(DatabaseHelper.UNIT_CONTENT_IS_NEW, unitContent.isNew());
                db.insert(DatabaseHelper.TABLE_UNIT_CONTENTS, null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    /**
     * Deletes all units related to specific course
     *
     * @param unitId    Id of particular unit
     * @param subjectId Id of particular subject whose units you want to delete
     * @param courseId  Id of particular course whose units you want to delete
     */
    public void deleteUnitContent(String unitId, String subjectId, String courseId,String fileName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_UNIT_CONTENTS, DatabaseHelper.UNIT_ID + " ="+unitId+" AND " + DatabaseHelper.SUBJECT_ID + " ="+subjectId+" AND " + DatabaseHelper.COURSE_ID + " ="+courseId+" AND " + DatabaseHelper.UNIT_CONTENT_FILENAME + " ='"+fileName+"'", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

//    /**
//     * Maintains counter for how many time user open specific unit
//     *
//     * @param courseId   Id of the course
//     * @param unitID     Id of the unit
//     * @param courseName name of the course
//     * @param unitName   name of the unit
//     */
//    public void incrementActivityCounter(String courseId, String unitID, String courseName, String unitName) {
//
//        int counter = getActivityCounter(courseId, unitID);
//        if (counter < 0) {
//            insertActivity(courseId, unitID, courseName, unitName);
//        } else {
//            updateActivityCounter(courseId, unitID, ++counter);
//        }
//    }

    public void insertUsagePattern(UsagePattern usagePattern) {

//        int counter = 1;//New activity done by user it will get incremented if user perform same activity again

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.USAGE_PATTERN_ID, System.currentTimeMillis());
            contentValues.put(DatabaseHelper.USAGE_PATTERN_COURSE_NAME, usagePattern.getCourseName());
            contentValues.put(DatabaseHelper.USAGE_PATTERN_SUBJECT_NAME, usagePattern.getSubjectName());
            contentValues.put(DatabaseHelper.USAGE_PATTERN_UNIT_NAME, usagePattern.getUnitName());
            contentValues.put(DatabaseHelper.USAGE_PATTERN_FILE_TYPE, usagePattern.isFileType());
            contentValues.put(DatabaseHelper.USAGE_PATTERN_FILE_NAME, usagePattern.getFileName());
            contentValues.put(DatabaseHelper.USAGE_PATTERN_TIME, usagePattern.getTimeStamp());
            contentValues.put(DatabaseHelper.USAGE_PATTERN_TIME_DURATION, usagePattern.getDuration());
            db.insert(DatabaseHelper.TABLE_USAGE_PATTERN, null, contentValues);
        } finally {
            db.close();
        }

//        private static final String USAGE_PATTERN_ID = TABLE_ID;
//        private static final String USAGE_PATTERN_COURSE_NAME = "usage_pattern_course_name";
//        private static final String USAGE_PATTERN_SUBJECT_NAME = "usage_pattern_subject_name";
//        private static final String USAGE_PATTERN_UNIT_NAME = "usage_pattern_unit_name";
//        private static final String USAGE_PATTERN_FILE_TYPE = "usage_pattern_file_type";
//        private static final String USAGE_PATTERN_FILE_NAME = "usage_pattern_file_name";
//        private static final String USAGE_PATTERN_TIME = "usage_pattern_time";
//        private static final String USAGE_PATTERN_TIME_DURATION = "usage_pattern_time_duration";
    }

//    private void updateActivityCounter(String courseId, String unitID, int counter) {
//
//        SQLiteDatabase db = helper.getWritableDatabase();
//        try {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(DatabaseHelper.ACTIVITY_COUNTER, counter);
//            db.update(DatabaseHelper.TABLE_ACTIVITIES, contentValues, DatabaseHelper.COURSE_ID + " =? AND" + DatabaseHelper.UNIT_ID + " =?", new String[]{courseId, unitID});
//        } finally {
//            db.close();
//        }
//    }


//    private int getActivityCounter(String courseId, String unitID) {
//
//        int counter = -1;
//        SQLiteDatabase db = helper.getReadableDatabase();
//        Cursor cursor = db.query(DatabaseHelper.TABLE_ACTIVITIES, new String[]{DatabaseHelper.ACTIVITY_COUNTER}, DatabaseHelper.COURSE_ID + " =? AND" + DatabaseHelper.UNIT_ID + " =?", new String[]{courseId, unitID}, null, null, null);
//
//        try {
//            if (cursor.getCount() > 0) {
//                if (cursor.moveToFirst()) {
//                    counter = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIVITY_COUNTER));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            cursor.close();
//            db.close();
//        }
//
//        return counter;
//    }

    //Get list of all courses
    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_COURSES,
                new String[]{DatabaseHelper.COURSE_ID, DatabaseHelper.TABLE_TIMESTAMP, DatabaseHelper.COURSE_TITLE, DatabaseHelper.COURSE_INSTRUCTOR, DatabaseHelper.COURSE_DESCRIPTION, DatabaseHelper.COURSE_URL},
                null, null, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    Course course = new Course(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_TITLE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_INSTRUCTOR)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_URL)),
                            cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TABLE_TIMESTAMP))
                    );
                    courses.add(course);
                    cursor.moveToNext();
                }
                return courses;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return courses;
    }


    /**
     * Returns all units which belongs to given courseId
     *
     * @param courseId Id of the course
     * @return ArrayList of subjects which belongs to given courseId
     */
    public ArrayList<Subject> getSubjects(String courseId) {
        ArrayList<Subject> subjects = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SUBJECTS,
                new String[]{DatabaseHelper.COURSE_ID,
                        DatabaseHelper.SUBJECT_ID, DatabaseHelper.SUBJECT_TITLE},
                DatabaseHelper.COURSE_ID + " =? ", new String[]{courseId}, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    Subject subject = new Subject(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_TITLE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_ID)),
                            courseId
                    );
                    subjects.add(subject);
                    cursor.moveToNext();
                }
                return subjects;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return subjects;
    }


    /**
     * Returns course name
     *
     * @param courseId Id of the course
     * @return String of course name
     */
    public String getCourseTitle(String courseId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_COURSES,
                new String[]{DatabaseHelper.COURSE_TITLE},
                DatabaseHelper.COURSE_ID + " =? ", new String[]{courseId}, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    return cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_TITLE));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }

    /**
     * Returns subject name
     *
     * @param subjectID Id of the subject
     * @return String of subject name
     */
    public String getSubjectTitle(String subjectID) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SUBJECTS,
                new String[]{DatabaseHelper.SUBJECT_TITLE},
                DatabaseHelper.SUBJECT_ID + " =? ", new String[]{subjectID}, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    return cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_TITLE));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }


    /**
     * Returns unit name
     *
     * @param unitID Id of the unit
     * @return String of unit name
     */
    public String getUnitTitle(String unitID) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNITS,
                new String[]{DatabaseHelper.UNIT_TITLE},
                DatabaseHelper.UNIT_ID + " =? ", new String[]{unitID}, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    return cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_TITLE));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }

    /**
     * Returns all units
     *
     * @return ArrayList of units
     */
    public ArrayList<Unit> getAllUnits() {
        ArrayList<Unit> units = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNITS,
                new String[]{DatabaseHelper.UNIT_ID, DatabaseHelper.SUBJECT_ID, DatabaseHelper.COURSE_ID,
                        DatabaseHelper.UNIT_TITLE, DatabaseHelper.UNIT_DESCRIPTION,
                        DatabaseHelper.TABLE_TIMESTAMP},
                null, null, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    Unit unit = new Unit(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_TITLE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_DESCRIPTION)),
                            cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TABLE_TIMESTAMP))
                    );
                    units.add(unit);
                    cursor.moveToNext();
                }
                return units;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return units;
    }

    /**
     * Returns all units
     *
     * @return ArrayList of units
     */
    public int getNewUnitContentCount() {
//        ArrayList<UnitContent> unitContents = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNIT_CONTENTS,
                new String[]{DatabaseHelper.UNIT_ID},
                DatabaseHelper.UNIT_CONTENT_IS_NEW + " =1", null, null, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return -1;
    }

    public void updateLatestUnitContent(UnitContent unitContent) {

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.UNIT_CONTENT_IS_NEW, false);
            db.update(DatabaseHelper.TABLE_UNIT_CONTENTS, contentValues, DatabaseHelper.COURSE_ID + " =" + unitContent.getCourseId() + " AND " + DatabaseHelper.SUBJECT_ID + " =" + unitContent.getSubjectId() + " AND " + DatabaseHelper.UNIT_ID + " =" + unitContent.getUnitId()+ " AND " + DatabaseHelper.UNIT_CONTENT_FILENAME + " ='" + unitContent.getFileName()+"'", null);
        } finally {
            db.close();
        }
    }

    public void updateAllLatestUnitContent() {

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.UNIT_CONTENT_IS_NEW, false);
            db.update(DatabaseHelper.TABLE_UNIT_CONTENTS, contentValues, null, null);
        } finally {
            db.close();
        }
    }

    /**
     * Returns all units which belongs to given courseId
     *
     * @param courseId Id of the course
     * @return ArrayList of units which belongs to given courseId
     */
    public ArrayList<Unit> getUnits(String courseId) {
        ArrayList<Unit> units = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNITS,
                new String[]{DatabaseHelper.UNIT_ID, DatabaseHelper.SUBJECT_ID,
                        DatabaseHelper.UNIT_TITLE, DatabaseHelper.UNIT_DESCRIPTION,
                        DatabaseHelper.TABLE_TIMESTAMP},
                DatabaseHelper.COURSE_ID + " =? ", new String[]{courseId}, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    Unit unit = new Unit(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_ID)),
                            courseId,
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_TITLE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_DESCRIPTION)),
                            cursor.getLong(cursor.getColumnIndex(DatabaseHelper.TABLE_TIMESTAMP))
                    );
                    units.add(unit);
                    cursor.moveToNext();
                }
                return units;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return units;
    }


    /**
     * Returns all units which belongs to given courseId
     *
     * @param unitId    Id of the unit
     * @param subjectId Id of the subject
     * @param courseId  Id of the course
     * @return true if content exist else false
     */
    public boolean isUnitContentExist(String unitId, String subjectId, String courseId, String fileName) {
        ArrayList<UnitContent> unitContents = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNIT_CONTENTS,
                new String[]{DatabaseHelper.UNIT_ID, DatabaseHelper.SUBJECT_ID, DatabaseHelper.UNIT_CONTENT_IS_NEW,
                        DatabaseHelper.COURSE_ID, DatabaseHelper.UNIT_CONTENT_FILENAME, DatabaseHelper.UNIT_CONTENT_URL, DatabaseHelper.UNIT_CONTENT_IS_VIDEO},
                DatabaseHelper.COURSE_ID + " =" + courseId + " AND " + DatabaseHelper.SUBJECT_ID + " =" + subjectId + " AND " + DatabaseHelper.UNIT_ID + " =" + unitId + " AND " + DatabaseHelper.UNIT_CONTENT_FILENAME + " ='" + fileName + "'", null, null, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return true;//It will not add same content twice
    }


    /**
     * Returns all units which belongs to given courseId
     *
     * @param unitId    Id of the unit
     * @param subjectId Id of the subject
     * @param courseId  Id of the course
     * @return ArrayList of units which belongs to given courseId
     */
    public ArrayList<UnitContent> getUnitContent(String unitId, String subjectId, String courseId) {
        ArrayList<UnitContent> unitContents = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNIT_CONTENTS,
                new String[]{DatabaseHelper.UNIT_ID, DatabaseHelper.SUBJECT_ID, DatabaseHelper.UNIT_CONTENT_IS_NEW,
                        DatabaseHelper.COURSE_ID, DatabaseHelper.UNIT_CONTENT_FILENAME, DatabaseHelper.UNIT_CONTENT_URL, DatabaseHelper.UNIT_CONTENT_IS_VIDEO},
                DatabaseHelper.COURSE_ID + " =? AND " + DatabaseHelper.SUBJECT_ID + " =? AND " + DatabaseHelper.UNIT_ID + " =?", new String[]{courseId, subjectId, unitId}, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    UnitContent unitContent = new UnitContent(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_ID)),
                            courseId,
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_CONTENT_URL)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_CONTENT_FILENAME)),
                            cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIT_CONTENT_IS_VIDEO)) > 0,
                            cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIT_CONTENT_IS_NEW)) > 0,
                            false
                    );
                    unitContents.add(unitContent);
                    cursor.moveToNext();
                }
                return unitContents;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return unitContents;
    }


    /**
     * Returns all new units
     *
     * @return ArrayList of units which belongs to given courseId
     */
    public ArrayList<UnitContent> getLatestUnitContent() {
        ArrayList<UnitContent> unitContents = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNIT_CONTENTS,
                new String[]{DatabaseHelper.UNIT_ID, DatabaseHelper.SUBJECT_ID, DatabaseHelper.UNIT_CONTENT_IS_NEW,
                        DatabaseHelper.COURSE_ID, DatabaseHelper.UNIT_CONTENT_FILENAME, DatabaseHelper.UNIT_CONTENT_URL, DatabaseHelper.UNIT_CONTENT_IS_VIDEO},
                DatabaseHelper.UNIT_CONTENT_IS_NEW + " =1", null, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    UnitContent unitContent = new UnitContent(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_CONTENT_URL)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_CONTENT_FILENAME)),
                            cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIT_CONTENT_IS_VIDEO)) > 0,
                            cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIT_CONTENT_IS_NEW)) > 0,
                            false
                    );
                    unitContents.add(unitContent);
                    cursor.moveToNext();
                }
                return unitContents;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return unitContents;
    }

    public long getTotalFileSize() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT SUM(" + DatabaseHelper.DOWNLOAD_FILE_SIZE + ") as Total FROM " + DatabaseHelper.TABLE_DOWNLOADS, null);

            if (cursor.moveToFirst()) {

                return cursor.getLong(cursor.getColumnIndex("Total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor = null;
        }

        return 0;
    }

    public String getFirstDownloadedFile() {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_DOWNLOADS,
                new String[]{DatabaseHelper.DOWNLOAD_FILENAME},
                null, null, null, null, DatabaseHelper.DOWNLOAD_TIMESTAMP + " ASC LIMIT 1");
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                return cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOWNLOAD_FILENAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }

    public void deleteDownloadedFile(String fileName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_DOWNLOADS, DatabaseHelper.DOWNLOAD_FILENAME + " = ? ", new String[]{fileName});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    //Returns ArrayList of all user activities with counter
    public ArrayList<UnitContent> getAllDownloadedFiles(String limit) {
        ArrayList<UnitContent> unitContents = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_DOWNLOADS,
                new String[]{DatabaseHelper.DOWNLOAD_FILENAME,
                        DatabaseHelper.UNIT_ID,
                        DatabaseHelper.SUBJECT_ID,
                        DatabaseHelper.COURSE_ID,
                        DatabaseHelper.DOWNLOAD_FILE_SIZE},
                null, null, null, null, DatabaseHelper.DOWNLOAD_LAST_USED_TIMESTAMP + " DESC LIMIT " + limit);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    UnitContent unitContent = new UnitContent(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.UNIT_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COURSE_ID)),
                            "",
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOWNLOAD_FILENAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.DOWNLOAD_FILENAME)).contains(".mp4"),
                            false,
                            true
                    );
                    unitContents.add(unitContent);
                    cursor.moveToNext();
                }
                return unitContents;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return unitContents;
    }

    //Returns ArrayList of all user activities with counter
    public ArrayList<UsagePattern> getAllUsagePattern() {
        ArrayList<UsagePattern> usagePatterns = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USAGE_PATTERN,
                new String[]{DatabaseHelper.USAGE_PATTERN_ID,
                        DatabaseHelper.USAGE_PATTERN_COURSE_NAME,
                        DatabaseHelper.USAGE_PATTERN_SUBJECT_NAME,
                        DatabaseHelper.USAGE_PATTERN_UNIT_NAME,
                        DatabaseHelper.USAGE_PATTERN_FILE_TYPE,
                        DatabaseHelper.USAGE_PATTERN_FILE_NAME,
                        DatabaseHelper.USAGE_PATTERN_TIME,
                        DatabaseHelper.USAGE_PATTERN_TIME_DURATION,
                        },
                null, null, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                CustomLog.e("DATABASE","----------------------------------Start------------------------------");
                while (!cursor.isAfterLast()) {
                    UsagePattern usagePattern = new UsagePattern(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_UNIT_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_SUBJECT_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_COURSE_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_FILE_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_FILE_TYPE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_TIME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_TIME_DURATION))
                    );
                    usagePatterns.add(usagePattern);
                    usagePattern.print();
//                    deleteUsagePattern(usagePattern.getId());
                    cursor.moveToNext();
                }
                CustomLog.e("DATABASE","----------------------------------End------------------------------");
                return usagePatterns;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return usagePatterns;
    }


    //Returns ArrayList of all user activities with counter
    public UsagePattern getUsagePattern() {
        ArrayList<UserActivity> userActivities = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USAGE_PATTERN,
                new String[]{DatabaseHelper.USAGE_PATTERN_ID,
                        DatabaseHelper.USAGE_PATTERN_COURSE_NAME,
                        DatabaseHelper.USAGE_PATTERN_SUBJECT_NAME,
                        DatabaseHelper.USAGE_PATTERN_UNIT_NAME,
                        DatabaseHelper.USAGE_PATTERN_FILE_TYPE,
                        DatabaseHelper.USAGE_PATTERN_FILE_NAME,
                        DatabaseHelper.USAGE_PATTERN_TIME,
                        DatabaseHelper.USAGE_PATTERN_TIME_DURATION,
                },
                null, null, null, null, null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {

                    UsagePattern usagePattern = new UsagePattern(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_UNIT_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_SUBJECT_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_COURSE_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_FILE_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_FILE_TYPE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_TIME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.USAGE_PATTERN_TIME_DURATION))
                    );
                    return usagePattern;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }
        return null;
    }

    public void deleteUsagePattern(String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(DatabaseHelper.TABLE_USAGE_PATTERN, DatabaseHelper.USAGE_PATTERN_ID + " = ? ", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void clearData(){
        helper.dropTables(null);
    }


    //Handles SQLite database connections
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static DatabaseHelper sInstance;

        private static final String DATABASE_NAME = "E_LEARNING_APP_DATABASE";
        private static final int DATABASE_VERSION = 2;
        private static final String TABLE_ID = "_id";
        private static final String TABLE_TIMESTAMP = "timestamp";//To compare with WPLMS timestamp

        //Courses Table
        private static final String TABLE_COURSES = "courses";
        private static final String COURSE_ID = "course_id"; //to store course ID returned by WPLMS
        private static final String COURSE_TITLE = "course_title";
        private static final String COURSE_INSTRUCTOR = "course_instructor";
        private static final String COURSE_DESCRIPTION = "course_description";
        private static final String COURSE_URL = "course_url";
        private static final String COURSE_EXPIRES_IN = "course_expires_in";
        private static final String CREATE_TABLE_COURSES = "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + " ( " + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + TABLE_TIMESTAMP + " LONG," + COURSE_ID + " VARCHAR(70)," + COURSE_TITLE + " VARCHAR(200)," + COURSE_INSTRUCTOR + " VARCHAR(200)," + COURSE_DESCRIPTION + " VARCHAR(2000)," + COURSE_URL + " TEXT," + COURSE_EXPIRES_IN + " VARCHAR(20));";
        private static final String DROP_TABLE_COURSES = "DROP TABLE IF EXISTS " + TABLE_COURSES;


        //Subjects Table
        private static final String TABLE_SUBJECTS = "subjects";
        private static final String SUBJECT_ID = "subject_id";//Section id in WPLMS
        private static final String SUBJECT_TITLE = "subject_title";
        private static final String CREATE_TABLE_SUBJECTS = "CREATE TABLE IF NOT EXISTS " + TABLE_SUBJECTS + " ( " + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + SUBJECT_ID + " VARCHAR(70)," + COURSE_ID + " VARCHAR(70)," + SUBJECT_TITLE + " VARCHAR(100));";
        private static final String DROP_TABLE_SUBJECTS = "DROP TABLE IF EXISTS " + TABLE_COURSES;


        //Units Table
        private static final String TABLE_UNITS = "units";
        private static final String UNIT_ID = "unit_id"; //to store unit ID returned by WPLMS
        private static final String UNIT_TITLE = "unit_title";
        private static final String UNIT_DESCRIPTION = "unit_description";
        private static final String CREATE_TABLE_UNITS = "CREATE TABLE IF NOT EXISTS " + TABLE_UNITS + " ( " + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + TABLE_TIMESTAMP + " LONG," + UNIT_ID + " VARCHAR(70)," + SUBJECT_ID + " VARCHAR(70)," + COURSE_ID + " VARCHAR(70)," + UNIT_TITLE + " VARCHAR(200)," + UNIT_DESCRIPTION + " VARCHAR(2000));";
        private static final String DROP_TABLE_UNITS = "DROP TABLE IF EXISTS " + TABLE_UNITS;


        //Units Table
        private static final String TABLE_UNIT_CONTENTS = "unit_contents";
        private static final String UNIT_CONTENT_URL = "unit_url";
        private static final String UNIT_CONTENT_FILENAME = "unit_filename";
        private static final String UNIT_CONTENT_IS_VIDEO = "unit_is_video";
        private static final String UNIT_CONTENT_IS_NEW = "unit_is_new";
        private static final String CREATE_TABLE_UNIT_CONTENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_UNIT_CONTENTS + " ( " + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + UNIT_ID + " VARCHAR(70)," + SUBJECT_ID + " VARCHAR(70)," + COURSE_ID + " VARCHAR(70)," + UNIT_CONTENT_FILENAME + " VARCHAR(70)," + UNIT_CONTENT_URL + " TEXT," + UNIT_CONTENT_IS_NEW + " BOOLEAN, " + UNIT_CONTENT_IS_VIDEO + " BOOLEAN);";
        private static final String DROP_TABLE_UNIT_CONTENTS = "DROP TABLE IF EXISTS " + TABLE_UNIT_CONTENTS;


        //User Activities Table
        private static final String TABLE_ACTIVITIES = "activities";
//        private static final String ACTIVITY_ID = TABLE_ID;
//        private static final String ACTIVITY_COURSE_NAME = "activity_course_name";
//        private static final String ACTIVITY_UNIT_NAME = "activity_unit_name";
//        private static final String ACTIVITY_COUNTER = "activity_counter";
//        private static final String CREATE_TABLE_ACTIVITIES = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVITIES + " ( " + ACTIVITY_ID + " LONG PRIMARY KEY NOT NULL ," + COURSE_ID + " VARCHAR(70)," + UNIT_ID + " VARCHAR(70)," + ACTIVITY_COURSE_NAME + " VARCHAR(70)," + ACTIVITY_UNIT_NAME + " VARCHAR(70)," + ACTIVITY_COUNTER + " INTEGER );";
        private static final String DROP_TABLE_ACTIVITIES = "DROP TABLE IF EXISTS " + TABLE_ACTIVITIES;


        private static final String TABLE_USAGE_PATTERN = "usage_pattern";
        private static final String USAGE_PATTERN_ID = TABLE_ID;
        private static final String USAGE_PATTERN_COURSE_NAME = "usage_pattern_course_name";
        private static final String USAGE_PATTERN_SUBJECT_NAME = "usage_pattern_subject_name";
        private static final String USAGE_PATTERN_UNIT_NAME = "usage_pattern_unit_name";
        private static final String USAGE_PATTERN_FILE_TYPE = "usage_pattern_file_type";
        private static final String USAGE_PATTERN_FILE_NAME = "usage_pattern_file_name";
        private static final String USAGE_PATTERN_TIME = "usage_pattern_time";
        private static final String USAGE_PATTERN_TIME_DURATION = "usage_pattern_time_duration";
        private static final String CREATE_TABLE_USAGE_PATTERN = "CREATE TABLE IF NOT EXISTS " + TABLE_USAGE_PATTERN + " ( " + USAGE_PATTERN_ID + " LONG PRIMARY KEY NOT NULL ," + USAGE_PATTERN_COURSE_NAME + " VARCHAR(70)," + USAGE_PATTERN_SUBJECT_NAME + " VARCHAR(70)," + USAGE_PATTERN_UNIT_NAME + " VARCHAR(70)," + USAGE_PATTERN_FILE_TYPE + " VARCHAR(70),"+ USAGE_PATTERN_FILE_NAME + " VARCHAR(70),"+ USAGE_PATTERN_TIME_DURATION + " VARCHAR(70)," + USAGE_PATTERN_TIME + " VARCHAR(70) );";
        private static final String DROP_TABLE_USAGE_PATTERN = "DROP TABLE IF EXISTS " + TABLE_USAGE_PATTERN;


        //Successful Downloads Table
        private static final String TABLE_DOWNLOADS = "downloads";
        private static final String DOWNLOAD_ID = TABLE_ID;
        private static final String DOWNLOAD_TIMESTAMP = "download_timestamp";
        private static final String DOWNLOAD_LAST_USED_TIMESTAMP = "download_last_used_timestamp";
        private static final String DOWNLOAD_FILENAME = "download_filename";
        private static final String DOWNLOAD_FILE_SIZE = "download_file_size";//in mb
        private static final String CREATE_TABLE_DOWNLOADS = "CREATE TABLE IF NOT EXISTS " + TABLE_DOWNLOADS + " ( " + DOWNLOAD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + DOWNLOAD_TIMESTAMP + " LONG," + DOWNLOAD_LAST_USED_TIMESTAMP + " LONG," + UNIT_ID + " VARCHAR(70)," + SUBJECT_ID + " VARCHAR(70)," + COURSE_ID + " VARCHAR(70)," + DOWNLOAD_FILENAME + " VARCHAR(200)," + DOWNLOAD_FILE_SIZE + " LONG);";
        private static final String DROP_TABLE_DOWNLOADS = "DROP TABLE IF EXISTS " + TABLE_DOWNLOADS;


        static synchronized DatabaseHelper getInstance(Context context) {

            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            if (sInstance == null) {
                sInstance = new DatabaseHelper(context.getApplicationContext());
            }
            return sInstance;
        }

        /**
         * Constructor should be private to prevent direct instantiation.
         * make call to static method "getInstance()" instead.
         */
        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {


            try {
                createTable(db);
            } catch (SQLException e) {
                CustomLog.e(TAG, e.toString());
            }

        }


        private void createTable(SQLiteDatabase db) {
            if (db == null)
                db = getWritableDatabase();
            try {
                db.execSQL(CREATE_TABLE_COURSES);
                db.execSQL(CREATE_TABLE_SUBJECTS);
                db.execSQL(CREATE_TABLE_UNITS);
                db.execSQL(CREATE_TABLE_UNIT_CONTENTS);
                db.execSQL(CREATE_TABLE_USAGE_PATTERN);
                db.execSQL(CREATE_TABLE_DOWNLOADS);
            } catch (SQLException e) {
                CustomLog.e(TAG, e.toString());
            }
        }


        public void dropTables(SQLiteDatabase db){
            if (db == null)
                db = getWritableDatabase();
            try {
                db.execSQL(DROP_TABLE_COURSES);
                db.execSQL(DROP_TABLE_SUBJECTS);
                db.execSQL(DROP_TABLE_UNITS);
                db.execSQL(DROP_TABLE_UNIT_CONTENTS);
                db.execSQL(DROP_TABLE_ACTIVITIES);
                db.execSQL(DROP_TABLE_USAGE_PATTERN);
//                db.execSQL(DROP_TABLE_ACTIVITIES);
                db.execSQL(DROP_TABLE_DOWNLOADS);
            } catch (SQLException e) {
                CustomLog.e(TAG, e.toString());
            }

            createTable(db);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            try {
                // Drop older table if existed
                //db.execSQL("DROP TABLE IF EXISTS " + <TABLE_NAME>);
                // Creating tables again
                db.execSQL(CREATE_TABLE_USAGE_PATTERN);
                db.execSQL(DROP_TABLE_ACTIVITIES);
                //onCreate(db);
            } catch (Exception e) {
                CustomLog.e(TAG, e.toString());
            }
        }

//        @Override
//        public void onConfigure(SQLiteDatabase db) {
//            super.onConfigure(db);
//            if (!db.isReadOnly()) {
//                // Enable foreign key constraints
//                db.execSQL("PRAGMA foreign_keys=ON;");
//            }
//        }

//        @Override
//        public void onOpen(SQLiteDatabase db) {
//            super.onOpen(db);
//            if (!db.isReadOnly()) {
//                // Enable foreign key constraints
//                db.execSQL("PRAGMA foreign_keys=ON;");
//            }
//        }

    }
}