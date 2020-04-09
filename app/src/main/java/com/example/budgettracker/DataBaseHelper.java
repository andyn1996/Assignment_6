package com.example.budgettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ProgrammingKnowledge on 4/3/2015.
 *
 * Edited by Andy Ni on 4/5/2020
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Trans.db";
    public static final String TABLE_NAME = "transaction_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "DATE";
    public static final String COL_3 = "AMOUNT";
    public static final String COL_4 = "CATEGORY";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COL_1 + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT NOT NULL, "
                + COL_3 + " REAL NOT NULL, "
                + COL_4 + " TEXT NOT NULL)";

        db.execSQL(CREATE_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String date,double amount,String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,date);
        contentValues.put(COL_3,amount);
        contentValues.put(COL_4,category);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    
}