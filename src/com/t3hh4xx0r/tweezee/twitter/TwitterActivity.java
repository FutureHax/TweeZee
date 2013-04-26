package com.t3hh4xx0r.tweezee.twitter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class TwitterActivity extends SherlockFragmentActivity {

	public static User[] users;
	public static int user;

	ViewPager pager;
	ArrayList<String> entryArray;
	UserFragment uF;
	Handler handy;
	int place;
	int p = 0;
	private final static int SIGN_IN = 0;

	public static SharedPreferences prefs;

	boolean showAd = true;

	File dir = new File(Environment.getExternalStorageDirectory()
			+ "/t3hh4xx0r/ultimate_scheduler/backups");
	File backup = new File(dir + "/twitter.txt");

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.twitter);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		DBAdapter db = new DBAdapter(this);
		db.open();
		if (!db.isLoggedInT()) {
			startActivity(new Intent(this, TwitterSplash.class)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
		db.close();

		getUsers(this);

		pager = (ViewPager) findViewById(android.R.id.list);
		pager.setAdapter(new ExamplePagerAdapter(getSupportFragmentManager()));
		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		try {
			place = getIntent().getIntExtra("pos", 999);
		} catch (Exception e) {
			place = 999;
		}

		if (place != 999) {
			pager.setCurrentItem(place);
			indicator.setViewPager(pager, place);
		} else {
			pager.setCurrentItem(0);
			indicator.setViewPager(pager, 0);
		}

		indicator.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				p = position;
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});
	}

	public void getUsers(Context ctx) {
		DBAdapter db = new DBAdapter(ctx);
		db.open();
		Cursor c = db.getAllTUsers();
		user = c.getCount();
		users = new User[c.getCount()];
		int i = 0;
		try {
			while (c.moveToNext()) {
				i++;
				users[i - 1] = new User();
				users[i - 1].setId(c.getString(c.getColumnIndex("user_id")));
				users[i - 1].setName(c.getString(c.getColumnIndex("username")));
				if (c.getString(c.getColumnIndex("username")).equals(
						"r2DoesInc")) {
					prefs.edit().putBoolean("isReg", true).commit();
				}
				users[i - 1].setToken(c.getString(c
						.getColumnIndex("oauth_token")));
				users[i - 1].setSecret(c.getString(c
						.getColumnIndex("oauth_token_secret")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.close();
		db.close();
	}

	public ArrayList<String> updateUserFrag(int p) {
		entryArray = new ArrayList<String>();
		if (entryArray.size() != 0) {
			entryArray.clear();
		}
		DBAdapter db = new DBAdapter(this);
		db.open();
		Cursor c = db.getAllTEntries();
		try {
			while (c.moveToNext()) {
				StringBuilder sb = new StringBuilder();
				if (c.getString(0).equals(users[p].getName())) {
					sb.append(c.getString(1));
					sb.append(" " + c.getString(c.getColumnIndex("mentions")));
					entryArray.add(sb.toString());
				}
			}
		} catch (Exception e) {
		}
		c.close();
		db.close();
		return entryArray;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.twitter_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.sign_in) {
			Intent si = new Intent(this, TwitterAuth.class);
			si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(si, SIGN_IN);
			return true;
		} else if (item.getItemId() == R.id.manage_acct) {
			Intent mi = new Intent(this, AccountManager.class);
			mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mi);
		} else if (item.getItemId() == R.id.limit) {
			apiCheck();
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			Intent hi = new Intent(this, MainActivity.class);
			hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(hi);
			return true;
		} else if (item.getItemId() == R.id.backup) {
			prepareBackupEntries(this);
			return true;
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		getUsers(this);
	}

	private void apiCheck() {
		Twitter t = new TwitterFactory().getInstance();
		AccessToken token = new AccessToken(users[p].getToken(),
				users[p].getSecret());
		t.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
		t.setOAuthAccessToken(token);

		int left = 0;
		int total = 0;
		int mins = 0;

		try {
			left = t.getRateLimitStatus().get("statuses").getRemaining();
			total = t.getRateLimitStatus().get("statuses").getLimit();
			mins = t.getRateLimitStatus().get("statuses")
					.getSecondsUntilReset() / 60;
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Twitter API limit");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setMessage(
				"You currently have " + Integer.toString(left) + " of "
						+ Integer.toString(total)
						+ ".\nThis limit will refresh in "
						+ Integer.toString(mins) + " minutes")
				.setCancelable(false)
				.setPositiveButton("Cool beans man.",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case SIGN_IN:
			Intent mi = new Intent(this, TwitterActivity.class);
			mi.putExtra("pos", p);
			startActivity(mi);
			break;
		default:
			break;
		}
	}

	public class ExamplePagerAdapter extends FragmentPagerAdapter implements
			TitleProvider {

		public ExamplePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return user;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new UserFragment();
			Bundle b = new Bundle();
			b.putInt("p", position);
			fragment.setArguments(b);
			return fragment;
		}

		@Override
		public String getTitle(int pos) {
			return "@" + users[pos].getName();
		}

	}

	private void prepareBackupEntries(final Context c) {
		DBAdapter db = new DBAdapter(c);
		db.open();
		Cursor cu = db.getAllTEntries();
		int count = cu.getCount();
		cu.close();
		db.close();

		if (count < 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Whoopsie");
			builder.setMessage("You've got no entires to backup!")
					.setCancelable(false)
					.setPositiveButton("Oh ya! My bad.",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			if (backup.exists()) {
				AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
				builder2.setTitle("Warning");
				builder2.setMessage(
						"This will overwrite your current backup. Continue?")
						.setCancelable(false)
						.setPositiveButton("Yup.",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										backup.delete();
										backupEntries(c);
									}
								})
						.setNegativeButton("Nah.",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();
									}
								});
				AlertDialog alert2 = builder2.create();
				alert2.show();
			} else {
				backupEntries(c);
			}
		}
	}

	protected void backupEntries(Context c) {
		if (!dir.exists()) {
			dir.mkdirs();
		}

		FileWriter fW = null;
		BufferedWriter bW = null;
		try {
			fW = new FileWriter(backup, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bW = new BufferedWriter(fW);

		DBAdapter db = new DBAdapter(c);
		db.open();
		Cursor cu = db.getAllTEntries();
		int count = cu.getCount();
		try {
			while (cu.moveToNext()) {
				StringBuilder sB = new StringBuilder();
				String username = cu.getString(cu.getColumnIndex("username"));
				String message = cu.getString(cu.getColumnIndex("message"));
				String mentions = cu.getString(cu.getColumnIndex("mentions"));
				String send_wait = cu.getString(cu.getColumnIndex("send_wait"));
				String send_day = cu.getString(cu.getColumnIndex("send_day"));
				String send_time = cu.getString(cu.getColumnIndex("send_time"));
				String start_boot = cu.getString(cu
						.getColumnIndex("start_boot"));
				String my_id = cu.getString(cu.getColumnIndex("my_id"));
				String send_date = cu.getString(cu.getColumnIndex("send_date"));
				sB.append("///");
				sB.append(username + "//");
				if (send_date.length() > 0) {
					sB.append(send_date + "//");
				} else {
					sB.append("--//");
				}
				sB.append(message + "//");
				if (mentions.length() > 0) {
					sB.append(mentions + "//");
				} else {
					sB.append("--//");
				}
				if (send_wait.length() > 0) {
					sB.append(send_wait + "//");
				} else {
					sB.append("--//");
				}
				sB.append(send_day + "//");
				if (send_time.length() > 0) {
					sB.append(send_time + "//");
				} else {
					sB.append("--//");
				}
				sB.append(start_boot + "//");
				sB.append(my_id + "//");

				bW.append(sB.toString());
				bW.newLine();
				bW.newLine();
				if (cu.getPosition() == count - 1) {
					bW.flush();
					bW.close();
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		cu.close();
		db.close();
	}
}
