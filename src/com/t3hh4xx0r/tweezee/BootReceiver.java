package com.t3hh4xx0r.tweezee;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

		if (!prefs.getBoolean("isReg", false)) {
			Log.d("TWEEZEE", "Boot Recieved but not premium version.");
		} else {
		     DBAdapter db = new DBAdapter(ctx);
	       	 db.open();
	       	 Cursor c = db.getAllEntries();
	       	 try {
	       		while (c.moveToNext()) {
	       			String username = null;
	       			String day = null;
	       			String wait = null;
	       			String message = null;
	       			String time = null;
	       			String id = null;
	       			String mentions = null;
	       			if (c.getString(c.getColumnIndex("start_boot")).equals("true")) {	       			
	       				if (c.getString(c.getColumnIndex("send_time")).length()>2) {
	       					username = c.getString(c.getColumnIndex("username"));
	       					message = c.getString(c.getColumnIndex("message"));
	       					day = c.getString(c.getColumnIndex("send_day"));
	       					mentions = c.getString(c.getColumnIndex("mentions"));
	       					time = c.getString(c.getColumnIndex("send_time"));
	       					id = c.getString(c.getColumnIndex("my_id"));
	       					
	       					setupTimedTweet(ctx, username, message, day, mentions, time, id);
		       				Log.d("TWEEZEE", "Starting "+c.getString(c.getColumnIndex("message")));
	       				} else {
	       					username = c.getString(c.getColumnIndex("username"));
	       					message = c.getString(c.getColumnIndex("message"));
	       					day = c.getString(c.getColumnIndex("send_day"));
	       					mentions = c.getString(c.getColumnIndex("mentions"));
	       					wait = c.getString(c.getColumnIndex("send_wait"));
	       					id = c.getString(c.getColumnIndex("my_id"));
	       					
	       					setupIntervalTweet(ctx, username, message, day, mentions, wait, id);
		       				Log.d("TWEEZEE", "Starting "+c.getString(c.getColumnIndex("message")));
	       				}
	       			}
	       		}
	       	 } catch (Exception e) {
	       		 e.printStackTrace();
	       	 }
	       	 c.close();
	       	 db.close();
		}
			
	}

	private void setupTimedTweet(Context c, String username, String message, String day, String mentions, String timeValue, String id) {
    	Toast.makeText(c, "New tweet saved, "+message, Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(c, TweezeeReceiver.class);
	    myIntent.setAction(Integer.toString(md5(username+message))+id);
	    myIntent.setData(Uri.parse(Integer.toString(md5(username+message))+id));
	    myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("mentions", mentions);
    	myIntent.putExtra("day", day);        
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(c, md5(username+message), myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeValue.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeValue.split(":")[1]));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);					
	}
	
	private void setupIntervalTweet(Context c, String username, String message, String day, String mentions, String wait, String id) {
    	Toast.makeText(c, "New tweet saved, "+message, Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(c, TweezeeReceiver.class);
    	myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("mentions", mentions);
    	myIntent.putExtra("day", day);
	    myIntent.setAction(Integer.toString(md5(username+message))+id);
	    myIntent.setData(Uri.parse(Integer.toString(md5(username+message))+id));
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, md5(username+message), myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Integer.parseInt(wait)*60000, pendingIntent);					
	}
	
	private int md5(String s) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes(), 0, s.length());
			String hash = new BigInteger(1,digest.digest()).toString(16);
			return Integer.parseInt(hash);
		} catch (Exception e) {
			return 420;
		}
	}
}