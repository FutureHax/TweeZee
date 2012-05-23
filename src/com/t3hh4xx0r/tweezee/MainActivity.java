package com.t3hh4xx0r.tweezee;

import java.io.IOException;

import com.pontiflex.mobile.webview.sdk.AdManagerFactory;
import com.pontiflex.mobile.webview.sdk.IAdManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MainActivity extends PreferenceActivity {
	public static SharedPreferences prefs;
	
	/** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.main);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    SharedPreferences.Editor editor = prefs.edit();
	    
	    if (!prefs.getBoolean("lReg", false) && prefs.getBoolean("isReg", false)) {
	        editor.putBoolean("lReg", true);
	        editor.commit();
	        
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  		builder.setTitle("Thanks for purchasing!");
	  		builder.setMessage("Enjoy the premium features.")
	  		   .setCancelable(false)
	  		   .setPositiveButton("Can\'t wait to check em out!", new DialogInterface.OnClickListener() {
	  		       public void onClick(DialogInterface dialog, int id) {
	  		    	   dialog.dismiss();
	  		       }
	  		   });
	  		AlertDialog alert = builder.create();
	  		alert.show();
	    }
	    if (!prefs.getBoolean("isReg", false)) {
	    	int count = prefs.getInt("adCount", 0);
	    	int nCount = count+1;
	    	IAdManager adManager = AdManagerFactory.createInstance(getApplication());
	    	if (count == 3) {
	    		prefs.edit().putInt("adCount", 0).commit();
	    	} else if (count == 0){
	    		prefs.edit().putInt("adCount", nCount).commit();        		
	    		adManager.showAd();        		
	    	} else {
	    		prefs.edit().putInt("adCount", nCount).commit();        		
	     	}
		   Intent intent = new Intent("com.t3hh4xx0r.tweezee.REGISTER");
		   this.sendBroadcast(intent, Manifest.permission.REGISTER);
		   
	   		try {
				new SimpleEula(this).show();
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		}
    }
}
