package com.t3hh4xx0r.tweezee;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class RunnableTweets implements Runnable {

    private String message; 
    private String username; 
    private String amount; 
    private String wait; 
    private String day; 
    private String mentions; 
    private String token; 
    private String secret; 
    
    Context c;
    
    SharedPreferences prefs;

	    public RunnableTweets(Context c, String username, String message, String amount, String wait, String day, String mentions){ 
	        this.username = username; 
	        this.message = message; 
	        this.amount = amount; 
	        this.wait = wait; 
	        this.day = day; 
	        this.mentions = mentions; 
	        this.c = c;
			prefs = PreferenceManager.getDefaultSharedPreferences(c);
	    }

	    public void run() {	    	 
	    	 getUser();
		     Twitter t = new TwitterFactory().getInstance();
		     AccessToken tToken = new AccessToken(token, secret);
		     t.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
		     t.setOAuthAccessToken(tToken);
		     Calendar calendar = Calendar.getInstance();
		     int cDay = calendar.get(Calendar.DAY_OF_WEEK); 
		    	 do {
			    	 try {
					     if (day.split(",")[cDay-1].equals("true")) {
					    	 t.updateStatus(getRandom()+" "+message+" "+mentions);
					    	 DBAdapter db = new DBAdapter(c);
					    	 db.open();
					    	 int newAmount = Integer.parseInt(amount)-1;
					    	 amount = Integer.toString(newAmount);
					       	 if (newAmount > 0) {
					       		 db.updateEntry(username, message, mentions, message, wait, Integer.toString(newAmount), day);
					       	 } else {
					       		 db.deleteEntry(new String [] {message});
						       	 db.close();
					       		 break;
					       	 }
					       	 db.close();
					       	 if (prefs.getBoolean("notify", true)) {
				    			 alert(message, c);
				    		 }
					     }
			    	 } catch (TwitterException e) {
			    		 e.printStackTrace();
			    	 }
			 	    try {
			 	    	TimeUnit.MINUTES.sleep(Integer.parseInt(wait));
			 	    } catch (InterruptedException e) {
				        e.printStackTrace();
				    }
		    	 } while (Integer.parseInt(amount) > 0);
	    }

		private int getRandom() {
			return new Random().nextInt(98 - 0 + 1) + 0;
		}

		private void getUser() {
		     DBAdapter db = new DBAdapter(c);
	       	 db.open();
	       	 Cursor cu = db.getAllUsers();
	   		 try {
	   			while (cu.moveToNext()) {
	   				if (cu.getString(1).equals(username)) {
	   					token = cu.getString(3);
	   					secret = cu.getString(4);
	   				}
	   			}
	   		 } catch (Exception e) {}
	   		 cu.close();
	   		 db.close();   						
		}

		private void alert(String message, Context c) {
				 int icon = R.drawable.ic_launcher;
				 CharSequence tickerText = "Status Update!";
				 long when = System.currentTimeMillis();
				 CharSequence contentTitle = "TweeZee updated your status."; 
				 CharSequence contentText = message; 
				 
				 Intent notificationIntent = new Intent(c, MainActivity.class);

				 PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, 0);

				 Notification notification = new Notification(icon, tickerText, when);
		   	     notification.defaults = Notification.DEFAULT_VIBRATE;
		   	     notification.flags = Notification.FLAG_AUTO_CANCEL;
				 notification.setLatestEventInfo(c, contentTitle, contentText, contentIntent);
				 final int HELLO_ID = 1;

				 NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(
			                Context.NOTIFICATION_SERVICE);	
				 mNotificationManager.notify(HELLO_ID, notification);			
		} 
	}