package com.t3hh4xx0r.tweezee.sms;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;

public class SMSActivity extends ListActivity {
	ListView lv1;
	Button mAddEntry;
	static EntriesAdapter a;
	static ArrayList<String> mEntries;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.sms_main);
	    lv1 = (ListView) findViewById(android.R.id.list);   
        lv1.setOnItemLongClickListener(new OnItemLongClickListener() {
        	@Override
        	public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {           	
    			ArrayList<String> mMessages = new ArrayList<String>();
    			ArrayList<String> mRecipients = new ArrayList<String>();
    			DBAdapter db = new DBAdapter(v.getContext());
    		    db.open();
    		    Cursor c = db.getAllSEntries();
    		    	try {
    		       		while (c.moveToNext()) {
    		       			mMessages.add(c.getString(c.getColumnIndex("message")));
    		       			mRecipients.add(c.getString(c.getColumnIndex("send_to")));
    		       		}
    		       	 } catch (Exception e1) {}
    		   c.close();
    		   db.close();
    		   
    		   final Vibrator vibe = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
        	   vibe.vibrate(50);
               String message = mMessages.get(position); 
               String recipient = mRecipients.get(position); 
               BetterPopupWindowS dw = new BetterPopupWindowS.DemoPopupWindow(v, message, recipient, position);
               dw.showLikeQuickAction(0, 30);
               return false;
        	}
        });
        
        mAddEntry = (Button) findViewById(R.id.entry_b);
        mAddEntry.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		Intent i = new Intent(SMSActivity.this, EntryAddS.class);
        		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		i.putExtra("editing", false);
        		startActivity(i);
        	}
        });	   
	}

	public void onListItemClick(ListView lv, View v, int p, long id) {	
		ArrayList<String> mMessages = new ArrayList<String>();
		ArrayList<String> mRecipients = new ArrayList<String>();
		ArrayList<String> mIntervals = new ArrayList<String>();
		ArrayList<String> mDays = new ArrayList<String>();
		ArrayList<String> mTimes = new ArrayList<String>();
		ArrayList<String> mBoots = new ArrayList<String>();
		String m = null;
		String r = null;
		String i = null;
		String d = null;
		String t = null;
		String boot = null;
		DBAdapter db = new DBAdapter(v.getContext());
	    db.open();
	    Cursor c = db.getAllSEntries();
	    	try {
	       		while (c.moveToNext()) {
	       			mMessages.add(c.getString(c.getColumnIndex("message")));
	       			mRecipients.add(c.getString(c.getColumnIndex("send_to")));
	       			mIntervals.add(c.getString(c.getColumnIndex("send_wait")));
	       			mDays.add(c.getString(c.getColumnIndex("send_day")));
	       			mTimes.add(c.getString(c.getColumnIndex("send_time")));
	       			mBoots.add(c.getString(c.getColumnIndex("start_boot")));
	       		}
	       	 } catch (Exception e1) {}
	    c.close();
	    db.close();		
	    r = mRecipients.get(p);
	    m = mMessages.get(p);
	    i = mIntervals.get(p);
	    d = mDays.get(p);
	    t = mTimes.get(p);
	    boot = mBoots.get(p);
        Bundle b = new Bundle();
        b.putBoolean("editing", true);
        b.putInt("pos", p);
        b.putString("message", m);
        b.putString("interval", i);
        b.putString("recipient", r);
        b.putString("days", d);
        b.putString("boot", boot);
        if (t.length()>1) {
        	b.putString("time", t);
        } else {
        	b.putString("time", null);
        }
        Intent mi = new Intent(v.getContext(), EntryAddS.class);
        mi.putExtras(b);
        startActivity(mi);	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		populateList(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		populateList(this);
	}
	
	void populateList(Context ctx) {
		mEntries = new ArrayList<String>();
		if (mEntries.size() != 0) {
			mEntries.clear();
		}
		a = new EntriesAdapter(SMSActivity.this, mEntries);
	    lv1.setAdapter(a);

		DBAdapter db = new DBAdapter(ctx);
	    db.open();
	    Cursor cu = db.getAllSEntries();
	    	try {
	       		while (cu.moveToNext()) {
	       			StringBuilder sB = new StringBuilder();
	       			sB.append(cu.getString(cu.getColumnIndex("message")));
	       			sB.append(":");
	       			sB.append(cu.getString(cu.getColumnIndex("send_to")));
	       			mEntries.add(sB.toString());
	       		}
	       	 } catch (Exception e1) {}
	   cu.close();
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
	            Intent hi = new Intent(this, MainActivity.class);
	            hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(hi);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	
}
