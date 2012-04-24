package com.t3hh4xx0r.tweezee.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class TweezeeService extends Service {
  
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
		//DO STUFF NUKKA
	}
	
}