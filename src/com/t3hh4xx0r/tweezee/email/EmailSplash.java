package com.t3hh4xx0r.tweezee.email;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;

public class EmailSplash extends SherlockActivity {
	TextView signIn;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.splash);

		signIn = (TextView) findViewById(R.id.sign_in);
		signIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(v.getContext(),
						EmailLogin.class), 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case 0:
			Intent mi = new Intent(this, EmailActivity.class);
			mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mi);
			break;
		default:
			break;
		}
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
