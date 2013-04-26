package com.t3hh4xx0r.tweezee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_USERID = "user_id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_TOKEN = "oauth_token";
	public static final String KEY_SECRET = "oauth_token_secret";

	public static final String KEY_SEND_TO = "send_to";

	public static final String KEY_WAIT = "send_wait";
	public static final String KEY_DAY = "send_day";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_SUBJECT = "subject";
	public static final String KEY_MENTIONS = "mentions";
	public static final String KEY_TIME = "send_time";
	public static final String KEY_DATE = "send_date";
	public static final String KEY_ACTIVE = "active";
	public static final String BOOT = "start_boot";
	public static final String ID = "my_id";

	private static final String DATABASE_NAME = "tweezee.db";
	private static final String T_USER_TABLE = "twitter_users";
	private static final String T_ENTRY_TABLE = "twitter_entries";
	private static final String S_ENTRY_TABLE = "sms_entries";
	private static final String E_USER_TABLE = "email_users";
	private static final String E_ENTRY_TABLE = "email_entries";

	private static final int DATABASE_VERSION = 30;

	private static final String CREATE_E_USERS = "create table email_users (_id integer primary key autoincrement, "
			+ "username text not null, password text not null);";

	private static final String CREATE_E_ENTRIES = "create table email_entries (_id integer primary key autoincrement, "
			+ "username text not null, send_date text not null, "
			+ "message text not null, subject text not null, send_to text not null, "
			+ " send_wait text not null, send_day text not null, send_time text not null, "
			+ " start_boot text not null, my_id text not null, active text not null);";

	private static final String CREATE_T_USERS = "create table twitter_users (_id integer primary key autoincrement, "
			+ "username text not null, user_id text not null, oauth_token text not null, oauth_token_secret text not null);";

	private static final String CREATE_T_ENTRIES = "create table twitter_entries (_id integer primary key autoincrement, "
			+ "username text not null, message text not null, mentions text not null, "
			+ " send_wait text not null, send_day text not null, send_time text not null, "
			+ " start_boot text not null, my_id text not null, active text not null, send_date text not null);";

	private static final String CREATE_S_ENTRIES = "create table sms_entries (_id integer primary key autoincrement, "
			+ "message text not null, send_to text not null, "
			+ " send_wait text not null, send_day text not null, send_time text not null, "
			+ " start_boot text not null, my_id text not null, active text not null, send_date text not null);";

	private final Context context;

	private DatabaseHelper DBHelper;
	public SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_E_USERS);
			db.execSQL(CREATE_E_ENTRIES);
			db.execSQL(CREATE_T_USERS);
			db.execSQL(CREATE_T_ENTRIES);
			db.execSQL(CREATE_S_ENTRIES);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS twitter_users");
			db.execSQL("DROP TABLE IF EXISTS twitter_entries");
			db.execSQL("DROP TABLE IF EXISTS sms_entries");
			db.execSQL("DROP TABLE IF EXISTS email_users");
			db.execSQL("DROP TABLE IF EXISTS email_entries");
			onCreate(db);
		}
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	public void insertEUser(String username, String password) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERNAME, username);
		initialValues.put(KEY_PASSWORD, password);
		db.insert(E_USER_TABLE, null, initialValues);
	}

	public void insertSEntry(String message, String wait, String day,
			String send_to, String time, String boot, int id, String date) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MESSAGE, message);
		initialValues.put(KEY_WAIT, wait);
		initialValues.put(KEY_DAY, day);
		initialValues.put(KEY_SEND_TO, send_to);
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_DATE, date);
		initialValues.put(BOOT, boot);
		initialValues.put(KEY_ACTIVE, "true");
		initialValues.put(ID, Integer.toString(id));

		db.insert(S_ENTRY_TABLE, null, initialValues);
	}

	public void insertEEntry(String username, String subject, String message,
			String wait, String day, String send_to, String time, String date,
			String boot, int id) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SUBJECT, subject);
		initialValues.put(KEY_USERNAME, username);
		initialValues.put(KEY_MESSAGE, message);
		initialValues.put(KEY_WAIT, wait);
		initialValues.put(KEY_DAY, day);
		initialValues.put(KEY_SEND_TO, send_to);
		initialValues.put(KEY_TIME, time);
		initialValues.put(BOOT, boot);
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_ACTIVE, "true");
		initialValues.put(ID, Integer.toString(id));

		db.insert(E_ENTRY_TABLE, null, initialValues);
	}

	public long insertTEntry(String name, String message, String wait,
			String day, String mentions, String time, String boot, int id,
			String date) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERNAME, name);
		initialValues.put(KEY_MESSAGE, message);
		initialValues.put(KEY_WAIT, wait);
		initialValues.put(KEY_DAY, day);
		initialValues.put(KEY_MENTIONS, mentions);
		initialValues.put(KEY_TIME, time);
		initialValues.put(BOOT, boot);
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_ACTIVE, "true");
		initialValues.put(ID, Integer.toString(id));

		return db.insert(T_ENTRY_TABLE, null, initialValues);
	}

	public long insertTUser(String name, String id, String token, String secret) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERNAME, name);
		initialValues.put(KEY_USERID, id);
		initialValues.put(KEY_TOKEN, token);
		initialValues.put(KEY_SECRET, secret);

		return db.insert(T_USER_TABLE, null, initialValues);
	}

	public Cursor getAllEUsers() {
		Cursor mCursor = db.query(E_USER_TABLE, new String[] { KEY_USERNAME,
				KEY_PASSWORD }, null, null, null, null, null);

		return mCursor;
	}

	public Cursor getAllEEntries() {
		Cursor mCursor = db.query(E_ENTRY_TABLE, new String[] { KEY_USERNAME,
				KEY_MESSAGE, KEY_WAIT, KEY_DAY, KEY_SEND_TO, KEY_TIME, BOOT,
				KEY_SUBJECT, KEY_ACTIVE, KEY_DATE, ID }, null, null, null,
				null, null);

		return mCursor;
	}

	public Cursor getAllSEntries() {
		Cursor mCursor = db.query(S_ENTRY_TABLE, new String[] { KEY_MESSAGE,
				KEY_WAIT, KEY_DAY, KEY_SEND_TO, KEY_TIME, BOOT, KEY_ACTIVE,
				KEY_DATE, ID }, null, null, null, null, null);

		return mCursor;
	}

	public Cursor getAllTEntries() {
		Cursor mCursor = db.query(T_ENTRY_TABLE, new String[] { KEY_USERNAME,
				KEY_MESSAGE, KEY_WAIT, KEY_DAY, KEY_MENTIONS, KEY_TIME, BOOT,
				KEY_ACTIVE, KEY_DATE, ID }, null, null, null, null, null);

		return mCursor;
	}

	public Cursor getAllTUsers() {
		Cursor mCursor = db.query(T_USER_TABLE, new String[] { KEY_ROWID,
				KEY_USERNAME, KEY_USERID, KEY_TOKEN, KEY_SECRET }, null, null,
				null, null, null);

		return mCursor;
	}

	public boolean deleteTEntry(String[] message) {

		Cursor mCursor = db.query(true, T_ENTRY_TABLE,
				new String[] { KEY_ROWID }, "message=?", message, null, null,
				null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return db.delete(T_ENTRY_TABLE, KEY_ROWID + "=" + mCursor.getString(0),
				null) > 0;
	}

	public boolean deleteSEntry(String message, String recipient) {

		Cursor mCursor = db.query(true, S_ENTRY_TABLE,
				new String[] { KEY_ROWID }, "message=? AND send_to=?",
				new String[] { message, recipient }, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return db.delete(S_ENTRY_TABLE, KEY_ROWID + "=" + mCursor.getString(0),
				null) > 0;
	}

	public boolean deleteEEntry(String message, String recipient) {

		Cursor mCursor = db.query(true, E_ENTRY_TABLE,
				new String[] { KEY_ROWID }, "message=? AND send_to=?",
				new String[] { message, recipient }, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return db.delete(E_ENTRY_TABLE, KEY_ROWID + "=" + mCursor.getString(0),
				null) > 0;
	}

	public boolean deleteEUser(String[] user) {

		Cursor mCursor = db.query(true, E_USER_TABLE,
				new String[] { KEY_ROWID }, "username=?", user, null, null,
				null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return db.delete(E_USER_TABLE, KEY_ROWID + "=" + mCursor.getString(0),
				null) > 0;
	}

	public boolean deleteTUser(String[] user) {

		Cursor mCursor = db.query(true, T_USER_TABLE,
				new String[] { KEY_ROWID }, "username=?", user, null, null,
				null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return db.delete(T_USER_TABLE, KEY_ROWID + "=" + mCursor.getString(0),
				null) > 0;
	}

	public void updateSEntry(String message, String og, String wait,
			String days, String send_to, String time, String boot, String date) {
		ContentValues args = new ContentValues();
		args.put(KEY_MESSAGE, message);
		args.put(KEY_SEND_TO, send_to);
		args.put(KEY_WAIT, wait);
		args.put(KEY_DAY, days);
		args.put(KEY_TIME, time);
		args.put(BOOT, boot);
		args.put(KEY_DATE, date);
		this.db.update(S_ENTRY_TABLE, args, ("message = ? AND send_to = ?"),
				new String[] { og, send_to });
	}

	public void updateEEntry(String usern, String subject, String message,
			String og, String wait, String days, String send_to, String time,
			String boot, String date) {
		ContentValues args = new ContentValues();
		args.put(KEY_SUBJECT, subject);
		args.put(KEY_USERNAME, usern);
		args.put(KEY_MESSAGE, message);
		args.put(KEY_SEND_TO, send_to);
		args.put(KEY_WAIT, wait);
		args.put(KEY_DAY, days);
		args.put(KEY_TIME, time);
		args.put(BOOT, boot);
		args.put(KEY_DATE, date);
		this.db.update(E_ENTRY_TABLE, args, ("message = ? AND send_to = ?"),
				new String[] { og, send_to });
	}

	public void updateFEntry(String message, String og, String wait,
			String days, String time, String boot, String date) {
		ContentValues args = new ContentValues();
		args.put(KEY_MESSAGE, message);
		args.put(KEY_WAIT, wait);
		args.put(KEY_DAY, days);
		args.put(KEY_TIME, time);
		args.put(BOOT, boot);
		args.put(KEY_DATE, date);
		this.db.update(E_ENTRY_TABLE, args, ("message = ?"),
				new String[] { og });
	}

	public void updateTEntry(String user, String message, String mentions,
			String og, String wait, String days, String time, String boot,
			String date) {
		ContentValues args = new ContentValues();
		args.put(KEY_MESSAGE, message);
		args.put(KEY_MENTIONS, mentions);
		args.put(KEY_WAIT, wait);
		args.put(KEY_DAY, days);
		args.put(KEY_TIME, time);
		args.put(KEY_DATE, date);
		args.put(BOOT, boot);
		this.db.update(T_ENTRY_TABLE, args, ("message = ? AND username = ?"),
				new String[] { og, user });
	}

	public void updateActiveE(String user, String message, boolean active) {
		ContentValues args = new ContentValues();
		args.put(KEY_ACTIVE, Boolean.toString(active));
		this.db.update(E_ENTRY_TABLE, args, ("message = ? AND username = ?"),
				new String[] { message, user });
	}

	public void updateActiveT(String user, String message, boolean active) {
		ContentValues args = new ContentValues();
		args.put(KEY_ACTIVE, Boolean.toString(active));
		this.db.update(T_ENTRY_TABLE, args, ("message = ? AND username = ?"),
				new String[] { message, user });
	}

	public void updateActiveS(String id, boolean active) {
		ContentValues args = new ContentValues();
		args.put(KEY_ACTIVE, Boolean.toString(active));
		this.db.update(S_ENTRY_TABLE, args, ("my_id = ?"), new String[] { id });
	}

	public boolean isLoggedInT() {
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM TWITTER_USERS", null);
		if (cur != null) {
			cur.moveToFirst();
			if (cur.getInt(0) == 0) {
				cur.close();
				return false;
			} else {
				cur.close();
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean isLoggedInE() {
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM EMAIL_USERS", null);
		if (cur != null) {
			cur.moveToFirst();
			if (cur.getInt(0) == 0) {
				cur.close();
				return false;
			} else {
				cur.close();
				return true;
			}
		} else {
			return false;
		}
	}

	public void deleteTable(String table) {
		db.delete(table, null, null);
	}
}