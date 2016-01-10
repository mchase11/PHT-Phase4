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

public class vitalDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "pht.vitals.db";

    // Table information
    public static final String TABLE = "vitals";
    public static final String LOG_TABLE = "log_vitals";

    // Column information
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";
    public static final String KEY_log_id = "id";
    public static final String KEY_vital = "vital";
    public static final String KEY_value = "value";
    public static final String KEY_timestamp = "timestamp";

    // Data properties
    private int id;
    private String name;

    private int log_id;
    private int vital;
    private int value;

    public vitalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_VITALS = "CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_name + " TEXT )";

        db.execSQL(CREATE_TABLE_VITALS);

        String CREATE_TABLE_LOG_VITALS = "CREATE TABLE " + LOG_TABLE + "("
                + KEY_log_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_vital + " INTEGER, "
                + KEY_value + " INTEGER, "
                + KEY_timestamp + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + KEY_vital + ") REFERENCES " + TABLE + "(" + KEY_ID +"));";

        db.execSQL(CREATE_TABLE_LOG_VITALS);
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

    // Add new vital record
    public void addVital(vital item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_name, item.getName());
        long rowId = db.insertOrThrow(TABLE, null, values);
        db.close();
    }

    // Return a single vital item by id
    public vital getVital(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE,
                new String[] { KEY_ID, KEY_name },
                KEY_ID + "= ?", new String[] { String.valueOf(id) },
                null, null, "id ASC", "100");
        if (cursor != null)
            cursor.moveToFirst();
        vital item = new vital(cursor.getString(1));
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        if (cursor != null)
            cursor.close();
        return item;
    }

    // Return a single vital item by name
    public vital getVital(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE,
                new String[] { KEY_ID, KEY_name },
                KEY_name + "= ?", new String[] { name },
                null, null, "id ASC", "100");
        if (cursor != null)
            cursor.moveToFirst();
        vital item = new vital(cursor.getString(1));
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        if (cursor != null)
            cursor.close();
        return item;
    }

    public List<vital> getAllVitals() {
        List<vital> vitals = new ArrayList<vital>();
        String selectQuery = "Select * FROM " + TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do {
                vital item = new vital(cursor.getString(1));
                item.setId(cursor.getInt(0));
                vitals.add(item);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return vitals;
    }

    public int getVitalCount() {
        String countQuery = "Select * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateVital(vital item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_name, item.getName());
        int result = db.update(TABLE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId())});
        db.close();
        return result;
    }

    public void deleteVital(vital item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId()) });
        db.close();
    }

    public void populate() {

        List<String> vitals = new ArrayList<String>();
        vitals.add("Blood Pressure");
        vitals.add("Temperature");
        vitals.add("Pulse");
        vitals.add("Respiration");
        vitals.add("Sp02");

        for(String temp : vitals) addVital(new vital(temp));
    }

    public void logVital(String vitalName, int value) {
        vital item = getVital(vitalName);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_vital, item.getId());
        values.put(KEY_value, value);
        long rowId = db.insertOrThrow(LOG_TABLE, null, values);
        db.close();
    }

    public String getLatest() {
        SQLiteDatabase db = this.getReadableDatabase();
        String message = "";

        Cursor cursor = db.query(LOG_TABLE,
                new String[] { KEY_vital, KEY_value },null, null,
                null, null, "id DESC", "1");
        if (cursor != null)
            cursor.moveToFirst();
        if(cursor.getCount()==0){
            message =  "No vitals logged";
        }
        else {
            vital item = getVital(cursor.getInt(0));
            message = item.getName() + ": " + cursor.getString(1);
        }

        if (cursor != null)
            cursor.close();

        return message;
    }

    public HashMap<Integer, Integer> getVitals(int range, vital vital) {

        HashMap vitals = new HashMap<Integer, Integer>();

        for(int i=0; i<range; i++)
            vitals.put(i,getDailyVital(i, vital));

        return vitals;
    }

    public int getDailyVital(int daysAgo, vital vital) {
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

        String findQuery = "SELECT * FROM " + LOG_TABLE + " WHERE "
                + KEY_timestamp + " > DATE('" + yesterday + "') AND "
                + KEY_timestamp + " < DATE('" + tomorrow + "') AND "
                + KEY_vital + "=" + vital.getId() + ";";

        Cursor cursor = db.rawQuery(findQuery, null);

        int value = 0;

        if(cursor.getCount()!=0) {
            cursor.moveToFirst();
            value = cursor.getInt(2);
        }

        cursor.close();

        return value;
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

    public void testPopulateVitals() {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //clear log_exercises table
        String clearMeals = "DELETE FROM " + LOG_TABLE;
        db.rawQuery(clearMeals,null);

        // Populate 100 previous days
        for(int i=0; i<100; i++) {
            for(int k=1; k<6; k++) {
                ContentValues values = new ContentValues();
                values.put(KEY_vital, k);
                switch (k) {
                    case 1:
                        values.put(KEY_value, randInt(110,135));
                        break;
                    case 2:
                        values.put(KEY_value,randInt(97,100));
                        break;
                    case 3:
                        values.put(KEY_value,randInt(60,100));
                        break;
                    case 4:
                        values.put(KEY_value,randInt(12,20));
                        break;
                    case 5:
                        values.put(KEY_value,randInt(93,100));
                        break;
                }
                values.put(KEY_timestamp, sdf.format(c.getTime()));
                long rowId = db.insertOrThrow(LOG_TABLE, null, values);
            }
            c.add(Calendar.DATE, -1);
        }
        db.close();
    }


}
