package com.t3hh4xx0r.tweezee.email;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.Encryption;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class EmailActivity extends SherlockFragmentActivity {
    ViewPager pager;
    static int p;
    public static Accounts[] accounts;
    public int account;    
	private final static int SIGN_IN = 0;
	public ArrayList<String> entryArray;
	int place;
	File dir = new File(Environment.getExternalStorageDirectory()+"/t3hh4xx0r/ultimate_scheduler/backups");
	File backup = new File(dir+"/email.txt");
	
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
	    if (item.getItemId() == R.id.sign_in) {
		        Intent si = new Intent(this, EmailLogin.class);
		        si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivityForResult(si, SIGN_IN);
	            return true;
	    } else if (item.getItemId() == R.id.manage_acct) {
	            Intent mi = new Intent(this, EmailAcctManager.class);
	            mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(mi);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    switch (requestCode) {
	    case SIGN_IN:
            Intent mi = new Intent(this, EmailActivity.class);
            mi.putExtra("pos", p);
            startActivity(mi);
	        break;
	    default:
	        break;
	    }
	}
	
	private void prepareBackupEntries(final Context c) {
		DBAdapter db = new DBAdapter(c);
	    db.open();
	    Cursor cu = db.getAllEEntries();
	    int count = cu.getCount();
	    cu.close();
	    db.close();
	    
	    if (count < 1) {
           	AlertDialog.Builder builder = new AlertDialog.Builder(this);
      		builder.setTitle("Whoopsie");
      		builder.setMessage("You've got no entires to backup!")
      		   .setCancelable(false)
      		   .setPositiveButton("Oh ya! My bad.", new DialogInterface.OnClickListener() {
      		       public void onClick(DialogInterface dialog, int id) {
      		    	   dialog.dismiss();
      		 		}
      		   });
      		AlertDialog alert = builder.create();
      		alert.show();
	    } else {
	    	if (backup.exists()) {
		    	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
	      		builder2.setTitle("Warning");
	      		builder2.setMessage("This will overwrite your current backup. Continue?")
	      		   .setCancelable(false)
	      		   .setPositiveButton("Yup.", new DialogInterface.OnClickListener() {
	      		       public void onClick(DialogInterface dialog, int id) {
	      		    	   backup.delete();
	      		    	   backupEntries(c);
	      		       }
	      		   })
	      		   .setNegativeButton("Nah.", new DialogInterface.OnClickListener() {
	      		       public void onClick(DialogInterface dialog, int id) {
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
	    Cursor cu = db.getAllEEntries();
	    int count = cu.getCount();
    	try {
       		while (cu.moveToNext()) {				
       			StringBuilder sB = new StringBuilder();
       			String username = cu.getString(cu.getColumnIndex("username"));
       			String subject = cu.getString(cu.getColumnIndex("subject"));
       			String message = cu.getString(cu.getColumnIndex("message"));
       			String send_to = cu.getString(cu.getColumnIndex("send_to"));
       			String send_wait = cu.getString(cu.getColumnIndex("send_wait"));
       			String send_day = cu.getString(cu.getColumnIndex("send_day"));
       			String send_time = cu.getString(cu.getColumnIndex("send_time"));
       			String start_boot = cu.getString(cu.getColumnIndex("start_boot"));
       			String my_id = cu.getString(cu.getColumnIndex("my_id"));
       			String send_date = cu.getString(cu.getColumnIndex("send_date"));
       			sB.append("///");
       			sB.append(username+"//");
       			if (send_date.length()>0) {
           			sB.append(send_date+"//");
       			} else {
       				sB.append("--//");
       			}
       			sB.append(message+"//");
       			sB.append(subject+"//");
       			sB.append(send_to+"//");
       			if (send_wait.length()>0) {
           			sB.append(send_wait+"//");
       			} else {
       				sB.append("--//");
       			}
       			sB.append(send_day+"//");
       			if (send_time.length()>0) {
           			sB.append(send_time+"//");
       			} else {
       				sB.append("--//");
       			}
           		sB.append(start_boot+"//");
           		sB.append(my_id+"//");

       			bW.append(sB.toString());  
       			bW.newLine();
       			bW.newLine();
       			if (cu.getPosition() == count-1) {
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
