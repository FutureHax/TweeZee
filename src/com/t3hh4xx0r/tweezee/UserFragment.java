package com.t3hh4xx0r.tweezee;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class UserFragment extends ListFragment {
	  Context ctx;
	  View v;
	  Button mAddEntry;
	  int pos; 
	  ArrayList<String> entryArray;
	  ArrayAdapter<String> a;
	  private final static int REQUEST_ADD = 0;
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
	        listView.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> a, View v, int position, long id) { 		    
	            	//EDITABLE
	            }
	        });
	        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	        	@Override
	        	public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
	            	vibe.vibrate(50);
	            	String message = entryArray.get(position); 
	            	BetterPopupWindow dw = new BetterPopupWindow.DemoPopupWindow(v, position, message, pos);
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
			        startActivityForResult(mi, REQUEST_ADD);	
	        	}
	        });	   
		    pos = getArguments().getInt("p");
		    entryArray = getArguments().getStringArrayList("e");
		    return v;
	   }	 
		
		@Override
		public void onStart() {
			super.onStart();
			a = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, entryArray);
			setListAdapter(a);
		}
	
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    switch (requestCode) {
		    case REQUEST_ADD:
	
		        if(resultCode != -1) {
			         StringBuilder sb = new StringBuilder();
				     DBAdapter db = new DBAdapter(ctx);
			       	 db.open();
			       	 Cursor c = db.getAllEntries();
			       	 try {
			       		while (c.moveToNext()) {
			       			if (c.getString(0).equals(MainActivity.users[pos].getName()) && !entryArray.contains(c.getString(1))) {
			  					sb.append(c.getString(1));
			  					entryArray.add(c.getString(1));
			       			}
			       		}
			       	 } catch (Exception e) {}
			       	 c.close();
			       	 db.close();
			       	 
			       	 a.notifyDataSetChanged();
		        } 
	
		    default:
		        break;
		    }
		}
}