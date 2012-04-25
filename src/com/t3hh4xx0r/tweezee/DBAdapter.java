package com.t3hh4xx0r.tweezee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_TOKEN = "oauth_token";
    public static final String KEY_SECRET = "oauth_token_secret";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_FRIEND_IDS = "friend_ids";
    
    public static final String KEY_AMOUNT = "send_amount";
    public static final String KEY_WAIT = "send_wait";
    public static final String KEY_DAY = "send_day";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_MENTIONS = "mentions";
    
    private static final String DATABASE_NAME = "tweezee.db";
    private static final String USER_TABLE = "users";
    private static final String ENTRY_TABLE = "entries";
    private static final int DATABASE_VERSION = 3;

    private static final String CREATE_USERS =
            "create table users (_id integer primary key autoincrement, "
            + "username text not null, user_id text not null, oauth_token text not null, oauth_token_secret text not null, friends text not null, friend_ids text not null);";
        
    private static final String CREATE_ENTRIES =
            "create table entries (_id integer primary key autoincrement, "
                    + "username text not null, message text not null, mentions text not null, send_amount text not null, send_wait text not null, send_day text not null);";
         
    
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    public SQLiteDatabase db;

    public DBAdapter(Context ctx) 
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
            db.execSQL(CREATE_USERS);
            db.execSQL(CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {
            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS entries");
            onCreate(db);
        }
    }    
    
    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
    	DBHelper.close();
    }
    
    public long insertEntry(String name, String message, String amount, String wait, String day, String mentions) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, name);
        initialValues.put(KEY_MESSAGE, message);
        initialValues.put(KEY_AMOUNT, amount);
        initialValues.put(KEY_WAIT, wait);
        initialValues.put(KEY_DAY, day);
        initialValues.put(KEY_MENTIONS, mentions);
        
        return db.insert(ENTRY_TABLE, null, initialValues);
    }

    public long insertUser(String name, String id, String token, String secret) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, name);
        initialValues.put(KEY_USERID, id);
        initialValues.put(KEY_TOKEN, token);
        initialValues.put(KEY_SECRET, secret);
        initialValues.put(KEY_FRIENDS, "");
        initialValues.put(KEY_FRIEND_IDS, "");
        
        return db.insert(USER_TABLE, null, initialValues);
    }    
    
    public Cursor getAllEntries() 
    {
    	Cursor mCursor = db.query(ENTRY_TABLE, new String[] {
                KEY_USERNAME,
                KEY_MESSAGE,
                KEY_AMOUNT,
                KEY_WAIT,
                KEY_DAY,
                KEY_MENTIONS}, 
                null,
                null, 
                null, 
                null, 
                null);
	
		return mCursor;
    }
    
    public Cursor getAllUsers() 
    {
    	Cursor mCursor = db.query(USER_TABLE, new String[] {
        		KEY_ROWID, 
                KEY_USERNAME,
                KEY_USERID,
                KEY_TOKEN,
                KEY_SECRET,
                KEY_FRIENDS,
                KEY_FRIEND_IDS}, 
                null, 
                null, 
                null, 
                null, 
                null);
	
		return mCursor;
    }

    public boolean deleteEntry(String[] message) {
    	
        Cursor mCursor = db.query(true, ENTRY_TABLE, new String[] {
        		KEY_ROWID
        		}, 
        		"message=?", 
        		message,
        		null, 
        		null, 
        		null, 
        		null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
                
        return db.delete(ENTRY_TABLE, KEY_ROWID + 
        		"=" + mCursor.getString(0), null) > 0;        		
    }	
    
   public boolean deleteUser(String[] user) {
    	
        Cursor mCursor = db.query(true, USER_TABLE, new String[] {
        		KEY_ROWID
        		}, 
        		"username=?", 
        		user,
        		null, 
        		null, 
        		null, 
        		null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
                
        return db.delete(USER_TABLE, KEY_ROWID + 
        		"=" + mCursor.getString(0), null) > 0;  
    }	
   
    public void addFriends(String user, String friends) {
	        ContentValues args = new ContentValues();
	        args.put(KEY_FRIENDS, friends);
	        this.db.update(USER_TABLE, args, "username = ?", new String[] {user});
    }

	public void addFriendIds(String user, String ids) {
		ContentValues args = new ContentValues();
	    args.put(KEY_FRIEND_IDS, ids);
	    this.db.update(USER_TABLE, args, "username = ?", new String[] {user});
	}

	public void updateEntry(String user, String message, String mentions, String og, String wait, String amount, String days) {
		ContentValues args = new ContentValues();
	    args.put(KEY_MESSAGE, message);
	    args.put(KEY_MENTIONS, mentions);
	    args.put(KEY_AMOUNT, amount);
	    args.put(KEY_WAIT, wait);
	    args.put(KEY_DAY, days);
	    this.db.update(ENTRY_TABLE, args, ("message = ? AND username = ?"), new String[] {og, user});
	}
	
	public boolean isLoggedIn() {
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM USERS", null);
		if (cur != null) {
		    cur.moveToFirst();
		    if (cur.getInt (0) == 0) {
		    	cur.close();
		    	return false;
		    } else{
		    	cur.close();
		    	return true;
		    }
		} else {
			return false;
		}			
	}
}