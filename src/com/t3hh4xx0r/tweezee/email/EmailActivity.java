package com.t3hh4xx0r.tweezee.email;

import java.util.ArrayList;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.Encryption;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.twitter.AccountManager;
import com.t3hh4xx0r.tweezee.twitter.TwitterActivity;
import com.t3hh4xx0r.tweezee.twitter.TwitterAuth;
import com.t3hh4xx0r.tweezee.twitter.TwitterSplash;
import com.t3hh4xx0r.tweezee.twitter.User;
import com.t3hh4xx0r.tweezee.twitter.UserFragment;
import com.t3hh4xx0r.tweezee.twitter.TwitterActivity.ExamplePagerAdapter;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class EmailActivity extends FragmentActivity {
    ViewPager pager;
    static int p;
    public static Accounts[] accounts;
    public static int account;    
	private final static int SIGN_IN = 0;
	public ArrayList<String> entryArray;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.email);
		
	    DBAdapter db = new DBAdapter(this);
	    db.open();
	   	if (!db.isLoggedInE()) {
	   		startActivity(new Intent(this, EmailSplash.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	   	}
	   	db.close();
	   	
	   	getUsers(this);
	   		
        pager = (ViewPager) findViewById(android.R.id.list);
        pager.setAdapter(new ExamplePagerAdapter(getSupportFragmentManager()));
        TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
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
    	pager.setCurrentItem(0);
    	indicator.setViewPager(pager, 0);        
		
	}
	
	public void getUsers(Context ctx) {
        DBAdapter db = new DBAdapter(ctx);
   		db.open();
   		Cursor c = db.getAllEUsers();
   		account = c.getCount();
   		accounts = new Accounts[account];
   		int i=0;
   		try {
   			while (c.moveToNext()) {
   				i++;
   				accounts[i-1] = new Accounts();
   				accounts[i-1].setName(Encryption.decryptString(c.getString(c.getColumnIndex("username")), Encryption.KEY));
   				accounts[i-1].setPassword(Encryption.decryptString(c.getString(c.getColumnIndex("password")), Encryption.KEY));
   			}
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		c.close();
   		db.close();
	}
	
	   public ArrayList<String> updateUserFrag (int p) {		   
		     entryArray = new ArrayList<String>();
		     if (entryArray.size() != 0) {
		    	 entryArray.clear();
		     }
		     DBAdapter db = new DBAdapter(this);
	       	 db.open();
	       	 Cursor c = db.getAllEEntries();
	       	 try {
	       		while (c.moveToNext()) {
	       			if (Encryption.decryptString(c.getString(c.getColumnIndex("username")), Encryption.KEY).equals(accounts[p].getName())) {	       			
	  					entryArray.add(c.getString(c.getColumnIndex("message")));
	       			}
	       		}
	       	 } catch (Exception e) {
	       		 e.printStackTrace();
	       	 }
	       	 c.close();
	       	 db.close();
			return entryArray;	       	 
	   }

	public class ExamplePagerAdapter extends FragmentPagerAdapter implements TitleProvider{

			public ExamplePagerAdapter(FragmentManager fm) {
		        super(fm);
		    }
			
		    @Override
		    public int getCount() {
		        return account;
		    }
		
		    @Override
		    public Fragment getItem(int position) {
		    	Fragment fragment = new EmailFragment();	
		    	Bundle b = new Bundle(); 
		    	b.putInt("p", position); 
		    	b.putStringArrayList("e", updateUserFrag(position));
		    	fragment.setArguments(b);
		        return fragment;
		    }
		   
			@Override
			public String getTitle(int pos) {
				return accounts[pos].getName();
			}
			
	    }

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.email_menu, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.sign_in:
		        Intent si = new Intent(this, EmailLogin.class);
		        si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivityForResult(si, SIGN_IN);
	        break;
	        case R.id.manage_acct:
	            Intent mi = new Intent(this, EmailAcctManager.class);
	            mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(mi);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    switch (requestCode) {
	    case SIGN_IN:
            Intent mi = new Intent(this, EmailActivity.class);
            startActivity(mi);
	        break;
	    default:
	        break;
	    }
	}
}
