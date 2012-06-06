package com.t3hh4xx0r.tweezee.sms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;

public class SMSActivity extends SherlockListActivity {
	ListView lv1;
	Button mAddEntry;
	static EntriesAdapter a;
	static ArrayList<String> mEntries;
	File dir = new File(Environment.getExternalStorageDirectory()+"/t3hh4xx0r/ultimate_scheduler/backups");
	File backup = new File(dir+"/sms.txt");

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
		ArrayList<String> mDates = new ArrayList<String>();
		String m = null;
		String r = null;
		String i = null;
		String d = null;
		String t = null;
		String date = null;
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
       				mDates.add(c.getString(c.getColumnIndex("send_date")));
	       		}
	       	 } catch (Exception e1) {}
	    c.close();
	    db.close();		
	    r = mRecipients.get(p);
	    m = mMessages.get(p);
	    i = mIntervals.get(p);
	    d = mDays.get(p);
	    date = mDates.get(p);
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
        if (date.length()>1) {
        	b.putString("date", date);
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

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.sms_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	        case android.R.id.home:
	            Intent hi = new Intent(this, MainActivity.class);
	            hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(hi);
	            return true;
	        case R.id.backup:
	        	prepareBackupEntries(this);
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void prepareBackupEntries(final Context c) {
		DBAdapter db = new DBAdapter(c);
	    db.open();
	    Cursor cu = db.getAllSEntries();
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
	    Cursor cu = db.getAllSEntries();
	    int count = cu.getCount();
    	try {
       		while (cu.moveToNext()) {
       			StringBuilder sB = new StringBuilder();
       			String message = cu.getString(cu.getColumnIndex("message"));
       			String send_to = cu.getString(cu.getColumnIndex("send_to"));
       			String send_wait = cu.getString(cu.getColumnIndex("send_wait"));
       			String send_day = cu.getString(cu.getColumnIndex("send_day"));
       			String send_time = cu.getString(cu.getColumnIndex("send_time"));
       			String start_boot = cu.getString(cu.getColumnIndex("start_boot"));
       			String my_id = cu.getString(cu.getColumnIndex("my_id"));
       			String send_date = cu.getString(cu.getColumnIndex("send_date"));
       			sB.append("///");
       			sB.append(message+"//");
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
       			if (send_date.length()>0) {
           			sB.append(send_date+"//");
       			} else {
       				sB.append("--//");
       			}
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
