package com.t3hh4xx0r.tweezee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_TOKEN = "oauth_token";
    public static final String KEY_SECRET = "oauth_token_secret";
    
    private static final String DATABASE_NAME = "tweezee.db";
    private static final String DATABASE_TABLE = "users";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table users (_id integer primary key autoincrement, "
            + "username text not null, user_id text not null, oauth_token text not null, oauth_token_secret text not null);";
        
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    public SQLiteDatabase db;

    public UDBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {
            Log.w("database", "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS favs");
            onCreate(db);
        }
    }    
    
    public void cleanDB() {
    	db.execSQL("delete from urls where rowid not in (select max(rowid) from urls group by url);");
    }
    //---opens the database---
    public UDBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
    	DBHelper.close();
    }
    
    //---insert a title into the database---
    public long insertUser(String name, String id, String token, String secret) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, name);
        initialValues.put(KEY_USERID, id);
        initialValues.put(KEY_TOKEN, token);
        initialValues.put(KEY_SECRET, secret);
        
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteFav(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + 
        		"=" + rowId, null) > 0;        		
    }
    

    //---retrieves all the titles---
    public Cursor getAllUsers() 
    {
    	Cursor mCursor = db.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID, 
                KEY_USERNAME,
                KEY_USERID,
                KEY_TOKEN,
                KEY_SECRET}, 
                null, 
                null, 
                null, 
                null, 
                null);
	
		return mCursor;
    }
    
    public Cursor getTitle(int songFinalValue) throws SQLException {
        Cursor mCursor =
                db.rawQuery("SELECT Title FROM MUSIC WHERE Id = 5899;", null);
                //db.query(DATABASE_TABLE, new String [] {KEY_TITLE}, KEY_ROWID + " = \'" + songFinalValue + "\'", null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
//    public Cursor getId(String url) throws SQLException {
//        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
//        		KEY_ROWID,
//        		}, 
//        		KEY_URL + "=" + url, 
//        		null,
//        		null, 
//        		null, 
//        		null, 
//        		null);
//
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//
//		return mCursor;
//    	
//    }
//
//
//    public Cursor getByIdent(String ident) throws SQLException {
//        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
//        		KEY_ROWID,
//        		KEY_TITLE,
//        		}, 
//        		KEY_IDENT + "=" + ident, 
//        		null,
//        		null, 
//        		null, 
//        		null, 
//        		null);
//
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//
//		return mCursor;
//    	
//    }
//    
//    public Cursor getUrl(String title) throws SQLException {
//        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
//        		"url",
//        		}, 
//        		title + "=" + title, 
//        		null,
//        		null, 
//        		null, 
//        		null, 
//        		null);
//
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//
//		return mCursor;
//    	
//    }
//    
//    //---deletes a particular title---
//    public boolean deleteUrl(long rowId) 
//    {
//        return db.delete(DATABASE_TABLE, KEY_ROWID + 
//        		"=" + rowId, null) > 0;        		
//    }
//    
//    //---retrieves a particular title---
//    public Cursor getUrl(long rowId) throws SQLException 
//    {
//        Cursor mCursor =
//                db.query(true, DATABASE_TABLE, new String[] {
//                		KEY_ROWID,
//                		KEY_URL
//                		}, 
//                		KEY_ROWID + "=" + rowId, 
//                		null,
//                		null, 
//                		null, 
//                		null, 
//                		null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//    }
//    
//    //---updates a title---
//    public boolean updateUrl(String ident, String title) 
//    {
//        ContentValues args = new ContentValues();
//        args.put(KEY_TITLE, title);
//        return db.update(DATABASE_TABLE, args, 
//                         KEY_IDENT + "=" + ident, null) > 0;
//    }
//
//	public Cursor byTitle(String[] title) throws SQLException {
//		  Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
//                  KEY_URL
//                  }, 
//                  "title=?", 
//                  title,
//                  null, 
//                  null, 
//                  null, 
//                  null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//   }

}