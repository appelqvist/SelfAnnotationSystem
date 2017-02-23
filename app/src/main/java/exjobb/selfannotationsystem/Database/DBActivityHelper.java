package exjobb.selfannotationsystem.Database;

/**
 * Created by Marten on 2017-02-22.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.StringBuilderPrinter;

import exjobb.selfannotationsystem.ActivityWrapper;

public class DBActivityHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "activites.db";
    private static final String TABLE_ACTIVITES = "activities";
    private static final String COLUMN_ID = "A_id";
    private static final String COLUMN_STEPS = "steps";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_LABEL = "label";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";

    public DBActivityHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_ACTIVITES  + "(" +
                COLUMN_ID + " INTEGER AUTOINCREMENT, " + //PRIMARY KEY AUTOINCREMENT
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_STEPS + " INTEGER, " +
                COLUMN_DISTANCE + " INTEGER, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_LABEL + " INTEGER, "+
                "PRIMARY KEY ("+COLUMN_ID+"),"+
                "FOREIGN KEY ("+COLUMN_LABEL+") REFERENCES "+DBLabelHelper.TABLE_LABELS+"("+DBLabelHelper.COLUMN_ID+")"+
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_ACTIVITES);
        onCreate(db);
    }

    //Add a new row to the database
    public void addActivity(ActivityWrapper activityWrapper){
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, activityWrapper.getDate());
        values.put(COLUMN_TIME, activityWrapper.getTime());
        values.put(COLUMN_STEPS, activityWrapper.getSteps());
        values.put(COLUMN_DISTANCE, activityWrapper.getDistance());
        values.put(COLUMN_TYPE, activityWrapper.getActivityType());
        values.put(COLUMN_LABEL, 0); //Default
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ACTIVITES, null, values);
        db.close();
    }

    //Print out database
    public ActivityWrapper[] printDB(){
        int steps, distance, dateIndex, timeIndex, type, labelIndex;
        SQLiteDatabase db = getReadableDatabase();
        String query = " SELECT * FROM " + TABLE_ACTIVITES;
        Cursor c = db.rawQuery(query, null);
        ActivityWrapper[] activities = new ActivityWrapper[c.getCount()];
        steps = c.getColumnIndex(COLUMN_STEPS);
        distance = c.getColumnIndex(COLUMN_DISTANCE);
        dateIndex = c.getColumnIndex(COLUMN_DATE);
        timeIndex = c.getColumnIndex(COLUMN_TIME);
        type = c.getColumnIndex(COLUMN_TYPE);
        labelIndex = c.getColumnIndex(COLUMN_LABEL);

        for(int i=0; i< activities.length; i++){
            c.moveToPosition(i);
            activities[i] = new ActivityWrapper(c.getString(dateIndex),c.getString(timeIndex),
                    c.getInt(steps), c.getInt(distance), c.getString(type), c.getString(labelIndex));
        }
        db.close();
        return activities;
    }

    public ActivityWrapper[] getActivitesByDate(String date){
        int steps, distance, dateIndex, timeIndex, type, labelIndex;
        SQLiteDatabase db = getReadableDatabase();
        String query = " SELECT * FROM " + TABLE_ACTIVITES+ " WHERE "+COLUMN_DATE+"='"+ date +"'";
        Cursor c = db.rawQuery(query, null);
        ActivityWrapper[] activities = new ActivityWrapper[c.getCount()];
        steps = c.getColumnIndex(COLUMN_STEPS);
        distance = c.getColumnIndex(COLUMN_DISTANCE);
        dateIndex = c.getColumnIndex(COLUMN_DATE);
        timeIndex = c.getColumnIndex(COLUMN_TIME);
        type = c.getColumnIndex(COLUMN_TYPE);
        labelIndex = c.getColumnIndex(COLUMN_LABEL);

        for(int i=0; i< activities.length; i++){
            c.moveToPosition(i);
            activities[i] = new ActivityWrapper(c.getString(dateIndex),c.getString(timeIndex),
                    c.getInt(steps), c.getInt(distance), c.getString(type), c.getString(labelIndex));
        }
        db.close();
        return activities;
    }
//
//    public ActivityWrapper[] outcomes(){
//        int amountIndex, titleIndex, dateIndex, categoryIndex;
//        SQLiteDatabase db = getReadableDatabase();
//        String query = " SELECT * FROM " + TABLE_TRANSACTION + " WHERE AMOUNT < 0 ";
//        Cursor c = db.rawQuery(query, null);
//        ActivityWrapper[] outComes = new ActivityWrapper[c.getCount()];
//        amountIndex = c.getColumnIndex(COLUMN_AMOUNT);
//        titleIndex = c.getColumnIndex(COLUMN_TITLE);
//        dateIndex = c.getColumnIndex(COLUMN_DATE);
//        categoryIndex = c.getColumnIndex(COLUMN_CATEGORY);
//
//        for(int i=0; i< outComes.length; i++){
//            c.moveToPosition(i);
//            outComes[i] = new ActivityWrapper(c.getInt(amountIndex),c.getString(titleIndex),
//                    c.getString(dateIndex), c.getString(categoryIndex));
//        }
//        db.close();
//        return outComes;
//    }
//
//    public int getSaldo(){
//        int amountFromDB;
//        SQLiteDatabase db = getReadableDatabase();
//        String query = "SELECT SUM(AMOUNT) FROM " + TABLE_TRANSACTION;
//        Cursor c = db.rawQuery(query, null);
//        if(c.moveToFirst()){
//            amountFromDB = c.getInt(0);
//        }
//        else {
//            amountFromDB = -1;
//            c.close();
//        }
//        return amountFromDB;
//    }
}
