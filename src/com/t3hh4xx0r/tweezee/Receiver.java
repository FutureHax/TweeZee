package com.t3hh4xx0r.tweezee;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {
	Twitter twitter;
	
	@Override
	public void onReceive(Context c, Intent i) {
		if (MentionsActivity.prefs.getBoolean("notifyAvailable", true)) {
			Toast.makeText(c, "Checking API", 9999999).show();
			
	        twitter = new TwitterFactory().getInstance();
	        AccessToken t = new AccessToken(MainActivity.users[0].getToken(), MainActivity.users[0].getSecret());
	        twitter.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
	        twitter.setOAuthAccessToken(t);
	        
			try {
				 twitter.getFriendsIDs(MainActivity.users[0].getName(), -1);
				 Editor e = MentionsActivity.prefs.edit();
				 e.putBoolean("notifyAvailable", false);
				 e.commit();
				 alert(c);		
			} catch (TwitterException e) {}
		}
	}
	
	public void alert(Context ctx) {
		 
		 String ns = Context.NOTIFICATION_SERVICE;
		 NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(ns);

		 int icon = R.drawable.ic_launcher;
		 CharSequence tickerText = "OMG!";
		 long when = System.currentTimeMillis();
		 CharSequence contentTitle = "TweeZee API"; 
		 CharSequence contentText = "Our limit has been refreshed!"; 
		 
		 Intent notificationIntent = new Intent(ctx, MainActivity.class);

		 PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);

		 Notification notification = new Notification(icon, tickerText, when);
		 notification.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent);
  	     notification.defaults = Notification.DEFAULT_VIBRATE;
  	     notification.flags = Notification.FLAG_AUTO_CANCEL;
  	     final int HELLO_ID = 1;

		 mNotificationManager.notify(HELLO_ID, notification);	

	}
}
