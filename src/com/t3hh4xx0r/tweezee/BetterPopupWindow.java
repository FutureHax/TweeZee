package com.t3hh4xx0r.tweezee;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * This class does most of the work of wrapping the {@link PopupWindow} so it's simpler to use.
 * 
 * @author qberticus
 * 
 */
public class BetterPopupWindow {
	protected final View anchor;
	private final PopupWindow window;
	private View root;
	private Drawable background = null;
	private final WindowManager windowManager;
	String message;
	int place;
	int position;


	/**
	 * Create a BetterPopupWindow
	 * 
	 * @param anchor
	 *            the view that the BetterPopupWindow will be displaying 'from'
	 */
	public BetterPopupWindow(View anchor) {
		this.anchor = anchor;
		this.window = new PopupWindow(anchor.getContext());


		// when a touch even happens outside of the window
		// make the window go away
		this.window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					BetterPopupWindow.this.window.dismiss();
					return true;
				}
				return false;
			}
		});

		this.windowManager = (WindowManager) this.anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
		onCreate();
	}

	/**
	 * Anything you want to have happen when created. Probably should create a view and setup the event listeners on
	 * child views.
	 */
	protected void onCreate() {}

	/**
	 * In case there is stuff to do right before displaying.
	 */
	protected void onShow() {}

	private void preShow() {
		if(this.root == null) {
			throw new IllegalStateException("setContentView was not called with a view to display.");
		}
		onShow();

		if(this.background == null) {
			this.window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			this.window.setBackgroundDrawable(this.background);
		}

		// if using PopupWindow#setBackgroundDrawable this is the only values of the width and hight that make it work
		// otherwise you need to set the background of the root viewgroup
		// and set the popupwindow background to an empty BitmapDrawable
		this.window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		this.window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		this.window.setTouchable(true);
		this.window.setFocusable(true);
		this.window.setOutsideTouchable(true);

		this.window.setContentView(this.root);
	}

	public void setBackgroundDrawable(Drawable background) {
		this.background = background;
	}

	/**
	 * Sets the content view. Probably should be called from {@link onCreate}
	 * 
	 * @param root
	 *            the view the popup will display
	 */
	public void setContentView(View root) {
		this.root = root;
		this.window.setContentView(root);
	}

	/**
	 * Will inflate and set the view from a resource id
	 * 
	 * @param layoutResID
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator =
				(LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * If you want to do anything when {@link dismiss} is called
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		this.window.setOnDismissListener(listener);
	}

	/**
	 * Displays like a popdown menu from the anchor view
	 */
	public void showLikePopDownMenu() {
		this.showLikePopDownMenu(0, 0);
	}

	/**
	 * Displays like a popdown menu from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in X direction
	 * @param yOffset
	 *            offset in Y direction
	 */
	public void showLikePopDownMenu(int xOffset, int yOffset) {
		this.preShow();

		this.window.setAnimationStyle(R.style.Animations_PopDownMenu);

		this.window.showAsDropDown(this.anchor, xOffset, yOffset);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 */
	public void showLikeQuickAction() {
		this.showLikeQuickAction(0, 0);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in the X direction
	 * @param yOffset
	 *            offset in the Y direction
	 */
	public void showLikeQuickAction(int xOffset, int yOffset) {
		this.preShow();

		this.window.setAnimationStyle(R.style.Animations_GrowFromBottom);

		int[] location = new int[2];
		this.anchor.getLocationOnScreen(location);

		Rect anchorRect =
				new Rect(location[0], location[1], location[0] + this.anchor.getWidth(), location[1]
					+ this.anchor.getHeight());

		this.root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = this.root.getMeasuredWidth();
		int rootHeight = this.root.getMeasuredHeight();

		int screenWidth = this.windowManager.getDefaultDisplay().getWidth();

		int xPos = ((screenWidth - rootWidth) / 2) + xOffset;
		int yPos = anchorRect.top - rootHeight + yOffset;

		// display on bottom
		if(rootHeight > anchorRect.top) {
			yPos = anchorRect.bottom + yOffset;
			this.window.setAnimationStyle(R.style.Animations_GrowFromTop);
		}

		this.window.showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	public void dismiss() {
		this.window.dismiss();
	}
	
	public static class DemoPopupWindow extends BetterPopupWindow implements OnClickListener {
		public DemoPopupWindow(View anchor, String mes, int p, int pos) {
                  super(anchor);
                  message = mes;
                  place = p;
                  position = pos;
        }

        @Override
        protected void onCreate() {
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.anchor.getContext());

                this.anchor.getContext();
				LayoutInflater inflater =
                                  (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                  ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup, null);
                  
                  for(int i = 0, icount = root.getChildCount() ; i < icount ; i++) {
                          View v = root.getChildAt(i);

                          if(v instanceof LinearLayout) {
                          	
                              LinearLayout lL = (LinearLayout) v;
                              for(int j = 0, jcount = lL.getChildCount() ; j < jcount ; j++) {
                              	View item = lL.getChildAt(j);
                              	if (prefs.getBoolean("account", false)) {
                              		if(item.getId() == R.id.send) {
                              			item.setVisibility(View.GONE);                              	
                              		}
                              		if(item.getId() == R.id.stop) {
                              			item.setVisibility(View.GONE);                              	
                              		}                              	}
                              	item.setOnClickListener(this);
                              }
                          }
                  }
                  this.setContentView(root);
        }
        
  		@Override
  		public void onClick(View v) {
  			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.anchor.getContext());

  			if(v.getId() == R.id.delete) {  
              	if (!prefs.getBoolean("account", false)) {
				     String user = MainActivity.users[place].getName();
				     killTweet(user, message, getID(user, message));
				     DBAdapter db = new DBAdapter(this.anchor.getContext());
			       	 db.open();
			       	 String[] m = new String[] {message};
			       	 db.deleteEntry(m);
			       	 db.close();
			       	 this.dismiss();
	       			 UserFragment.entryArray.remove(place);
	       			 Message msg = new Message();
	       			 msg.what = 0;
	       			 UserFragment.handy.sendMessage(msg);
  	    	    } else {
				     DBAdapter db = new DBAdapter(this.anchor.getContext());
			       	 db.open();
			       	 String[] m = new String[] {message};
			       	 db.deleteUser(m);
			         MainActivity.users = ArrayUtils.remove(MainActivity.users, place); 
			       	 db.close();
			       	 this.dismiss();
			         Intent mi = new Intent(v.getContext(), AccountManager.class);
			         mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			         Bundle b = new Bundle();
			         b.putInt("pos", place);
			         mi.putExtras(b);
			         this.anchor.getContext().startActivity(mi);			         
  	    	    }
	        } 
  	    	
  	    	if(v.getId() == R.id.send) { 
			     String user = MainActivity.users[place].getName();
			     String time = "";
			     String mentions = "";
			     String interval = "";
			     String days = "";
			     String id = "";
			     
			     DBAdapter db = new DBAdapter(this.anchor.getContext());
			     db.open();
			     Cursor c = db.getAllEntries();
		 	       	try {
			       		while (c.moveToNext()) {
			       			if (c.getString(c.getColumnIndex("username")).equals(user) && c.getString(c.getColumnIndex("message")).equals(message)) {
			   		       	  time = c.getString(c.getColumnIndex("send_time"));
					       	  mentions = c.getString(c.getColumnIndex("mentions"));
					       	  interval = c.getString(c.getColumnIndex("send_wait"));
					       	  days = c.getString(c.getColumnIndex("send_day"));
					       	  id = c.getString(c.getColumnIndex("my_id"));			       			}
			       		}
			       	} catch (Exception e1) {
			       		e1.printStackTrace();
			       	}
			     c.close();
			     db.close();		       	 
		       	 
		       	 if (time.length() < 2) {
		       		 setupIntervalTweet(this.anchor.getContext(), user, message, interval, days, mentions, id);
		       	 } else {
		       		 setupTimedTweet(this.anchor.getContext(), user, message, days, mentions, time, id);
		       	 }
		       	 this.dismiss();
	        }
  	    	
 	    	if(v.getId() == R.id.stop) {   	    		
			     String user = MainActivity.users[place].getName();
			     killTweet(user, message, getID(user, message));
		       	 this.dismiss();
 	    	}
 	    }

		private String getID(String user, String message) {
			String id = "";
		    DBAdapter db = new DBAdapter(this.anchor.getContext());
		    db.open();
		    Cursor c = db.getAllEntries();
 	       	try {
	       		while (c.moveToNext()) {
	       			if (c.getString(c.getColumnIndex("username")).equals(user) && c.getString(c.getColumnIndex("message")).equals(message)) {
	       				id = c.getString(c.getColumnIndex("my_id"));
	       			}
	       		}
	       	} catch (Exception e1) {
	       		e1.printStackTrace();
	       	}
	       	c.close();
	       	db.close();
	       	return id;
		}

		public void killTweet(String user, String message, String id) {
	    	Intent myIntent = new Intent(this.anchor.getContext(), TweezeeReceiver.class);
	        myIntent.setAction(Integer.toString(md5(user+message))+id);
	        myIntent.setData(Uri.parse(Integer.toString(md5(user+message))+id));
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.anchor.getContext(), md5(user+message), myIntent, 0);
	        AlarmManager alarmManager = (AlarmManager)this.anchor.getContext().getSystemService(Context.ALARM_SERVICE);
	        alarmManager.cancel(pendingIntent);		
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
		
		private void setupTimedTweet(Context c, String username, String message, String day, String mentions, String timeValue, String id) {
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
	        calendar.setTimeZone(TimeZone.getDefault());
	        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeValue.split(":")[0]));
	        calendar.set(Calendar.MINUTE, Integer.parseInt(timeValue.split(":")[1]));
	        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);					
		}
		
		private void setupIntervalTweet(Context c, String username, String message, String wait, String day, String mentions, String id) {
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
	}
}
