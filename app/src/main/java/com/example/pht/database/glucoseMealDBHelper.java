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
import java.util.List;
import java.util.Random;

public class glucoseMealDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "pht.glucoseMeals.db";

    // Table information
    public static final String GLUCOSE_TABLE = "log_glucose";
    public static final String MEAL_TABLE = "meals";
    public static final String LOG_TABLE = "log_meals";

    // Column information
    public static final String KEY_GLUCOSE_ID = "id";
    public static final String KEY_GLUCOSE_value = "value";
    public static final String KEY_GLUCOSE_timestamp = "timestamp";

    public static final String KEY_MEAL_ID = "id";
    public static final String KEY_MEAL_name = "name";

    public static final String KEY_LOG_ID = "id";
    public static final String KEY_LOG_meal = "meal";
    public static final String KEY_LOG_calories = "calories";
    public static final String KEY_LOG_timestamp = "timestamp";

    // Data properties
    private int glucose_id;
    private int value;

    private int meal_id;
    private String name;

    private int log_id;
    private int calories;

    public glucoseMealDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_GLUCOSE = "CREATE TABLE " + GLUCOSE_TABLE + "("
                + KEY_GLUCOSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_GLUCOSE_value + " INTEGER, "
                + KEY_GLUCOSE_timestamp + " DATETIME DEFAULT CURRENT_TIMESTAMP )";

        db.execSQL(CREATE_TABLE_GLUCOSE);

        String CREATE_TABLE_MEAL = "CREATE TABLE " + MEAL_TABLE + "("
                + KEY_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_MEAL_name + " STRING )";

        db.execSQL(CREATE_TABLE_MEAL);

        String CREATE_TABLE_LOG_CALORIES = "CREATE TABLE " + LOG_TABLE + "("
                + KEY_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_LOG_meal + " INTEGER, "
                + KEY_LOG_calories + " INTEGER, "
                + KEY_LOG_timestamp + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + KEY_LOG_meal + ") REFERENCES " + MEAL_TABLE + "(" + KEY_MEAL_ID + "))";

        db.execSQL(CREATE_TABLE_LOG_CALORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GLUCOSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MEAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Add new meal record
    public void addMeal(meal item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MEAL_name, item.getName());
        long rowId = db.insertOrThrow(MEAL_TABLE, null, values);
        db.close();
    }

    // Return a single meal item by id
    public meal getMeal(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL_TABLE,
                new String[] { KEY_MEAL_ID, KEY_MEAL_name },
                KEY_MEAL_ID + "= ?", new String[] { String.valueOf(id) },
                null, null, "id ASC", "100");
        if (cursor != null)
            cursor.moveToFirst();
        meal item = new meal(cursor.getString(1));
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MEAL_ID)));
        if (cursor != null)
            cursor.close();
        return item;
    }

    // Return a single meal item by name
    public meal getMeal(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL_TABLE,
                new String[] { KEY_MEAL_ID, KEY_MEAL_name },
                KEY_MEAL_name + "= ?", new String[] { name },
                null, null, "id ASC", "100");
        if (cursor != null)
            cursor.moveToFirst();
        meal item = new meal(cursor.getString(1));
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MEAL_ID)));
        if (cursor != null)
            cursor.close();
        return item;
    }

    public List<meal> getAllMeals() {
        List<meal> meals = new ArrayList<meal>();
        String selectQuery = "Select * FROM " + MEAL_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                meal item = new meal(cursor.getString(1));
                item.setId(cursor.getInt(0));
                meals.add(item);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return meals;
    }

    public int getMealCount() {
        String countQuery = "Select * FROM " + MEAL_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateMeal(meal item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEAL_name, item.getName());
        int result = db.update(MEAL_TABLE, values, KEY_MEAL_ID + " = ?",
                new String[] { String.valueOf(item.getId())});
        db.close();
        return result;
    }

    public void deleteMeal(meal item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MEAL_TABLE, KEY_MEAL_ID + " = ?",
                new String[] { String.valueOf(item.getId()) });
        db.close();
    }

    public void populate() {

        List<String> vitals = new ArrayList<String>();
        vitals.add("Breakfast");
        vitals.add("Lunch");
        vitals.add("Dinner");
        vitals.add("Snack");

        for(String temp : vitals) addMeal(new meal(temp));
    }

    public void logCalories(String mealName, int value) {
        meal item = getMeal(mealName);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOG_meal, item.getId());
        values.put(KEY_LOG_calories, value);
        long rowId = db.insertOrThrow(LOG_TABLE, null, values);
        db.close();
    }

    public void logGlucose(int value) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GLUCOSE_value, value);
        long rowId = db.insertOrThrow(GLUCOSE_TABLE, null, values);
        db.close();
    }

    public int getLastGlucose() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(GLUCOSE_TABLE,
                new String[] { KEY_GLUCOSE_ID, KEY_GLUCOSE_value },null, null,
                null, null, "id DESC", "1");
        if (cursor != null)
            cursor.moveToFirst();
        int item = (cursor.getCount() == 0) ? 0 : cursor.getInt(1);
        if (cursor != null)
            cursor.close();
        db.close();
        return item;
    }

    public int getDailyGlucose(int daysBack) {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        String today, tomorrow, yesterday;
        c.add(Calendar.DATE, daysBack*-1);
        today = format1.format(c.getTime());
        c.add(Calendar.DATE, 1);
        tomorrow = format1.format(c.getTime());
        c.add(Calendar.DATE, -2);
        yesterday = format1.format(c.getTime());

        String sumQuery = "SELECT "+ KEY_GLUCOSE_value +" FROM " + GLUCOSE_TABLE + " WHERE "
                + KEY_GLUCOSE_timestamp + " > DATE('" + yesterday + "') AND "
                + KEY_GLUCOSE_timestamp + " < DATE('" + tomorrow + "');";
        Cursor cursor = db.rawQuery(sumQuery, null);

        if (cursor != null)
            cursor.moveToFirst();
        int item = (cursor.getCount() == 0) ? 0 : cursor.getInt(0);
        if (cursor != null)
            cursor.close();

        db.close();
        return item;
    }

    public HashMap<Integer, Integer> getGlucose(int range) {

        HashMap glucose = new HashMap<Integer, Integer>();

        for(int i=0; i<range; i++)
            glucose.put(i,getDailyGlucose(i));

        return glucose;
    }


    public int getDailyCalories(int daysBack) {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        String today, tomorrow, yesterday;
        c.add(Calendar.DATE, daysBack*-1);
        today = format1.format(c.getTime());
        c.add(Calendar.DATE, 1);
        tomorrow = format1.format(c.getTime());
        c.add(Calendar.DATE, -2);
        yesterday = format1.format(c.getTime());

        String sumQuery = "SELECT SUM("+ KEY_LOG_calories +") FROM " + LOG_TABLE + " WHERE "
                + KEY_LOG_timestamp + " > DATE('" + yesterday + "') AND "
                + KEY_LOG_timestamp + " < DATE('" + tomorrow + "');";
        Cursor cursor = db.rawQuery(sumQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        int count = (cursor.getCount() == 0) ? 0 : cursor.getInt(0);

        if (cursor != null)
            cursor.close();

        db.close();
        return count;
    }

    public HashMap<Integer, Integer> getCalories(int range) {

        HashMap calories = new HashMap<Integer, Integer>();

        for(int i=0; i<range; i++)
            calories.put(i,getDailyCalories(i));

        return calories;
    }

    public void testPopulateGlucose() {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //clear glucose table
        String clearGlucose = "DELETE FROM " + GLUCOSE_TABLE;
        db.rawQuery(clearGlucose,null);

        // Populate 100 previous days
        for(int i=0; i<100; i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_GLUCOSE_value, randInt(70,100));
            values.put(KEY_GLUCOSE_timestamp, sdf.format(c.getTime()));
            long rowId = db.insertOrThrow(GLUCOSE_TABLE, null, values);
            c.add(Calendar.DATE, -1);
        }
        db.close();
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

    public void testPopulateMeals() {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //clear glucose table
        String clearMeals = "DELETE FROM " + LOG_TABLE;
        db.rawQuery(clearMeals,null);

        // Populate 100 previous days
        for(int i=0; i<100; i++) {
            int loops = randInt(2,4);
            // Log 2 to 4 meals per day
            for(int k=loops; k>0; k--) {
                ContentValues values = new ContentValues();
                values.put(KEY_LOG_calories, randInt(270, 450));
                values.put(KEY_LOG_meal, k);
                values.put(KEY_LOG_timestamp, sdf.format(c.getTime()));
                long rowId = db.insertOrThrow(LOG_TABLE, null, values);
            }
            c.add(Calendar.DATE, -1);
        }
        db.close();
    }
}
