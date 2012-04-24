package com.t3hh4xx0r.tweezee;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		if (prefs.getBoolean("boot", true)) {
			Intent I = new Intent("com.t3hh4xx0r.tweezee");
			I.setClass(context, TweezeeService.class);
			context.startService(I);
		}
	}
}
