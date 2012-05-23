package com.t3hh4xx0r.tweezee;

import java.util.Calendar;
import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.t3hh4xx0r.tweezee.sms.SMSActivity;
import com.t3hh4xx0r.tweezee.twitter.OAUTH;
import com.t3hh4xx0r.tweezee.twitter.TwitterActivity;

public class TweezeeReceiver extends BroadcastReceiver {

	private String message; 
	private String mentions; 
    private String username; 
    private String day; 
    private String token; 
    private String secret; 
    private String tokenE;
    private String type;
    private String recipient;
    Twitter t;
    
    SharedPreferences prefs;
    
    Context ctx;
    
    Handler mHandler;
    
    AccessToken aToken;

	@Override
	public void onReceive(Context c, Intent i) {
		type = i.getStringExtra("type");
		prefs = PreferenceManager.getDefaultSharedPreferences(c);
		ctx = c;
		mHandler = new Handler();

		message = i.getStringExtra("message");
		day = i.getStringExtra("day");
		
		if (type.equals("tweet")) {
			username = i.getStringExtra("username");
			mentions = i.getStringExtra("mentions");
			sendTweet();
		} else if (type.equals("sms")) {
			recipient = i.getStringExtra("recipient");
			sendSMS();
		}

		
	}

    private void sendSMS() {
   	 try {
	     if (day.split(",")[getcDay()-1].equals("true")) {
		     Intent i = new Intent(ctx, TweezeeReceiver.class);
             i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);  
		     SmsManager sms = SmsManager.getDefault();
		     sms.sendTextMessage(recipient, null, message, pi, null);			        			       			          
	       	 if (prefs.getBoolean("notify", true)) {
    			 if (!prefs.getBoolean("notifyIntrusive", true)) {
    				 mHandler.post(new Runnable() {
    					 @Override
    					 public void run() {
    						 Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    					 }
    				 });
    			 } else{
	    			 alertS(message, ctx);
    			 }
	       	 }
	     }
	 } catch (Exception e) {
		 e.printStackTrace();
	 }
	}

	private void sendTweet () {	    	 
	     t = new TwitterFactory().getInstance();
	     t.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
	     if (getToken() == null) {
	    	 Toast.makeText(ctx, "Error while sending tweet, "+message+"\n"+tokenE, Toast.LENGTH_LONG).show();
	     } else {
	    	 aToken = getToken();
	     }
	     t.setOAuthAccessToken(aToken);
		    	 try {
				     if (day.split(",")[getcDay()-1].equals("true")) {
				    	 if (prefs.getBoolean("direct", false)) {
				    		 t.updateStatus(mentions+" "+message+" "+mentions+" "+getRandom());
				    	 } else {
				    		 t.updateStatus(getRandom()+" "+message+" "+mentions);					    		 
				    	 }
				       	 if (prefs.getBoolean("notify", true)) {
			    			 if (!prefs.getBoolean("notifyIntrusive", true)) {
			    				 mHandler.post(new Runnable() {
			    					 @Override
			    					 public void run() {
			    						 Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
			    					 }
			    				 });
			    			 } else{
				    			 alertT(message, ctx);
			    			 }
				       	 }
				     }
		    	 } catch (Exception e) {
		    		 e.printStackTrace();
		    	 }
   }
    
	private AccessToken getToken() {
		 DBAdapter dba = new DBAdapter(ctx);
      	 dba.open();
      	 Cursor cu = dba.getAllTUsers();
  		 try {
  			while (cu.moveToNext()) {
  				if (cu.getString(1).equals(username)) {
  					token = cu.getString(3);
  					secret = cu.getString(4);
  				}
  			}
  		 } catch (Exception e) {
  			 e.printStackTrace();
  		 }
  		 cu.close();
  		 dba.close();
  		 
  		 try {
  			 return new AccessToken(token, secret);
  		 } catch (Exception e) {
  			 tokenE = e.toString();
  			 return null; 
  		 }
	}

	private int getcDay() {
	    Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	private int getRandom() {
		return new Random().nextInt(98 - 0 + 1) + 0;
	}

	private void alertT(String message, Context c) {
			 int icon = R.drawable.ic_launcher;
			 CharSequence tickerText = "Status Update!";
			 long when = System.currentTimeMillis();
			 CharSequence contentTitle = "TweeZee updated your status."; 
			 CharSequence contentText = message; 
			 
			 Intent notificationIntent = new Intent(c, TwitterActivity.class);

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

	private void alertS(String message, Context c) {
		 int icon = R.drawable.ic_launcher;
		 CharSequence tickerText = "SMS Sent!";
		 long when = System.currentTimeMillis();
		 CharSequence contentTitle = "TweeZee sent an SMS."; 
		 CharSequence contentText = message; 
		 
		 Intent notificationIntent = new Intent(c, SMSActivity.class);

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
