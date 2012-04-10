package com.t3hh4xx0r.tweezee;

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
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class MainActivity extends FragmentActivity {
	
    static User[] users;
    public static int user;

    ViewPager pager;
	ArrayList<String> entryArray;
	UserFragment uF;
	Handler handy;
	int place;
	int p = 0;
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
        
		getUsers(this);        
        
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
       		Cursor c = db.getAllUsers();
       		user = c.getCount();
       		users = new User[c.getCount()];
       		int i=0;
       		try {
       			while (c.moveToNext()) {
       				i++;
       	       		users[i-1] = new User();
		        	users[i-1].setId(c.getString(c.getColumnIndex("user_id")));	
		        	users[i-1].setName(c.getString(c.getColumnIndex("username")));	
		        	users[i-1].setToken(c.getString(c.getColumnIndex("oauth_token")));	
		        	users[i-1].setSecret(c.getString(c.getColumnIndex("oauth_token_secret")));	
       			}
       		} catch (Exception e) {
       			e.printStackTrace();
       		}
       		c.close();
       		db.close();
	}	
	
	   public ArrayList<String> updateUserFrag (int p) {		   
		     entryArray = new ArrayList<String>();
		     DBAdapter db = new DBAdapter(this);
	       	 db.open();
	       	 Cursor c = db.getAllEntries();
	       	 try {
	       		while (c.moveToNext()) {
	   	         StringBuilder sb = new StringBuilder();
	       			if (c.getString(0).equals(users[p].getName())) {	       			
	  					sb.append(c.getString(1));
	  					sb.append(" "+c.getString(c.getColumnIndex("mentions")));
	  					entryArray.add(sb.toString());
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
	        case R.id.limit:
	        	apiCheck();
	        break;	   
	        case android.R.id.home:
	            Intent hi = new Intent(this, MainActivity.class);
	            hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(hi);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
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
        AccessToken token = new AccessToken(users[p].getToken(), users[p].getSecret());
        t.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
        t.setOAuthAccessToken(token);
        
		int left = 0;
		int total = 0;
		int mins = 0;
		
		try {
			left = t.getRateLimitStatus().getRemainingHits();
			total = t.getRateLimitStatus().getHourlyLimit();
			mins = t.getRateLimitStatus().getSecondsUntilReset()/60;
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("TweeZee API limit");
	    builder.setIcon(R.drawable.ic_launcher);
	    builder.setMessage("You currently have "+Integer.toString(left)+" of "+Integer.toString(total)+".\nThis limit will refresh in "+Integer.toString(mins)+" minutes")
			   .setCancelable(false)
			   .setPositiveButton("Cool beans man.", new DialogInterface.OnClickListener() {
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
