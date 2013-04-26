package com.t3hh4xx0r.tweezee.sms;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
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

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.TweezeeReceiver;

/**
 * This class does most of the work of wrapping the {@link PopupWindow} so it's
 * simpler to use.
 * 
 * @author qberticus
 * 
 */
public class BetterPopupWindowS {
	protected final View anchor;
	private final PopupWindow window;
	private View root;
	private Drawable background = null;
	private final WindowManager windowManager;
	String message;
	String recipient;
	int position;

	/**
	 * Create a BetterPopupWindow
	 * 
	 * @param anchor
	 *            the view that the BetterPopupWindow will be displaying 'from'
	 */
	public BetterPopupWindowS(View anchor) {
		this.anchor = anchor;
		this.window = new PopupWindow(anchor.getContext());

		// when a touch even happens outside of the window
		// make the window go away
		this.window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					BetterPopupWindowS.this.window.dismiss();
					return true;
				}
				return false;
			}
		});

		this.windowManager = (WindowManager) this.anchor.getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		onCreate();
	}

	/**
	 * Anything you want to have happen when created. Probably should create a
	 * view and setup the event listeners on child views.
	 */
	protected void onCreate() {
	}

	/**
	 * In case there is stuff to do right before displaying.
	 */
	protected void onShow() {
	}

	private void preShow() {
		if (this.root == null) {
			throw new IllegalStateException(
					"setContentView was not called with a view to display.");
		}
		onShow();

		if (this.background == null) {
			this.window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			this.window.setBackgroundDrawable(this.background);
		}

		// if using PopupWindow#setBackgroundDrawable this is the only values of
		// the width and hight that make it work
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
		LayoutInflater inflator = (LayoutInflater) this.anchor.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ this.anchor.getWidth(), location[1] + this.anchor.getHeight());

		this.root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = this.root.getMeasuredWidth();
		int rootHeight = this.root.getMeasuredHeight();

		int screenWidth = this.windowManager.getDefaultDisplay().getWidth();

		int xPos = ((screenWidth - rootWidth) / 2) + xOffset;
		int yPos = anchorRect.top - rootHeight + yOffset;

		// display on bottom
		if (rootHeight > anchorRect.top) {
			yPos = anchorRect.bottom + yOffset;
			this.window.setAnimationStyle(R.style.Animations_GrowFromTop);
		}

		this.window.showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	public void dismiss() {
		this.window.dismiss();
	}

	public static class DemoPopupWindow extends BetterPopupWindowS implements
			OnClickListener {
		public DemoPopupWindow(View anchor, String mes, String recip, int pos) {
			super(anchor);
			recipient = recip;
			message = mes;
			position = pos;
		}

		@Override
		protected void onCreate() {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this.anchor.getContext());

			this.anchor.getContext();
			LayoutInflater inflater = (LayoutInflater) this.anchor.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup, null);

			for (int i = 0, icount = root.getChildCount(); i < icount; i++) {
				View v = root.getChildAt(i);

				if (v instanceof LinearLayout) {

					LinearLayout lL = (LinearLayout) v;
					for (int j = 0, jcount = lL.getChildCount(); j < jcount; j++) {
						View item = lL.getChildAt(j);
						if (prefs.getBoolean("account", false)) {
							if (item.getId() == R.id.send) {
								item.setVisibility(View.GONE);
							}
							if (item.getId() == R.id.stop) {
								item.setVisibility(View.GONE);
							}
						}
						item.setOnClickListener(this);
					}
				}
			}
			this.setContentView(root);
		}

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.delete) {
				killSMS(getID(recipient, message));
				DBAdapter db = new DBAdapter(v.getContext());
				db.open();
				db.deleteSEntry(message, recipient);
				db.close();
				this.dismiss();
			}

			if (v.getId() == R.id.send) {
				String time = "";
				String interval = "";
				String days = "";
				String id = "";
				String date = "";

				DBAdapter db = new DBAdapter(this.anchor.getContext());
				db.open();
				Cursor c = db.getAllSEntries();
				try {
					c.moveToPosition(position);
					time = c.getString(c.getColumnIndex("send_time"));
					interval = c.getString(c.getColumnIndex("send_wait"));
					days = c.getString(c.getColumnIndex("send_day"));
					date = c.getString(c.getColumnIndex("send_date"));
					id = c.getString(c.getColumnIndex("my_id"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				c.close();
				db.updateActiveS(id, true);
				db.close();

				if (time.length() < 2) {
					setupIntervalSMS(this.anchor.getContext(), message,
							interval, days, recipient,
							getID(recipient, message), date);
				} else {
					setupTimedSMS(this.anchor.getContext(), message, days,
							recipient, time, getID(recipient, message), date);
				}
				this.dismiss();
			}

			if (v.getId() == R.id.stop) {
				killSMS(getID(recipient, message));
				DBAdapter db = new DBAdapter(this.anchor.getContext());
				db.open();
				db.updateActiveS(Integer.toString(getID(recipient, message)),
						false);
				db.close();
				this.dismiss();
			}

			Intent mi = new Intent(v.getContext(), SMSActivity.class);
			mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.anchor.getContext().startActivity(mi);
		}

		private int getID(String recipient, String message) {
			int id = 420;
			final DBAdapter db = new DBAdapter(this.anchor.getContext());
			db.open();
			Cursor cu = db.getAllSEntries();
			try {
				while (cu.moveToNext()) {
					if ((cu.getString(cu.getColumnIndex("message"))
							.equals(message))
							&& cu.getString(cu.getColumnIndex("send_to"))
									.equals(recipient)) {
						id = Integer.parseInt(cu.getString(cu
								.getColumnIndex("my_id")));
						break;
					}
				}
			} catch (Exception e) {
			}
			cu.close();
			db.close();
			return id;
		}

		public void killSMS(int id) {
			Intent myIntent = new Intent(this.anchor.getContext(),
					TweezeeReceiver.class);
			myIntent.setAction(Integer.toString(id));
			myIntent.setData(Uri.parse(Integer.toString(id)));
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					this.anchor.getContext(), id, myIntent, 0);
			AlarmManager alarmManager = (AlarmManager) this.anchor.getContext()
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);
		}

		private void setupIntervalSMS(Context c, String message, String wait,
				String day, String recipient, int id, String date) {
			Toast.makeText(c, "New sms saved, " + message, Toast.LENGTH_LONG)
					.show();
			Intent myIntent = new Intent(c, TweezeeReceiver.class);
			myIntent.putExtra("type", "sms");
			myIntent.putExtra("message", message);
			myIntent.putExtra("day", day);
			myIntent.putExtra("recipient", recipient);
			if (date.length() > 4) {
				myIntent.putExtra("dated", true);
			}
			myIntent.setAction(Integer.toString(id));
			myIntent.setData(Uri.parse(Integer.toString(id)));
			PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id,
					myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) c
					.getSystemService(Context.ALARM_SERVICE);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			if (date.length() > 4) {
				calendar.setTimeZone(TimeZone.getDefault());
				calendar.set(Calendar.MONTH,
						Integer.parseInt(date.split("-")[0]));
				calendar.set(Calendar.DAY_OF_MONTH,
						Integer.parseInt(date.split("-")[1]));
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), Integer.parseInt(wait) * 60000,
					pendingIntent);
		}

		private void setupTimedSMS(Context c, String message, String day,
				String recipient, String timeValue, int id, String date) {
			Toast.makeText(c, "New sms saved, " + message, Toast.LENGTH_LONG)
					.show();
			Intent myIntent = new Intent(c, TweezeeReceiver.class);
			myIntent.setAction(Integer.toString(id));
			myIntent.setData(Uri.parse(Integer.toString(id)));
			myIntent.putExtra("type", "sms");
			myIntent.putExtra("message", message);
			myIntent.putExtra("recipient", recipient);
			myIntent.putExtra("day", day);
			if (date.length() > 4) {
				myIntent.putExtra("dated", true);
			}
			PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id,
					myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) c
					.getSystemService(Context.ALARM_SERVICE);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.setTimeZone(TimeZone.getDefault());
			calendar.set(Calendar.HOUR_OF_DAY,
					Integer.parseInt(timeValue.split(":")[0]));
			calendar.set(Calendar.MINUTE,
					Integer.parseInt(timeValue.split(":")[1]));
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			if (date.length() > 4) {
				calendar.setTimeZone(TimeZone.getDefault());
				calendar.set(Calendar.MONTH,
						Integer.parseInt(date.split("-")[0]));
				calendar.set(Calendar.DAY_OF_MONTH,
						Integer.parseInt(date.split("-")[1]));
			}
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
					pendingIntent);
		}
	}
}
