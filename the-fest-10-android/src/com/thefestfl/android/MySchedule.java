/*
 *  John R. Flynn
 *  "The Fest! 10"
 * 
 */

package com.thefestfl.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Class for working with the user's schedule DB

public class MySchedule {
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mySched;
	
	static final String KEY_ID = "_id";
	static final String KEY_ROW = "festrow";
	
	private static final String MYDB_CREATE =
        "create table if not exists mysched (" + 
        "_id integer primary key autoincrement, " +
        "festrow integer" +
        ");";
	
	private static final String DATABASE_NAME = "data";
    private static final String MYDB_TABLE = "mysched";
    private static final int DATABASE_VERSION = 2;
    
    // Helper subclass for the schedule db	
    private static class DatabaseHelper extends SQLiteOpenHelper {

    	DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);    
        }
    	
		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create the database!
			db.execSQL(MYDB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// What would be done if I was considering upgrades
			
		}
		
		public void onOpen(SQLiteDatabase db){
			db.execSQL(MYDB_CREATE);
		}
    }
    
    private final Context mCtx;
    
    public MySchedule(Context c){
    	mCtx = c;
    }
    
    // Opens the DB
    public MySchedule open() throws SQLException{
    	mDbHelper = new DatabaseHelper(mCtx);
    	mySched = mDbHelper.getWritableDatabase();
    	return this;
    }
	
    // Closes the DB
    public void close(){
    	mDbHelper.close();
    }
    
    // Add a show to the schedule
    public long addShow(int id){
    	ContentValues cv = new ContentValues();
    	cv.put(KEY_ROW, id);
    	return mySched.insert(MYDB_TABLE, null, cv);
    }
    
    // remove a show from the schedule
    public long removeShow(int id){
    	return mySched.delete(MYDB_TABLE, KEY_ID+"="+id, null);
    }
    
    // returns the whole schedule
    public Cursor getSched(){
    	return mySched.query(MYDB_TABLE, new String[]{KEY_ROW, KEY_ID}, null, null, null, null, null);
    }
    
    // check to see if a show is currently in the schedule
    public boolean checkShow(int row){
    	Cursor c = mySched.query(MYDB_TABLE, new String[]{KEY_ID}, KEY_ROW+"="+row, null, null, null, null);
    	if(c.getCount() == 0) return false; else return true;
    }
    
    // return the _id of a show
    public int getShowId(int row){
    	Cursor c = mySched.query(MYDB_TABLE, new String[]{KEY_ID}, KEY_ROW+"="+row, null, null, null, null);
    	c.moveToFirst();
    	return c.getInt(0);
    	
    }
}
