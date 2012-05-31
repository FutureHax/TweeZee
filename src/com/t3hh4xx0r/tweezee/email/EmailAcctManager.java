package com.t3hh4xx0r.tweezee.email;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockListActivity;
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.Encryption;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class EmailAcctManager extends SherlockListActivity{
	Button mAddEntry;
	ArrayList<String> data;
	
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
		data = new ArrayList<String>();
        DBAdapter db = new DBAdapter(ctx);
   		db.open();
   		Cursor c = db.getAllEUsers();
   		try {
   			while (c.moveToNext()) {
   				data.add(Encryption.decryptString(c.getString(c.getColumnIndex("username")), Encryption.KEY));
   			}
   		} catch (Exception e) {}
		return data;
	}
	
	public void onListItemClick(ListView lv, View v, int p, long id) {
		String msg = data.get(p);	
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Editor e = prefs.edit();
		e.putBoolean("account", true);
		e.commit();
				
		final Vibrator vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
    	vibe.vibrate(50);
    	BetterPopupWindowE dw = new BetterPopupWindowE.DemoPopupWindow(v, msg,p, null, null);
		dw.showLikeQuickAction(0, 30);
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
