package com.t3hh4xx0r.tweezee.email;

import java.util.ArrayList;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.twitter.AccountManager;
import com.t3hh4xx0r.tweezee.twitter.TwitterActivity;
import com.t3hh4xx0r.tweezee.twitter.TwitterAuth;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class EmailAcctManager extends ListActivity{
	Button mAddEntry;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.accounts);
		
        mAddEntry = (Button) findViewById(R.id.entry_b);
        mAddEntry.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
		       	Intent si = new Intent(v.getContext(), EmailLogin.class);
		       	si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		       	startActivityForResult(si, 0);
        	}
        });	
		
		ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getUsers(this));
		setListAdapter(a);
		getListView().setTextFilterEnabled(true);
	}

	private ArrayList<String> getUsers(Context ctx) {
		ArrayList<String> data = new ArrayList<String>();
        DBAdapter db = new DBAdapter(ctx);
   		db.open();
   		Cursor c = db.getAllEUsers();
   		try {
   			while (c.moveToNext()) {
   				data.add(c.getString(c.getColumnIndex("username")));
   			}
   		} catch (Exception e) {}
		return data;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    switch (requestCode) {
	    case 0:
            Intent mi = new Intent(this, EmailAcctManager.class);
            startActivity(mi);
	        break;
	    default:
	        break;
	    }
	}
}
