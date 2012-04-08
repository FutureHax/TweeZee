package com.t3hh4xx0r.tweezee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class MainActivity extends FragmentActivity {
	
    static User[] users;
    public static int user;

    ViewPager pager;
	ArrayList<String> entryArray;
	Button mAddEntry;
	UserFragment uF;
	Handler handy;
	int place;
	private final static int SIGN_IN = 0;

    
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		
        try {
			new SimpleEula(this).show();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
        
        try {
            Bundle extras = getIntent().getExtras();
            place = extras.getInt("pos");
        } catch (Exception e) {
        	place = 999;
        }
        
        pager = (ViewPager) findViewById(android.R.id.list);
        pager.setAdapter(new ExamplePagerAdapter(getSupportFragmentManager()));
        TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);

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
        	}

			@Override
			public void onPageScrollStateChanged(int arg0) {				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {				
			}
        });
		getUsers();        
    }
	
	public void getUsers() {
	        DBAdapter db = new DBAdapter(this);
       		db.open();
       		Cursor c = db.getAllUsers();
       		user = c.getCount();
       		users = new User[c.getCount()];
       		try {
       			while (c.moveToNext()) {
       				int _id = Integer.parseInt(c.getString(c.getColumnIndex("_id")));
       	       		users[_id-1] = new User();
		        	users[_id-1].setId(c.getString(c.getColumnIndex("user_id")));	
		        	users[_id-1].setName(c.getString(c.getColumnIndex("username")));	
		        	users[_id-1].setToken(c.getString(c.getColumnIndex("oauth_token")));	
		        	users[_id-1].setSecret(c.getString(c.getColumnIndex("oauth_token_secret")));	
       			}
       		} catch (Exception e) {
       			e.printStackTrace();
       		}
       		c.close();
       		db.close();
	}	
	
	   public ArrayList<String> updateUserFrag (int p) {		   
		     entryArray = new ArrayList<String>();
	         StringBuilder sb = new StringBuilder();
		     DBAdapter db = new DBAdapter(this);
	       	 db.open();
	       	 Cursor c = db.getAllEntries();
	       	 try {
	       		while (c.moveToNext()) {
	       			if (c.getString(0).equals(users[p].getName())) {
	  					sb.append(c.getString(1));
	  					entryArray.add(c.getString(1));
	       			}
	       		}
	       	 } catch (Exception e) {}
	       	 c.close();
	       	 db.close();
			return entryArray;	       	 
	   }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.main_menu, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.settings:
	            Intent s = new Intent(this, SettingsMenu.class);
	            s.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(s);
	        break;
	        case R.id.sign_in:
	            Intent si = new Intent(this, TwitterAuth.class);
	            si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivityForResult(si, SIGN_IN);
	        break;
	        case R.id.manage_acct:
	            Intent mi = new Intent(this, AccountManager.class);
	            mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(mi);
	        break;	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		return false;
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    switch (requestCode) {
	    case SIGN_IN:
            Intent mi = new Intent(this, MainActivity.class);
            startActivity(mi);
	        break;
	    default:
	        break;
	    }
	}

	   public class ExamplePagerAdapter extends FragmentPagerAdapter implements TitleProvider{

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
		    	b.putStringArrayList("e", updateUserFrag(position));
		    	fragment.setArguments(b);
		        return fragment;
		    }
		    
			@Override
			public String getTitle(int pos) {
				return "@"+users[pos].getName();
			}
			
	    }
}
