package com.sudoajay.whatsapp_media_mover_to_sdcard.Database_Classes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class BackgroundTimerDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BackgroundTimerDataBase.db";
    private static final String DATABASE_TABLE_NAME = "BackgroundTimerDATABASE_TABLE_NAME";
    private static final String col_1 = "ID";
    private static final String col_2 = "Choose_Type";
    private static final String col_3 = "Repeatedly";
    private static final String col_4 = "Weekdays";
    private static final String col_5 = "Endlessly";

    public BackgroundTimerDataBase(Context context  )
    {
        super(context, DATABASE_NAME, null,1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DATABASE_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "Choose_Type INTEGER,Repeatedly INTEGER,Weekdays TEXT,Endlessly DEFAULT CURRENT_DATE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
        onCreate(db);
    }

    public void deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE_NAME, "ID =?", new String[]{1 + ""});
    }
    public void FillIt(int choose_Type , int repeatedly , String weekdays , String endlessly){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_2,choose_Type);
        contentValues.put(col_3,repeatedly);
        contentValues.put(col_4,weekdays);
        contentValues.put(col_5, endlessly);
        sqLiteDatabase.insert(DATABASE_TABLE_NAME,null,contentValues);
    }
    public boolean check_For_Empty(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery("select * from "+DATABASE_TABLE_NAME,null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        return count <= 0;
    }
    public Cursor GetTheValueFromId(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery( "SELECT * FROM " + DATABASE_TABLE_NAME,null);
    }
    public Cursor GetTheRepeatedlyWeekdays(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery( "SELECT  Repeatedly ,Weekdays FROM " + DATABASE_TABLE_NAME,null);
    }
    public Cursor GetTheChoose_TypeRepeatedlyEndlessly(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery("SELECT Repeatedly,Weekdays,Endlessly FROM "+ DATABASE_TABLE_NAME  ,null);
    }


    public void UpdateTheTable(String id , int choose_Type , int repeatedly , String weekdays , String endlessly){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1,id);
        contentValues.put(col_2,choose_Type);
        contentValues.put(col_3,repeatedly);
        contentValues.put(col_4,weekdays);
        contentValues.put(col_5, endlessly);
        sqLiteDatabase.update(DATABASE_TABLE_NAME,contentValues,"ID = ?",new String[] { id });
    }


}
