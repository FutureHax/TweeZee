package com.t3hh4xx0r.tweezee;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class MainActivity extends FragmentActivity {
	
    static User[] users;
    
    public static int user;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

        try {
			new SimpleEula(this).show();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 

        ViewPager pager = (ViewPager) findViewById(android.R.id.list);
        pager.setAdapter(new ExamplePagerAdapter(getSupportFragmentManager()));

        pager.setCurrentItem(0);
        TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager, 0);
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

	       final UDBAdapter db = new UDBAdapter(this);
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
   	    
       		db.close();
	}
	
//	public void setProfilePic(String name){
//        Twitter twitter = new TwitterFactory().getInstance();
//        try {
//            ProfileImage image = twitter.getProfileImage(name, ProfileImage.NORMAL);
//            String src = image.getURL();
//        	Drawable i = ImageOperations(this, src ,name);
//
//        	iV.setImageDrawable(i);
//        } catch (TwitterException e) {
//			e.printStackTrace();
//			iV.setImageResource(R.drawable.acct_sel);
//		}
//	}
	
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
	            startActivity(si);
	        break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		return false;
	}	
	
	@Override
	public void onResume() {
		super.onResume();
		getUsers();
	}
	
    private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
    
	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
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
		
		        Bundle args = new Bundle();
		        args.putInt("p", position);
		        fragment.setArguments(args);
		
		        return fragment;
		    }

			@Override
			public String getTitle(int pos) {
				return "@"+users[pos].getName();
			}
			
	    }
}
