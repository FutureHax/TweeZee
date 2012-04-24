package com.t3hh4xx0r.tweezee;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TweezeeService extends Service {
	RunnableTweets[] tweet;
	
	@Override
	public IBinder onBind(Intent arg0) {
	    return null;
	}
	
	public void onCreate(){
	    super.onCreate();
	    startService();
	}
	
	public void onDestroyed(){
	    super.onDestroy();
	    Toast.makeText(getBaseContext(), "SERVICE STOPPED", 99999).show();
	    try {
	    	startService();
	    } catch (Exception e) {}
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

	public void startService() {
		Log.d("SERVICE", "STARTED LIKE A BOSS");
		getTweets();
		runTweets();
	}

	private void runTweets() {
		try {
			ExecutorService service = Executors.newFixedThreadPool(tweet.length); 
	        for (int i=0;i<tweet.length;i++) {
	        	service.execute(tweet[i]);
	        }
	        service.shutdown();
		} catch (Exception e) {}
	}

	private void getTweets() {
		int i = 0;
	    DBAdapter db = new DBAdapter(this);
	   	db.open();
	   	Cursor c = db.getAllEntries();
	   	tweet = new RunnableTweets[c.getCount()];

   		try {
   			while (c.moveToNext()) {
   				i++;
   	       		tweet[i-1] = new RunnableTweets(c.getString(c.getColumnIndex("message")));   			
   	       	}
   		} catch (Exception e) {}
   		c.close();
   		db.close();   			
	}
	
}