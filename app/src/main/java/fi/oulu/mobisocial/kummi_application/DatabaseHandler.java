package fi.oulu.mobisocial.kummi_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "TASKERI DATABASE";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "taskManager";

    // Contacts table name
    private static final String TABLE_TASKS = "tasks";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_INFO = "info";
    private static final String KEY_CALENDAR = "calendar";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "in onCreate");
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_INFO + " TEXT,"
                + KEY_CALENDAR + " INTEGER," + KEY_LATITUDE + " INTEGER,"
                + KEY_LONGITUDE + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "in onUpgrade");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new task
    long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        double time;

        ContentValues values = new ContentValues();
        values.put(KEY_INFO, task.getInfo()); // task info
        if (task.calendarExists()) {
            time = task.getCalendar().getTimeInMillis();
        } else {
            time = 0;
        }
        values.put(KEY_CALENDAR, time); // Task calendar
        values.put(KEY_LATITUDE, task.getLatitude());
        values.put(KEY_LONGITUDE, task.getLongitude());

        // Inserting Row
        long id;
        id = db.insert(TABLE_TASKS, null, values);
        Log.d(TAG, "ID: " + Long.toString(id));
        db.close(); // Closing database connection
        return id;
    }

    // Getting single task
    Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[] { KEY_ID,
                        KEY_INFO, KEY_CALENDAR, KEY_LATITUDE, KEY_LONGITUDE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Task task = new Task();
        task.setID(Integer.parseInt(cursor.getString(0)));
        task.setInfo(cursor.getString(1));
        task.setCalendarInMillis(Long.parseLong(cursor.getString(2)));
        task.setLatitude(cursor.getDouble(3));
        task.setLongitude(cursor.getDouble(4));
        // return task
        return task;
    }

    // Getting All Tasks
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<Task>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setID(Integer.parseInt(cursor.getString(0)));
                task.setInfo(cursor.getString(1));
                task.setCalendarInMillis(Long.parseLong(cursor.getString(2)));
                task.setLatitude(cursor.getDouble(3));
                task.setLongitude(cursor.getDouble(4));

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        // return task list
        return taskList;
    }

    // Updating single task
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INFO, task.getInfo());
        values.put(KEY_CALENDAR, task.getCalendar().getTimeInMillis());
        values.put(KEY_LATITUDE, task.getLatitude());
        values.put(KEY_LONGITUDE, task.getLongitude());

        // updating row
        return db.update(TABLE_TASKS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getID()) });
    }

    // Deleting single task
    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getID()) });
        db.close();
    }

    // Deleting single task
    public void deleteTask(long id) {
        Log.d(TAG, "in deleteTask with id: " + Long.toString(id));
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }


    // Getting Tasks Count
    public int getTasksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void deleteOldTasks() {
        Log.d(TAG, "in deleteOldTasks");
        List<Task> taskList = new ArrayList<Task>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setID(Integer.parseInt(cursor.getString(0)));
                task.setInfo(cursor.getString(1));
                task.setCalendarInMillis(Long.parseLong(cursor.getString(2)));
                task.setLatitude(cursor.getDouble(3));
                task.setLongitude(cursor.getDouble(4));

                taskList.add(task);
            } while (cursor.moveToNext());
        }

        for (Task t : taskList) {
            // If timetask and more than day old task then remove
            if (t.getLatitude() == 0) {
                if ((t.getCalendar().getTimeInMillis() + 24 * 60 * 60 * 1000) < Calendar.getInstance().getTimeInMillis()) {
                    deleteTask(t);
                }
            }
        }
    }
}