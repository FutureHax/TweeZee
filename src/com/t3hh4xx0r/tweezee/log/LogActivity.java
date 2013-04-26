package com.t3hh4xx0r.tweezee.log;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class LogActivity extends SherlockFragmentActivity {
	ViewPager pager;
	int place;
	int p;
	String options[] = new String[] { "Twitter", "SMS", "Email" };

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.log);

		pager = (ViewPager) findViewById(android.R.id.list);
		pager.setAdapter(new ExamplePagerAdapter(getSupportFragmentManager()));
		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		pager.setCurrentItem(0);
		indicator.setViewPager(pager, 0);

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

	public class ExamplePagerAdapter extends FragmentPagerAdapter implements
			TitleProvider {

		public ExamplePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return options.length;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new LogFragment();
			Bundle b = new Bundle();
			b.putInt("p", position);
			b.putString("type", options[position]);
			fragment.setArguments(b);
			return fragment;
		}

		@Override
		public String getTitle(int position) {
			return options[position];
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.log_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		} else if (item.getItemId() == R.id.clear) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning");
			builder.setMessage(
					"This will delete all of your logs. Are you sure you want to continue?")
					.setCancelable(false)
					.setPositiveButton("For sure dude.",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									File og = new File(
											Environment
													.getExternalStorageDirectory()
													+ "/t3hh4xx0r/ultimate_scheduler/log.txt");
									og.delete();
								}
							})
					.setNegativeButton("Nah, nevermind.",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
		return false;
	}
}
