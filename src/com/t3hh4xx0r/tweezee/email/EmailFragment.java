package com.t3hh4xx0r.tweezee.email;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.Encryption;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.sms.EntriesAdapter;
import com.t3hh4xx0r.tweezee.sms.SMSActivity;
import com.t3hh4xx0r.tweezee.twitter.BetterPopupWindow;
import com.t3hh4xx0r.tweezee.twitter.EntryAdd;
import com.t3hh4xx0r.tweezee.twitter.TwitterActivity;

public class EmailFragment extends ListFragment {
	  Context ctx;
	  View v;
	  Button mAddEntry;
	  int pos; 
	  public ArrayList<String> entryArray;
	  static EntriesAdapterE a;
	  ListView listView;
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    ctx = container.getContext();
		    if (v != null) {
		        v.invalidate();
		    }
		    
		    entryArray = new ArrayList<String>();
		    
			final Vibrator vibe = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE) ;

		    v = inflater.inflate(R.layout.user_fragment, container, false);
	        listView = (ListView) v.findViewById(android.R.id.list);
	        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View v,
						int position, long arg3) {
	        		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	        		Editor e = prefs.edit();
	        		e.putBoolean("account", false);
	        		e.commit();	            	
	        		
	    			ArrayList<String> mEntries = new ArrayList<String>();
	    			ArrayList<String> mUsers = new ArrayList<String>();
	    			ArrayList<String> mRecipients = new ArrayList<String>();
	    			DBAdapter db = new DBAdapter(ctx);
	    		    db.open();
	    		    Cursor c = db.getAllEEntries();
	    		    	try {
	    		       		while (c.moveToNext()) {
	    		       			if (Encryption.decryptString(c.getString(c.getColumnIndex("username")), Encryption.KEY).equals(EmailActivity.accounts[pos].getName())) {
	    		       				mEntries.add(c.getString(c.getColumnIndex("message")));
	    		       				mRecipients.add(c.getString(c.getColumnIndex("send_to")));
	    		       				mUsers.add(Encryption.decryptString(c.getString(c.getColumnIndex("username")), Encryption.KEY));
	    		       			}
	    		       		}
	    		       	 } catch (Exception e1) {}
	    		   c.close();
	    		   db.close();	
	    		   
	        	   vibe.vibrate(50);
	               String message = mEntries.get(position); 
	               String recipient = mRecipients.get(position);
	               String user = mUsers.get(position);
	               BetterPopupWindowE dw = new BetterPopupWindowE.DemoPopupWindow(v, message, pos, recipient, user);
	               dw.showLikeQuickAction(0, 30);
	               return false;
	        	}
	        });
	        mAddEntry = (Button) v.findViewById(R.id.entry_b);
	        mAddEntry.setOnClickListener(new OnClickListener() {
	        	@Override
	        	public void onClick(View v) {
	                Bundle b = new Bundle();
	                b.putInt("pos", pos);
		            Intent mi = new Intent(v.getContext(), EntryAddE.class);
		            mi.putExtras(b);
			        startActivity(mi);
	        	}
	        });	   
		    pos = getArguments().getInt("p");
		    populateList();
		    return v;
	}
	  
		@Override
		public void onStart() {
			super.onStart();
			populateList();
		}
		
		@Override
		public void onResume() {
			super.onResume();
			populateList();
		}	
		
		public void onListItemClick(ListView lv, View v, int p, long id) {	
			ArrayList<String> mEntries = new ArrayList<String>();
			ArrayList<String> mRecipients = new ArrayList<String>();
			ArrayList<String> mSubjects = new ArrayList<String>();
			ArrayList<String> mIntervals = new ArrayList<String>();
			ArrayList<String> mDays = new ArrayList<String>();
			ArrayList<String> mTimes = new ArrayList<String>();
			ArrayList<String> mBoots = new ArrayList<String>();
			String r = null;
			String e = null;
			String i = null;
			String d = null;
			String t = null;
			String s = null;
			String boot = null;
			DBAdapter db = new DBAdapter(ctx);
		    db.open();
		    Cursor c = db.getAllEEntries();
		    	try {
		       		while (c.moveToNext()) {
		       			if (Encryption.decryptString(c.getString(c.getColumnIndex("username")), Encryption.KEY).equals(EmailActivity.accounts[pos].getName())) {
		       				mEntries.add(c.getString(c.getColumnIndex("message")));
		       				mSubjects.add(c.getString(c.getColumnIndex("subject")));
		       				mRecipients.add(c.getString(c.getColumnIndex("send_to")));
		       				mIntervals.add(c.getString(c.getColumnIndex("send_wait")));
		       				mDays.add(c.getString(c.getColumnIndex("send_day")));
		       				mTimes.add(c.getString(c.getColumnIndex("send_time")));
		       				mBoots.add(c.getString(c.getColumnIndex("start_boot")));
		       			}
		       		}
		       	 } catch (Exception e1) {
		       		 e1.printStackTrace();
		       	 }
		    c.close();
		    db.close();		
		    e = mEntries.get(p);
		    r = mRecipients.get(p);
		    s = mSubjects.get(p);
		    i = mIntervals.get(p);
		    d = mDays.get(p);
		    t = mTimes.get(p);
		    boot = mBoots.get(p);
            Bundle b = new Bundle();
            b.putBoolean("editing", true);
            b.putInt("pos", pos);
            b.putString("message", e);
            b.putString("interval", i);
            b.putString("recipient", r);
            b.putString("subject", s);
            b.putString("days", d);
            b.putString("boot", boot);
            if (t.length()>1) {
            	b.putString("time", t);
            }
            Intent mi = new Intent(v.getContext(), EntryAddE.class);
            mi.putExtras(b);
            entryArray.remove(p);
	        startActivity(mi);	
		}
		
		public void populateList() {
	    	entryArray.clear();
	    	
			a = new EntriesAdapterE(ctx, entryArray);
			setListAdapter(a);
		    DBAdapter db = new DBAdapter(ctx);
	       	db.open();
	       	Cursor c = db.getAllEEntries();
	       	try {
	       		while (c.moveToNext()) {
	       			if (Encryption.decryptString(c.getString(c.getColumnIndex("username")), Encryption.KEY).equals(EmailActivity.accounts[pos].getName())
	       					&& !entryArray.contains(c.getString(c.getColumnIndex("message")))) {	 
	  					entryArray.add(c.getString(c.getColumnIndex("message"))+ ":"+c.getString(c.getColumnIndex("send_to")));
	       			}
	       		}
	       } catch (Exception e) {
	    	   e.printStackTrace();
	       }
	       c.close();
	       db.close();    	 
		   a.notifyDataSetChanged();
		}
		
		 public static Handler handy = new Handler() {
			public void handleMessage(Message m) {
				switch (m.what) {
				case 0:			
					   a.notifyDataSetChanged();
					break;
				}
			}
		};
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {	   
		        case android.R.id.home:
		            Intent hi = new Intent(ctx, MainActivity.class);
		            hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            startActivity(hi);
		            return true;
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}		  

}
