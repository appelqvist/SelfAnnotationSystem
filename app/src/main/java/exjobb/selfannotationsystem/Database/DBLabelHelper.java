package exjobb.selfannotationsystem.Database;

/**
 * Created by Andréas Appelqvist on 2017-02-23.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import exjobb.selfannotationsystem.ActivityWrapper;
import exjobb.selfannotationsystem.LabelWrapper;

public class DBLabelHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "labels.db";
    public static final String TABLE_LABELS = "labels";
    public static final String COLUMN_ID = "L_id";
    private static final String COLUMN_VALUE = "value";

    public DBLabelHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_LABELS + "(" +
                COLUMN_ID + " INTEGER, " +
                COLUMN_VALUE + " TEXT, "+
                "PRIMARY KEY ("+COLUMN_ID+")"+
                ");";
        db.execSQL(query);
        String[] defaults = {"Saknar Etikett","Transportsträcka", "Omedveten aktivitet", "Jobbaktivitet", "Skolaktivitet", "Träning", };

        for(String s : defaults) {
            addLabel(db, s);
        }
    }

    public void addLabel(SQLiteDatabase db,String text){
        ContentValues values = new ContentValues();
        values.put(COLUMN_VALUE, text);
        db.insert(TABLE_LABELS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_LABELS);
        onCreate(db);
    }

    public int addNewLabel(String value) {
        SQLiteDatabase db = getReadableDatabase();
        String query = " INSERT INTO "+ TABLE_LABELS +" ("+COLUMN_VALUE+") VALUES ('"+value+"');";
        db.execSQL(query);

        query = "SELECT * FROM "+TABLE_LABELS+" ORDER BY "+COLUMN_ID+" DESC LIMIT 1";
        Cursor c = db.rawQuery(query, null);
        int indexID, id;
        indexID = c.getColumnIndex(COLUMN_ID);
        c.moveToPosition(0);
        id = c.getInt(indexID);
        db.close();

        return id;
    }

    public List<LabelWrapper> getAllLabels() {
        int labelIndex, labelValue;
        SQLiteDatabase db = getReadableDatabase();
        String query = " SELECT * FROM " + TABLE_LABELS;
        Cursor c = db.rawQuery(query, null);
        LinkedList<LabelWrapper> labels = new LinkedList<>();
        labelValue = c.getColumnIndex(COLUMN_VALUE);
        labelIndex = c.getColumnIndex(COLUMN_ID);
        for(int i=0; i< c.getCount(); i++){
            c.moveToPosition(i);
            labels.add(new LabelWrapper(c.getInt(labelIndex),c.getString(labelValue)) );
        }
        return labels;
    }

}