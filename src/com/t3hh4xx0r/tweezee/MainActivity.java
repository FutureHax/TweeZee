package com.t3hh4xx0r.tweezee;

import java.io.IOException;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.pontiflex.mobile.webview.sdk.AdManagerFactory;
import com.pontiflex.mobile.webview.sdk.IAdManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends SherlockPreferenceActivity {
	public static SharedPreferences prefs;
	
	/** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.main);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    SharedPreferences.Editor editor = prefs.edit();
		IconPreferenceScreenLeft mSMS = (IconPreferenceScreenLeft) findPreference("sms");
		IconPreferenceScreenLeft mEmail = (IconPreferenceScreenLeft) findPreference("email");

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
		   mSMS.setEnabled(false);	
		   mSMS.setSummary("Upgrade to Premium today to unlock this feature");
		   mEmail.setEnabled(false);	
		   mEmail.setSummary("Upgrade to Premium today to unlock this feature");
		}
		   
   		try {
			new SimpleEula(this).show();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
    }

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.main_menu, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.feedback) {
	    		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
	    		sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
	    		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "r2doesinc@gmail.com" });
	    		sendIntent.setData(Uri.parse("r2doesinc@gmail.com"));
	    		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Ultimate Scheduler Feedback");
	    		sendIntent.setType("plain/text");
	    		startActivity(sendIntent);
	            return true;
	    } else if (item.getItemId() == R.id.settings) {
	            Intent s = new Intent(this, SettingsMenu.class);
	            s.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(s);
	            return true;
	    } else if (item.getItemId() == R.id.apps) {
	    		Intent marketApp = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=r2doesinc&c=apps"));
				marketApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		 		try{
					startActivity(marketApp);
				}catch(Exception e){
					e.printStackTrace();
				}  
	            return true;
	    } else if (item.getItemId() == R.id.twitter) {
	        	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/r2doesinc"));
        		Intent.createChooser(i, "Select...");
	        	try{
	        		startActivity(i);
	        	}catch(Exception e){
					e.printStackTrace();
				}  	
	            return true;
	    }
		return false;
	}		        
}
