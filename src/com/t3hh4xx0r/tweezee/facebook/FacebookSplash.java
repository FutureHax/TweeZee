package com.t3hh4xx0r.tweezee.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;


public class FacebookSplash extends SherlockActivity {
	TextView signIn;
	private FacebookConnector facebookConnector;
	
	private static final String FACEBOOK_APPID = "405018012875515";
	private static final String FACEBOOK_PERMISSION = "publish_stream";
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.splash);
        this.facebookConnector = new FacebookConnector(FACEBOOK_APPID, this, getApplicationContext(), new String[] {FACEBOOK_PERMISSION});

		signIn = (TextView) findViewById(R.id.sign_in);
		signIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {
					@Override
					public void onAuthSucceed() {
			            Intent mi = new Intent(getBaseContext(), FacebookActivity.class);
			            mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			            mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            startActivity(mi);
			        }

					@Override
					public void onAuthFail(String error) {
						Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
					}
				};
				SessionEvents.addAuthListener(listener);
				facebookConnector.login();
				}			
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	        case android.R.id.home:
	            Intent hi = new Intent(this, MainActivity.class);
	            hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(hi);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	
}
