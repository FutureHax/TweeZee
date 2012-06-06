package com.t3hh4xx0r.tweezee;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

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
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.t3hh4xx0r.tweezee.email.EmailActivity;
import com.t3hh4xx0r.tweezee.email.GmailSender;
import com.t3hh4xx0r.tweezee.facebook.FacebookActivity;
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
			pass = Encryption.decryptString(i.getStringExtra("pass"), Encryption.KEY);
			subject = i.getStringExtra("subject");
			sendEmail();
		} else if (type.equals("facebook")) {
			postFacebook();
		}

		
	}

    private void postFacebook() {
     	 if (day.split(",")[getcDay()-1].equals("true") || date) {
     		Thread thread2 = new Thread() {
    		    @Override
    		    public void run() {
    		    	 try {
    		    		 FacebookActivity.facebookConnector.postMessageOnWall(message);
    		    	     logSend("Facebook", getFormattedTime(), message, "--", true);
    		    	 } catch (Exception e) {
    					 e.printStackTrace();
    		    	     sendError = true;
    		    	 }
    		    }
    		};
    		thread2.start();
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
    					alertF(message, ctx);
    				}	
	       	 	}
    		} else {
	    	     logSend("Facebook", getFormattedTime(), message, "--", false);
				 mHandler.post(new Runnable() {
					 @Override
					 public void run() {
						 Toast.makeText(ctx, "Error while sending, "+message, Toast.LENGTH_LONG).show();
					 }
				 });
		     } 		        
    		}
	}

	protected String getFormattedTime() {
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTimeInMillis(System.currentTimeMillis());
	      calendar.setTimeZone(TimeZone.getDefault());
	      SimpleDateFormat d = new SimpleDateFormat("hh:mm");
	      return d.format(calendar.getTime());
	}

	private void sendEmail() {
      	 if (day.split(",")[getcDay()-1].equals("true") || date) {
		     GmailSender sender = new GmailSender(username, pass);
		     try {
				sender.sendMail(subject,   
					         message,   
					         username,   
					         recipient);
	    	     logSend("Email", getFormattedTime(), message, recipient, true);
			} catch (AddressException e) {
				e.printStackTrace();
				sendError = true;
			} catch (AuthenticationFailedException e) {
				e.printStackTrace();
				sendError = true;
			} catch (MessagingException e) {
				e.printStackTrace();
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
	    	     logSend("Email", getFormattedTime(), message, recipient, false);
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
     if (day.split(",")[getcDay()-1].equals("true") || date) {
	     Intent i = new Intent(ctx, TweezeeReceiver.class);
         i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	     PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);  
	     SmsManager sms = SmsManager.getDefault();
	     try {
    	     logSend("SMS", getFormattedTime(), message, recipient, true);
	    	 sms.sendTextMessage(recipient.replaceAll("-", ""), null, message, pi, null);			        			       			          
	     } catch (Exception e) {
	    	 sendError = true;
			 e.printStackTrace();
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
	    			 alertS(message, ctx);
	   			 }
	      	}
	     } else {
    	     logSend("SMS", getFormattedTime(), message, recipient, false);
			 mHandler.post(new Runnable() {
				 @Override
				 public void run() {
					 Toast.makeText(ctx, "Error while sending, "+message, Toast.LENGTH_LONG).show();
				 }
			 });
	     }
      }
	}
     
	private void sendTweet () {	    	 
	     t = new TwitterFactory().getInstance();
	     t.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
	     if (getToken() == null) {
	    	 sendError = true;
	     } else {
	    	 aToken = getToken();
	     }
	     t.setOAuthAccessToken(aToken);
		 if (day.split(",")[getcDay()-1].equals("true") || date) {
			 if (prefs.getBoolean("direct", false)) {
				 try {
					 t.updateStatus(mentions+" "+message+" "+mentions+" "+getRandom());
					 if (mentions.length() < 2 || mentions == null) {
						 logSend("Twitter", getFormattedTime(), message, "--", true);
					 } else{
						 logSend("Twitter", getFormattedTime(), message, mentions, true);						 
					 }
				 } catch (Exception e) {
					 sendError = true;
 					 e.printStackTrace();
				 }
			 } else {	
		    	try {
		    		t.updateStatus(getRandom()+" "+message+" "+mentions);					    		 
					 if (mentions.length() < 2 || mentions == null) {
						 logSend("Twitter", getFormattedTime(), message, "--", true);
					 } else{
						 logSend("Twitter", getFormattedTime(), message, mentions, true);						 
					 }
		    	} catch (Exception e) {
		    		sendError = true;
					e.printStackTrace();
		    	}
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
		    			 alertT(message, ctx);
	    			 }
		       	 }
		     } else {
				 if (mentions.length() < 2 || mentions == null) {
					 logSend("Twitter", getFormattedTime(), message, "--", false);
				 } else{
					 logSend("Twitter", getFormattedTime(), message, mentions, false);						 
				 }				 mHandler.post(new Runnable() {
					 @Override
					 public void run() {
						 Toast.makeText(ctx, "Error while sending, "+message, Toast.LENGTH_LONG).show();
					 }
				 });
		     }
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
			 CharSequence contentTitle = "UltimateScheduler updated your Twitter status."; 
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

	private void alertF(String message, Context c) {
		 int icon = R.drawable.ic_launcher;
		 CharSequence tickerText = "Facebook Status Updated!";
		 long when = System.currentTimeMillis();
		 CharSequence contentTitle = "UltimateScheduler posted to your wall."; 
		 CharSequence contentText = message; 
		 
		 Intent notificationIntent = new Intent(c, FacebookActivity.class);

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
	
	private void logSend(final String type, final String time, String message, final String to, final boolean success) {
		 if (type.equals("Email")) {
			 message = new String(message.replace("\n\n\nSent via UltimateScheduler", ""));
		 }
		 FileWriter fW = null;
		 BufferedWriter bW = null;
		 try {
			fW = new FileWriter(Environment.getExternalStorageDirectory()+"/t3hh4xx0r/ultimate_scheduler/log.txt", true);
		 } catch (IOException e) {
			e.printStackTrace();
		 }
		 bW = new BufferedWriter(fW);
		 try {
			bW.append("///"+type + "//"+time+"//"+message+"//"+to+"//"+Boolean.toString(success));
			bW.newLine();
			bW.newLine();
			bW.flush();
			bW.close();
		 } catch (IOException e) {
			e.printStackTrace();
		 }			
	}
}
