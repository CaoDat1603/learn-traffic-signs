package com.example.myapplication.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.data.model.TrafficSign;

import java.util.ArrayList;
import java.util.List;

public class TrafficSignDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "traffic_signs.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "traffic_signs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_STATUS = "status";

    public TrafficSignDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_STATUS + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertTrafficSign(TrafficSign sign) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, sign.getId());
        values.put(COLUMN_NAME, sign.getName());
        values.put(COLUMN_DESCRIPTION, sign.getDescription());
        values.put(COLUMN_IMAGE, sign.getImage());
        values.put(COLUMN_TYPE, sign.getType());
        values.put(COLUMN_STATUS, sign.getStatus());

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<TrafficSign> getTrafficSigns(String filterType) {
        List<TrafficSign> trafficSigns = new ArrayList<>();
        String query;
        String[] args = null;

        if (filterType.equalsIgnoreCase("all")) {
            query = "SELECT * FROM " + TABLE_NAME;
        } else {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TYPE + " = ?";
            args = new String[]{filterType};
        }

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, args)) {

            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));

                    TrafficSign sign = new TrafficSign(id, name, description, imagePath, type, status);
                    trafficSigns.add(sign);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trafficSigns;
    }
    
    public TrafficSign getTrafficSignById(String idCur) {
        TrafficSign sign = null;

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{idCur})) {

            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));

                sign = new TrafficSign(id, name, description, imagePath, type, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sign;
    }

    public void updateTrafficSignStatus(String id, String newStatus) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, newStatus);
            db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
