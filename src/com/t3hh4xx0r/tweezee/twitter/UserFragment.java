package com.t3hh4xx0r.tweezee.twitter;

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
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.sms.EntriesAdapter;
import com.t3hh4xx0r.tweezee.sms.SMSActivity;

public class UserFragment extends ListFragment {
	  Context ctx;
	  View v;
	  Button mAddEntry;
	  int pos; 
	  public ArrayList<String> entryArray;
	  static EntriesAdapterT a;
	  ListView listView;
	  public static ArrayList<String> idArray;	  
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
	        	public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
	        		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	        		Editor e = prefs.edit();
	        		e.putBoolean("account", false);
	        		e.commit();	            	
	        		
	    			ArrayList<String> mEntries = new ArrayList<String>();
	    			DBAdapter db = new DBAdapter(ctx);
	    		    db.open();
	    		    Cursor c = db.getAllTEntries();
	    		    	try {
	    		       		while (c.moveToNext()) {
	    		       			if (c.getString(0).equals(TwitterActivity.users[pos].getName())) {
	    		       				mEntries.add(c.getString(1));
	    		       			}
	    		       		}
	    		       	 } catch (Exception e1) {}
	    		   c.close();
	    		   db.close();	
	    		   
	        	   vibe.vibrate(50);
	               String message = mEntries.get(position); 
	               BetterPopupWindow dw = new BetterPopupWindow.DemoPopupWindow(v, message, pos, position);
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
		            Intent mi = new Intent(v.getContext(), EntryAdd.class);
		            mi.putExtras(b);
			        startActivity(mi);
    	       	}
	        });	   
		    pos = getArguments().getInt("p");
		    return v;
	}	 
		public void onListItemClick(ListView lv, View v, int p, long id) {	
			ArrayList<String> mEntries = new ArrayList<String>();
			ArrayList<String> mMentions = new ArrayList<String>();
			ArrayList<String> mIntervals = new ArrayList<String>();
			ArrayList<String> mDays = new ArrayList<String>();
			ArrayList<String> mTimes = new ArrayList<String>();
			ArrayList<String> mBoots = new ArrayList<String>();
			String m = null;
			String e = null;
			String i = null;
			String d = null;
			String t = null;
			String boot = null;
			DBAdapter db = new DBAdapter(ctx);
		    db.open();
		    Cursor c = db.getAllTEntries();
		    	try {
		       		while (c.moveToNext()) {
		       			if (c.getString(0).equals(TwitterActivity.users[pos].getName())) {
		       				mEntries.add(c.getString(1));
		       				mMentions.add(c.getString(c.getColumnIndex("mentions")));
		       				mIntervals.add(c.getString(c.getColumnIndex("send_wait")));
		       				mDays.add(c.getString(c.getColumnIndex("send_day")));
		       				mTimes.add(c.getString(c.getColumnIndex("send_time")));
		       				mBoots.add(c.getString(c.getColumnIndex("start_boot")));
		       			}
		       		}
		       	 } catch (Exception e1) {}
		    c.close();
		    db.close();		
		    e = mEntries.get(p);
		    m = mMentions.get(p);
		    i = mIntervals.get(p);
		    d = mDays.get(p);
		    t = mTimes.get(p);
		    boot = mBoots.get(p);
            Bundle b = new Bundle();
            b.putBoolean("editing", true);
            b.putInt("pos", pos);
            b.putString("message", e);
            b.putString("interval", i);
            b.putString("mentions", m);
            b.putString("days", d);
            b.putString("boot", boot);
            if (t.length()>1) {
            	b.putString("time", t);
            }
            Intent mi = new Intent(v.getContext(), EntryAdd.class);
            mi.putExtras(b);
            entryArray.remove(p);
	        startActivity(mi);	
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
	
	void populateList() {
    	entryArray.clear();
  	  	idArray = new ArrayList<String>();
  	  	idArray.clear();
  	  	
		a = new EntriesAdapterT(ctx, entryArray);
		setListAdapter(a);

		DBAdapter db = new DBAdapter(ctx);
	    db.open();
	    Cursor c = db.getAllTEntries();
	    	try {
	       		while (c.moveToNext()) {
	       	    	StringBuilder sb = new StringBuilder();
	       			if (c.getString(0).equals(TwitterActivity.users[pos].getName()) && !entryArray.contains(c.getString(1)+" "+c.getString(c.getColumnIndex("mentions")))) {
	  					sb.append(c.getString(1));
	  					sb.append(" "+c.getString(c.getColumnIndex("mentions")));
	  					entryArray.add(sb.toString());
	  					idArray.add(c.getString(c.getColumnIndex("my_id")));
	       			}
	       		}
	       	 } catch (Exception e) {}
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