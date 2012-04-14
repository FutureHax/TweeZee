package com.t3hh4xx0r.tweezee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;


public class Splash extends Activity {
	TextView signIn;
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.splash);
		
		signIn = (TextView) findViewById(R.id.sign_in);
		signIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	            startActivityForResult(new Intent(v.getContext(), TwitterAuth.class), 0);
			}			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    switch (requestCode) {
	    case 0:
            Intent mi = new Intent(this, MainActivity.class);
            mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mi);
	        break;
	    default:
	        break;
	    }
	}
}
