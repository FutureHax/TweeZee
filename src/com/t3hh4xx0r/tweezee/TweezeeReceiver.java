package com.t3hh4xx0r.tweezee;

import java.util.Calendar;
import java.util.Random;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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
import android.util.Log;
import android.widget.Toast;

import com.t3hh4xx0r.tweezee.email.EmailActivity;
import com.t3hh4xx0r.tweezee.email.GmailSender;
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
    private String pass;
    private String subject;
    private boolean date;
    private boolean sendError = false;
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
		date = i.getBooleanExtra("dated", false);

		Log.d("ULTIMATE SCHEDULER", "RECEIVED "+type+":"+message+". "+Boolean.toString(date)+ " : "+day);
		if (type.equals("tweet")) {
			username = i.getStringExtra("username");
			mentions = i.getStringExtra("mentions").replaceAll(",", "");
			sendTweet();
		} else if (type.equals("sms")) {
			if (!Character.isDigit(i.getStringExtra("recipient").charAt(0))) {
				recipient = i.getStringExtra("recipient").split("-", 2)[1];
			} else {
				recipient = i.getStringExtra("recipient");
			}
			sendSMS();
		} else if (type.equals("email")) {
			recipient = i.getStringExtra("recipient");
			username = Encryption.decryptString(i.getStringExtra("username"), Encryption.KEY);
			pass = i.getStringExtra("pass");
			subject = i.getStringExtra("subject");
			sendEmail();
		}

		
	}

    private void sendEmail() {
      	 if (day.split(",")[getcDay()-1].equals("true") || date) {
		     GmailSender sender = new GmailSender(username, pass);
		     try {
				sender.sendMail(subject,   
					         message,   
					         username,   
					         recipient);
			} catch (AddressException e) {
				sendError = true;
			} catch (AuthenticationFailedException e) {
				sendError = true;
			} catch (MessagingException e) {
				sendError = true;
			}  
		     if (!sendError) {
		       	 if (prefs.getBoolean("notify", true)) {
					 if (!prefs.getBoolean("notifyIntrusive", true)) {
						 mHandler.post(new Runnable() {
							 @Override
							 public void run() {
								 Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
							 }
						 });
					 } else{
		    			 alertE(message, ctx);
					 }
		       	 }        	    	 
		     } else {
				 mHandler.post(new Runnable() {
					 @Override
					 public void run() {
						 Toast.makeText(ctx, "Error while sending, "+message, Toast.LENGTH_LONG).show();
					 }
				 });
		     } 		        			       			          
		 }
	}

	private void sendSMS() {
   	 try {
	     if (day.split(",")[getcDay()-1].equals("true") || date) {
		     Intent i = new Intent(ctx, TweezeeReceiver.class);
             i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);  
		     SmsManager sms = SmsManager.getDefault();
		     sms.sendTextMessage(recipient.replaceAll("-", ""), null, message, pi, null);			        			       			          
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
				     if (day.split(",")[getcDay()-1].equals("true") || date) {
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
			 CharSequence contentTitle = "UltimateScheduler updated your status."; 
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
		 CharSequence contentTitle = "UltimateScheduler sent an SMS."; 
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

	private void alertE(String message, Context c) {
		 int icon = R.drawable.ic_launcher;
		 CharSequence tickerText = "Email Sent!";
		 long when = System.currentTimeMillis();
		 CharSequence contentTitle = "UltimateScheduler sent an Email."; 
		 CharSequence contentText = message; 
		 
		 Intent notificationIntent = new Intent(c, EmailActivity.class);

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
