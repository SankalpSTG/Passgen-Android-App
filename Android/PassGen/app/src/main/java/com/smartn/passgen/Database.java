package com.smartn.passgen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {



    //MasterData
    public static final String DATABASE_NAME="data.db";
    public static final String TABLE_NAME_MASTER="masterfile";
    public static final String COLUMN1="UNICID";
    public static final String COLUMN2="USERNAME";
    public static final String COLUMN3="MASTERPASSWORD";



    public Database(Context context) {
        //check this code
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE "+TABLE_NAME_MASTER+" (UNICID TEXT,USERNAME TEXT,MASTERPASSWORD TEXT)";
        db.execSQL(CREATE_TABLE);
        // CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" (WEBSITE TEXT,USERNAME TEXT,PASSWORD TEXT,SALT TEXT)";
        // db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public Cursor getAllDataMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME_MASTER,null);
        return res;
    }

    public boolean updateDataMaster(String unicid , String username, String password)
    {
        SQLiteDatabase dataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN1, unicid);
        values.put(COLUMN2, username);
        values.put(COLUMN3, password);
        dataBase.update(TABLE_NAME_MASTER,values,"UNICID = ?",new String[] {unicid});
        return true;
    }

    public boolean insertDataMaster(String unicid , String username, String password) {

        SQLiteDatabase dataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN1, unicid);
        values.put(COLUMN2, username);
        values.put(COLUMN3, password);
        long result= dataBase.insert(TABLE_NAME_MASTER, null, values);
        if(result==-1)
            return false;
        else
            return true;
    }

    public void deleteMasterPassword()
    {
        SQLiteDatabase dataBase = this.getWritableDatabase();
         dataBase.delete(TABLE_NAME_MASTER,null,null);
    }

/*
  //PasswordDatabse

    public static final String TABLE_NAME="passwordfile";
    public static final String COL1="WEBSITE";
    public static final String COL2="USERNAME";
    public static final String COL3="PASSWORD";
    public static final String COL4="SALT";

    public boolean insertDataPassword(String website , String username, String password,String salt) {

        SQLiteDatabase dataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL1, website);
        values.put(COL2, username);
        values.put(COL3, password);
        values.put(COL4, salt);
        long result= dataBase.insert(TABLE_NAME, null, values);
        if(result==-1)
            return false;
        else
            return true;
    }


    public Cursor getAllDataPassword(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public Cursor searchDataPassword(String website){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME+" where "+COL1+" like '%"+website+"%'",null);
        return res;
    }

    public boolean updateDataPassword(String website, String username, String password,String salt)
    {
        SQLiteDatabase dataBase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL1, website);
        values.put(COL2, username);
        values.put(COL3, password);
        values.put(COL4, salt);
        dataBase.update(TABLE_NAME,values,"WEBSITE = ?",new String[] {website});
        return true;
    }

    public Integer deleteDataPassword(String website,String username)
    {
        SQLiteDatabase dataBase = this.getWritableDatabase();
        return dataBase.delete(TABLE_NAME,"WEBSITE = ? AND USERNAME=?",new String[] {website,username});
    }

*/
}

