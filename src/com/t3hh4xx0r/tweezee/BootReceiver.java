package com.t3hh4xx0r.tweezee;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		
		setupTweets(ctx);
		setupSMS(ctx);

	}
			
	private void setupSMS(Context ctx) {
		DBAdapter db = new DBAdapter(ctx);
	    db.open();
	    Cursor c = db.getAllSEntries();
	    try {
	    	while (c.moveToNext()) {
	       		String recipient = null;
	       		String day = null;
	       		String wait = null;
	       		String message = null;
	       		String time = null;
	       		if (c.getString(c.getColumnIndex("start_boot")).equals("true")) {	       			
	       			if (c.getString(c.getColumnIndex("send_time")).length()>2) {
	       				message = c.getString(c.getColumnIndex("message"));
	       				day = c.getString(c.getColumnIndex("send_day"));
	       				recipient = c.getString(c.getColumnIndex("send_to"));
	       				time = c.getString(c.getColumnIndex("send_time"));
	       				setupTimedSMS(ctx, message, day, recipient, time, getSID(recipient, message, ctx));
		       			Log.d("TWEEZEE", "Starting "+c.getString(c.getColumnIndex("message")));
	       			} else {
	       				message = c.getString(c.getColumnIndex("message"));
	       				day = c.getString(c.getColumnIndex("send_day"));
	       				recipient = c.getString(c.getColumnIndex("send_to"));
	       				wait = c.getString(c.getColumnIndex("send_wait"));
	       				setupIntervalSMS(ctx, message, wait, day, recipient,getTID(recipient, message, ctx));
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

	private int getSID(String recipient, String message, Context ctx) {
		int id = 420;
    	final DBAdapter db = new DBAdapter(ctx);
    	db.open();
        Cursor cu = db.getAllSEntries();
	    try {
	       	while (cu.moveToNext()) {
	        	if ((cu.getString(cu.getColumnIndex("message")).equals(message)) && cu.getString(cu.getColumnIndex("send_to")).equals(recipient)) {
	        		id = Integer.parseInt(cu.getString(cu.getColumnIndex("my_id")));
	        		break;
	        	}
	       	}
        } catch (Exception e) {}
	    cu.close();
	    db.close();
	    return id;
	}

	private void setupTweets(Context ctx) {
		DBAdapter db = new DBAdapter(ctx);
	    db.open();
	    Cursor c = db.getAllTEntries();
	    try {
	    	while (c.moveToNext()) {
	       		String username = null;
	       		String day = null;
	       		String wait = null;
	       		String message = null;
	       		String time = null;
	       		String mentions = null;
	       		if (c.getString(c.getColumnIndex("start_boot")).equals("true")) {	       			
	       			if (c.getString(c.getColumnIndex("send_time")).length()>2) {
	       				username = c.getString(c.getColumnIndex("username"));
	       				message = c.getString(c.getColumnIndex("message"));
	       				day = c.getString(c.getColumnIndex("send_day"));
	       				mentions = c.getString(c.getColumnIndex("mentions"));
	       				time = c.getString(c.getColumnIndex("send_time"));
	       				
	       				setupTimedTweet(ctx, username, message, day, mentions, time, getTID(username, message, ctx));
		       			Log.d("TWEEZEE", "Starting "+c.getString(c.getColumnIndex("message")));
	       			} else {
	       				username = c.getString(c.getColumnIndex("username"));
	       				message = c.getString(c.getColumnIndex("message"));
	       				day = c.getString(c.getColumnIndex("send_day"));
	       				mentions = c.getString(c.getColumnIndex("mentions"));
	       				wait = c.getString(c.getColumnIndex("send_wait"));
	       					
	       				setupIntervalTweet(ctx, username, message, day, mentions, wait, getTID(username, message, ctx));
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

	private int getTID(String user, String message, Context ctx) {
		int id = 420;
    	final DBAdapter db = new DBAdapter(ctx);
    	db.open();
        Cursor cu = db.getAllSEntries();
	    try {
	       	while (cu.moveToNext()) {
	        	if ((cu.getString(cu.getColumnIndex("message")).equals(message)) && cu.getString(cu.getColumnIndex("username")).equals(user)) {
	        		id = Integer.parseInt(cu.getString(cu.getColumnIndex("my_id")));
	        		break;
	        	}
	       	}
        } catch (Exception e) {}
	    cu.close();
	    db.close();
	    return id;
	}
	
	private void setupTimedTweet(Context c, String username, String message, String day, String mentions, String timeValue, int id) {
        Intent myIntent = new Intent(c, TweezeeReceiver.class);
    	myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("mentions", mentions);
    	myIntent.putExtra("day", day); 
        myIntent.setAction(Integer.toString(id));
        myIntent.setData(Uri.parse(Integer.toString(id)));  
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeValue.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeValue.split(":")[1]));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);					
	}
	
	private void setupIntervalTweet(Context c, String username, String message, String wait, String day, String mentions, int id) {
        Intent myIntent = new Intent(c, TweezeeReceiver.class);
	    myIntent.putExtra("username", username);
	    myIntent.putExtra("message", message);
	    myIntent.putExtra("mentions", mentions);
	    myIntent.putExtra("day", day); 
        myIntent.setAction(Integer.toString(id));
        myIntent.setData(Uri.parse(Integer.toString(id)));  
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Integer.parseInt(wait)*60000, pendingIntent);					
	}	

	private void setupIntervalSMS(Context c, String message, String wait, String day, String recipient, int id) {
    	Toast.makeText(c, "New sms saved, "+message, Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(c, TweezeeReceiver.class);
    	myIntent.putExtra("type", "sms");
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("day", day);
    	myIntent.putExtra("recipient", recipient);
        myIntent.setAction(Integer.toString(id));
        myIntent.setData(Uri.parse(Integer.toString(id)));  
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Integer.parseInt(wait)*60000, pendingIntent);					
	}

	private void setupTimedSMS(Context c, String message, String day, String recipient, String timeValue, int id) {
    	Toast.makeText(c, "New sms saved, "+message, Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(c, TweezeeReceiver.class);
        myIntent.setAction(Integer.toString(id));
        myIntent.setData(Uri.parse(Integer.toString(id)));   
    	myIntent.putExtra("type", "sms");
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("recipient", recipient);
    	myIntent.putExtra("day", day);        
    	PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeValue.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeValue.split(":")[1]));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);					
	}	
}