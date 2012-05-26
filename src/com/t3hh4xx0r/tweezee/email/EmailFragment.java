package com.t3hh4xx0r.tweezee.email;

import java.util.ArrayList;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.Encryption;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.twitter.BetterPopupWindow;
import com.t3hh4xx0r.tweezee.twitter.EntryAdd;
import com.t3hh4xx0r.tweezee.twitter.TwitterActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class EmailFragment extends ListFragment {
	  Context ctx;
	  View v;
	  Button mAddEntry;
	  int pos; 
	  public static ArrayList<String> entryArray;
	  static ArrayAdapter<String> a;
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
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
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
		    entryArray = getArguments().getStringArrayList("e");
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
		
		void populateList() {
			a = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, entryArray);
			setListAdapter(a);
		    DBAdapter db = new DBAdapter(ctx);
	       	db.open();
	       	Cursor c = db.getAllEEntries();
	       	try {
	       		while (c.moveToNext()) {
	       			if (Encryption.decryptString(c.getString(c.getColumnIndex("username")), Encryption.KEY).equals(EmailActivity.accounts[pos].getName())
	       					&& !entryArray.contains(Encryption.decryptString(c.getString(c.getColumnIndex("message")), Encryption.KEY))) {	       			
	  					entryArray.add(Encryption.decryptString(c.getString(c.getColumnIndex("message")), Encryption.KEY));
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
