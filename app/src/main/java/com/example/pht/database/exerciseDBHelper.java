package com.example.pht.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class exerciseDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "pht.exercise.db";

    // Table information
    public static final String TABLE = "exercises";
    public static final String LOG_TABLE = "log_exercise";

    // Column information
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";
    public static final String KEY_met = "met";
    public static final String KEY_log_id = "id";
    public static final String KEY_exercise = "exercise";
    public static final String KEY_minutes = "minutes";
    public static final String KEY_weight = "weight";
    public static final String KEY_timestamp = "timestamp";

    // Data properties
    private int id;
    private String name;
    private double met;

    private int log_id;
    private int minutes;
    private int weight;

    public exerciseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_EXERCISE = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_name + " TEXT, "
                + KEY_met + " REAL )";

        db.execSQL(CREATE_TABLE_EXERCISE);

        String CREATE_TABLE_LOG_EXERCISE = "CREATE TABLE " + LOG_TABLE + "("
                + KEY_log_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_exercise + " INTEGER, "
                + KEY_minutes + " INTEGER, "
                + KEY_weight + " INTEGER, "
                + KEY_timestamp + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + KEY_exercise + ") REFERENCES " + TABLE + "(" + KEY_ID +"));";

        db.execSQL(CREATE_TABLE_LOG_EXERCISE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Add new exercise record
    public void addExercise(exercise item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_name, item.getName());
        values.put(KEY_met, item.getMet());
        long rowId = db.insertOrThrow(TABLE, null, values);
        db.close();
    }

    // Return a single exercise item by id
    public exercise getExercise(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE,
                new String[] { KEY_ID, KEY_name, KEY_met },
                KEY_ID + "= ?", new String[] { String.valueOf(id) },
                null, null, "id ASC", "100");
        if (cursor != null)
            cursor.moveToFirst();
        exercise item = new exercise(cursor.getString(1), cursor.getDouble(2));
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        if (cursor != null)
            cursor.close();
        return item;
    }

    // Return a single exercise item by name
    public exercise getExercise(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE,
                new String[] { KEY_ID, KEY_name, KEY_met },
                KEY_name + "= ?", new String[] { name },
                null, null, "id ASC", "100");
        if (cursor != null)
            cursor.moveToFirst();
        exercise item = new exercise(cursor.getString(1), cursor.getDouble(2));
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        if (cursor != null)
            cursor.close();
        return item;
    }

    public List<exercise> getAllExercises() {
        List<exercise> exercises = new ArrayList<exercise>();
        String selectQuery = "Select * FROM " + TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                exercise item = new exercise(cursor.getString(1),cursor.getDouble(2));
                item.setId(cursor.getInt(0));
                exercises.add(item);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return exercises;
    }

    public int getExerciseCount() {
        String countQuery = "Select * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateExercise(exercise item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_name, item.getName());
        values.put(KEY_met, item.getMet());
        int result = db.update(TABLE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId())});
        db.close();
        return result;
    }

    public void deleteExercise(exercise item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId()) });
        db.close();
    }

    public void populate() {
        HashMap hm = new HashMap<String, Double>();

        hm.put("Sleeping", 0.9);
        hm.put("Watching Television", 1.0);
        hm.put("Writing/Typing", 1.8);
        hm.put("Walking (1.7 mph)", 2.3);
        hm.put("Walking (2.5 mph)", 2.9);
        hm.put("Bicycling, Stationary (50 watts)", 3.0);
        hm.put("Walking (3.0 mph)", 3.3);
        hm.put("Calisthenics (Light or Moderate Effort)", 3.5);
        hm.put("Walking (3.4 mph)", 3.6);
        hm.put("Bicycling (<10 mph)", 4.0);
        hm.put("Bicycling, Stationary (100 watts)", 5.5);
        hm.put("Jogging", 7.0);
        hm.put("Calisthenics (Heavy or Vigorous Effort)", 8.0);
        hm.put("Running", 8.0);
        hm.put("Rope Jumping", 10.0);

        Set set = hm.entrySet();
        Iterator i = set.iterator();

        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            addExercise(new exercise(me.getKey().toString(), me.getValue()));
        }
    }

    public void logExercise(String exerciseName, int minutes, int weight) {
        exercise item = getExercise(exerciseName);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_exercise, item.getId());
        values.put(KEY_minutes, minutes);
        values.put(KEY_weight, weight);
        long rowId = db.insertOrThrow(LOG_TABLE, null, values);
        db.close();
    }

    public double calculateCalories(exercise item, int minutes, int weight) {
        // Convert weight from lbs to kg
        double kgW = weight * 0.453592;

        return item.getMet() * kgW * ((double)minutes/60);
    }

    public int getDailyCalories(int daysAgo) {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        String today, tomorrow, yesterday;
        c.add(Calendar.DATE, daysAgo*-1);
        today = format1.format(c.getTime());
        c.add(Calendar.DATE, 1);
        tomorrow = format1.format(c.getTime());
        c.add(Calendar.DATE, -2);
        yesterday = format1.format(c.getTime());

        String sumQuery = "SELECT * FROM " + LOG_TABLE + " WHERE "
                + KEY_timestamp + " > DATE('" + yesterday + "') AND "
                + KEY_timestamp + " < DATE('" + tomorrow + "');";
        Cursor cursor = db.rawQuery(sumQuery, null);

        double count = 0.0;

        if(cursor.moveToFirst()) {
            do {
                exercise item = getExercise(cursor.getInt(1));
                count += calculateCalories(item, cursor.getInt(2), cursor.getInt(3));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        db.close();

        return (int) Math.round(count);
    }

    public HashMap<Integer, Integer> getCalories(int range) {

        HashMap calories = new HashMap<Integer, Integer>();

        for(int i=0; i<range; i++)
            calories.put(i,getDailyCalories(i));

        return calories;
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void testPopulateExercises() {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //clear log_exercises table
        String clearMeals = "DELETE FROM " + LOG_TABLE;
        db.rawQuery(clearMeals,null);

        // Populate 100 previous days
        for(int i=0; i<100; i++) {
            int loops = randInt(0,3);
            // Log 0 to 3 exercises per day
            for(int k=loops; k>0; k--) {
                ContentValues values = new ContentValues();
                values.put(KEY_exercise, randInt(1, 15));
                values.put(KEY_minutes, randInt(20,60));
                values.put(KEY_weight, randInt(110,125));
                values.put(KEY_timestamp, sdf.format(c.getTime()));
                long rowId = db.insertOrThrow(LOG_TABLE, null, values);
            }
            c.add(Calendar.DATE, -1);
        }
        db.close();
    }
}
