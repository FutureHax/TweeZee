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
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {

		setupTweets(ctx);
		setupSMS(ctx);
		setupEmails(ctx);

	}

	private void setupTweets(Context ctx) {
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor c = db.getAllTEntries();
		try {
			while (c.moveToNext()) {
				String username = c.getString(c.getColumnIndex("username"));
				String day = c.getString(c.getColumnIndex("send_day"));
				String wait = c.getString(c.getColumnIndex("send_wait"));
				String message = c.getString(c.getColumnIndex("message"));
				String time = c.getString(c.getColumnIndex("send_time"));
				String mentions = c.getString(c.getColumnIndex("mentions"));
				if (c.getString(c.getColumnIndex("start_boot")).equals("true")) {
					if (c.getString(c.getColumnIndex("send_time")).length() > 2) {
						setupTimedTweet(ctx, username, message, day, mentions,
								time, getTID(username, message, ctx));
					} else {
						setupIntervalTweet(ctx, username, message, day,
								mentions, wait, getTID(username, message, ctx));
					}
					db.updateActiveT(username, message, true);
				} else {
					db.updateActiveT(username, message, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.close();
		db.close();
	}

	private void setupSMS(Context ctx) {
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor c = db.getAllSEntries();
		try {
			while (c.moveToNext()) {
				String recipient = c.getString(c.getColumnIndex("send_to"));
				String day = c.getString(c.getColumnIndex("send_day"));
				String wait = c.getString(c.getColumnIndex("send_wait"));
				String message = c.getString(c.getColumnIndex("message"));
				String time = c.getString(c.getColumnIndex("send_time"));
				if (c.getString(c.getColumnIndex("start_boot")).equals("true")) {
					if (c.getString(c.getColumnIndex("send_time")).length() > 2) {
						setupTimedSMS(ctx, message, day, recipient, time,
								getSID(recipient, message, ctx));
					} else {
						setupIntervalSMS(ctx, message, wait, day, recipient,
								getSID(recipient, message, ctx));
					}
					db.updateActiveS(
							Integer.toString(getSID(recipient, message, ctx)),
							true);
				} else {
					db.updateActiveS(
							Integer.toString(getSID(recipient, message, ctx)),
							false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.close();
		db.close();
	}

	private void setupEmails(Context ctx) {
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor c = db.getAllEEntries();
		try {
			while (c.moveToNext()) {
				String recipient = c.getString(c.getColumnIndex("send_to"));
				String day = c.getString(c.getColumnIndex("send_day"));
				String wait = c.getString(c.getColumnIndex("send_wait"));
				String message = c.getString(c.getColumnIndex("message"));
				String time = c.getString(c.getColumnIndex("send_time"));
				String user = c.getString(c.getColumnIndex("username"));
				String pass = Encryption.decryptString(
						c.getString(c.getColumnIndex("password")),
						Encryption.KEY);
				String subject = c.getString(c.getColumnIndex("subject"));
				if (c.getString(c.getColumnIndex("start_boot")).equals("true")) {
					if (c.getString(c.getColumnIndex("send_time")).length() > 2) {
						setupTimedEmail(ctx, user, pass, subject, message, day,
								recipient, time, getEID(user, message, ctx));
					} else {
						setupIntervalEmail(ctx, user, pass, subject, message,
								wait, day, recipient,
								getEID(user, message, ctx));
					}
					db.updateActiveE(user, message, true);
				} else {
					db.updateActiveE(user, message, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.close();
		db.close();
	}

	private void setupTimedTweet(Context c, String username, String message,
			String day, String mentions, String timeValue, int id) {
		Intent myIntent = new Intent(c, TweezeeReceiver.class);
		myIntent.putExtra("username", username);
		myIntent.putExtra("message", message);
		myIntent.putExtra("mentions", mentions);
		myIntent.putExtra("day", day);
		myIntent.setAction(Integer.toString(id));
		myIntent.setData(Uri.parse(Integer.toString(id)));
		PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id,
				myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(timeValue.split(":")[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(timeValue.split(":")[1]));
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				pendingIntent);
	}

	private void setupIntervalTweet(Context c, String username, String message,
			String wait, String day, String mentions, int id) {
		Intent myIntent = new Intent(c, TweezeeReceiver.class);
		myIntent.putExtra("type", "tweet");
		myIntent.putExtra("username", username);
		myIntent.putExtra("message", message);
		myIntent.putExtra("mentions", mentions);
		myIntent.putExtra("day", day);
		myIntent.setAction(Integer.toString(id));
		myIntent.setData(Uri.parse(Integer.toString(id)));
		PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id,
				myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), Integer.parseInt(wait) * 60000,
				pendingIntent);
	}

	private void setupTimedSMS(Context c, String message, String day,
			String recipient, String timeValue, int id) {
		Toast.makeText(c, "New sms saved, " + message, Toast.LENGTH_LONG)
				.show();
		Intent myIntent = new Intent(c, TweezeeReceiver.class);
		myIntent.setAction(Integer.toString(id));
		myIntent.setData(Uri.parse(Integer.toString(id)));
		myIntent.putExtra("type", "sms");
		myIntent.putExtra("message", message);
		myIntent.putExtra("recipient", recipient);
		myIntent.putExtra("day", day);
		PendingIntent pendingIntent;
		pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(timeValue.split(":")[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(timeValue.split(":")[1]));
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				pendingIntent);
	}

	private void setupIntervalSMS(Context c, String message, String wait,
			String day, String recipient, int id) {
		Toast.makeText(c, "New sms saved, " + message, Toast.LENGTH_LONG)
				.show();
		Intent myIntent = new Intent(c, TweezeeReceiver.class);
		myIntent.putExtra("type", "sms");
		myIntent.putExtra("message", message);
		myIntent.putExtra("day", day);
		myIntent.putExtra("recipient", recipient);
		myIntent.setAction(Integer.toString(id));
		myIntent.setData(Uri.parse(Integer.toString(id)));
		PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id,
				myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), Integer.parseInt(wait) * 60000,
				pendingIntent);
	}

	private void setupTimedEmail(Context c, String username, String pass,
			String subject, String message, String day, String recipients,
			String timeValue, int id) {
		Toast.makeText(c, "New email saved, " + message, Toast.LENGTH_LONG)
				.show();
		final DBAdapter db = new DBAdapter(c);
		db.open();
		Cursor cu = null;
		if (id == 420) {
			cu = db.getAllEEntries();
			try {
				while (cu.moveToNext()) {
					if ((cu.getString(cu.getColumnIndex("message"))
							.equals(message))
							&& cu.getString(cu.getColumnIndex("username"))
									.equals(username)) {
						id = Integer.parseInt(cu.getString(cu
								.getColumnIndex("my_id")));
						break;
					}
				}
			} catch (Exception e) {
			}
			cu.close();
		}
		db.close();
		Intent myIntent = new Intent(c, TweezeeReceiver.class);
		myIntent.setAction(Integer.toString(id));
		myIntent.setData(Uri.parse(Integer.toString(id)));
		myIntent.putExtra("type", "email");
		myIntent.putExtra("username", username);
		myIntent.putExtra("message", message
				+ "\n\n\nSent via UltimateScheduler");
		myIntent.putExtra("recipient", recipients);
		myIntent.putExtra("day", day);
		myIntent.putExtra("pass", pass);
		myIntent.putExtra("subject", subject);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id,
				myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(timeValue.split(":")[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(timeValue.split(":")[1]));
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				pendingIntent);
	}

	private void setupIntervalEmail(Context c, String username, String pass,
			String subject, String message, String wait, String day,
			String recipients, int id) {
		Toast.makeText(c, "New email saved, " + message, Toast.LENGTH_LONG)
				.show();
		final DBAdapter db = new DBAdapter(c);
		db.open();
		if (id == 420) {
			Cursor cu = db.getAllEEntries();
			try {
				while (cu.moveToNext()) {
					if ((cu.getString(cu.getColumnIndex("message"))
							.equals(message))
							&& cu.getString(cu.getColumnIndex("username"))
									.equals(username)) {
						id = Integer.parseInt(cu.getString(cu
								.getColumnIndex("my_id")));
						break;
					}
				}
			} catch (Exception e) {
			}
			cu.close();
			db.close();
		}
		Intent myIntent = new Intent(c, TweezeeReceiver.class);
		myIntent.putExtra("type", "email");
		myIntent.putExtra("username", username);
		myIntent.putExtra("message", message
				+ "\n\n\nSent via UltimateScheduler");
		myIntent.putExtra("recipient", recipients);
		myIntent.putExtra("day", day);
		myIntent.putExtra("pass", pass);
		myIntent.putExtra("subject", subject);
		myIntent.setAction(Integer.toString(id));
		myIntent.setData(Uri.parse(Integer.toString(id)));
		PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id,
				myIntent, 0);
		AlarmManager alarmManager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), Integer.parseInt(wait) * 60000,
				pendingIntent);
	}

	private int getTID(String user, String message, Context ctx) {
		int id = 420;
		final DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor cu = db.getAllTEntries();
		try {
			while (cu.moveToNext()) {
				if ((cu.getString(cu.getColumnIndex("message")).equals(message))
						&& cu.getString(cu.getColumnIndex("username")).equals(
								user)) {
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

	private int getSID(String recipient, String message, Context ctx) {
		int id = 420;
		final DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor cu = db.getAllSEntries();
		try {
			while (cu.moveToNext()) {
				if ((cu.getString(cu.getColumnIndex("message")).equals(message))
						&& cu.getString(cu.getColumnIndex("send_to")).equals(
								recipient)) {
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

	private int getEID(String user, String message, Context ctx) {
		int id = 420;
		final DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor cu = db.getAllEEntries();
		try {
			while (cu.moveToNext()) {
				if ((cu.getString(cu.getColumnIndex("message")).equals(message))
						&& Encryption.decryptString(
								cu.getString(cu.getColumnIndex("username")),
								Encryption.KEY).equals(user)) {
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
}