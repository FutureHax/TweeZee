package com.t3hh4xx0r.tweezee;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.t3hh4xx0r.tweezee.billing.BillingHelper;
import com.t3hh4xx0r.tweezee.billing.BillingService;
import com.t3hh4xx0r.tweezee.billing.C.PurchaseState;

public class MainActivity extends SherlockPreferenceActivity {
	public static SharedPreferences prefs;
	public Handler mTransactionHandler;
	IconPreferenceScreenLeft mSMS, mEmail;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.main);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		final SharedPreferences.Editor editor = prefs.edit();
		mSMS = (IconPreferenceScreenLeft) findPreference("sms");
		mEmail = (IconPreferenceScreenLeft) findPreference("email");

		startService(new Intent(this, BillingService.class));
		mTransactionHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (BillingHelper.latestPurchase.purchaseState == PurchaseState.PURCHASED) {
					editor.putBoolean(
							"has_" + BillingHelper.latestPurchase.productId,
							true).commit();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle("Thanks for purchasing!");
					builder.setMessage(
							"Enjoy the premium features. Please restart the app to unlock.")
							.setCancelable(false)
							.setPositiveButton("Restart Now",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Intent i = new Intent(
													MainActivity.this,
													MainActivity.class);
											startActivity(i);
											finish();
										}
									})
							.setNegativeButton("Restart Later",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.dismiss();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else {
					Toast.makeText(getApplicationContext(), "failed",
							Toast.LENGTH_SHORT).show();
				}
			};
		};
		BillingHelper.setCompletedHandler(mTransactionHandler);
		doPurchasesCheck();

		try {
			new SimpleEula(this).show();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void doPurchasesCheck() {
		if (!prefs.getBoolean("isReg", false)) {
			if (!prefs.getBoolean("has_all_content", false)) {
				CheckRegistrationTask t = new CheckRegistrationTask();
				t.execute();
				if (!prefs.getBoolean("has_sms_content", false)) {
					mSMS.setSummary("Upgrade today to unlock this feature!");
					mSMS.setIntent(null);
				}
				if (!prefs.getBoolean("has_email_content", false)) {
					mEmail.setSummary("Upgrade today to unlock this feature!");
					mEmail.setIntent(null);
				}
			}
		}
	}

	@Override
	@Deprecated
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		String key = preference.getKey();
		if (key.equals("sms") || key.equals("email")) {
			handlePurchaseRequest(key, preferenceScreen.getContext());
			return true;
		}
		return false;

	}

	void handlePurchaseRequest(String type, final Context c) {
		final String purchaseIds[] = new String[] { "all_content",
				"sms_content", "email_content" };
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("Premium Content");
		builder.setItems(
				new String[] { "Unlock all content", "Unlock SMS",
						"Unlock Email" }, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d("ID IS", purchaseIds[which]);
						BillingHelper.requestPurchase(c, purchaseIds[which]);
					}
				}).setCancelable(false);
		AlertDialog alert = builder.create();
		alert.show();
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
			sendIntent.setClassName("com.google.android.gm",
					"com.google.android.gm.ComposeActivityGmail");
			sendIntent.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "r2doesinc@gmail.com" });
			sendIntent.setData(Uri.parse("r2doesinc@gmail.com"));
			sendIntent.putExtra(Intent.EXTRA_SUBJECT,
					"Ultimate Scheduler Feedback");
			sendIntent.setType("plain/text");
			startActivity(sendIntent);
			return true;
		} else if (item.getItemId() == R.id.settings) {
			Intent s = new Intent(this, SettingsMenu.class);
			s.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(s);
			return true;
		} else if (item.getItemId() == R.id.apps) {
			Intent marketApp = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://search?q=r2doesinc&c=apps"));
			marketApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			try {
				startActivity(marketApp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (item.getItemId() == R.id.twitter) {
			Intent i = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.twitter.com/r2doesinc"));
			Intent.createChooser(i, "Select...");
			try {
				startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public class CheckRegistrationTask extends AsyncTask<Object, View, Boolean> {

		@Override
		protected Boolean doInBackground(Object... params) {
			DBAdapter db = new DBAdapter(MainActivity.this);
			db.open();
			PackageManager pm = getPackageManager();
			Intent i = new Intent("android.intent.action.MAIN");
			i.addCategory("com.t3hh4xx0r.tweezee.KEY");
			List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
			if (lst != null && lst.size() == 1) {
				return true;
			}
			return false;
		};

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("isReg", true);
				editor.commit();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("Thanks for purchasing!");
				builder.setMessage(
						"Enjoy the premium features. Please restart the app to unlock.")
						.setCancelable(false)
						.setPositiveButton("Restart Now",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										Intent i = new Intent(
												MainActivity.this,
												MainActivity.class);
										startActivity(i);
										finish();
									}
								})
						.setNegativeButton("Restart Later",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}
}
