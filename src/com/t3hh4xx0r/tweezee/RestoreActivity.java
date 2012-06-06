package com.t3hh4xx0r.tweezee;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

public class RestoreActivity extends SherlockListActivity{
	ListView lv1;
	String[] options = new String[] {"Twitter", "Facebook", "Email", "SMS"};
	String[] backups = new String[] {"twitter.txt", "facebook.txt", "email.txt", "sms.txt"};
	
	File dir = new File(Environment.getExternalStorageDirectory()+"/t3hh4xx0r/ultimate_scheduler/backups");
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.restore);
	    lv1 = (ListView) findViewById(android.R.id.list);   
	    ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
	    lv1.setAdapter(a);
	}
	
	public void onListItemClick(ListView lv, View v, final int p, long id) {	
		final File selectedBackup = new File(dir+"/"+backups[p]);
		if (selectedBackup.exists()) {
          	AlertDialog.Builder builder = new AlertDialog.Builder(this);
      		builder.setTitle("Warning");
      		builder.setMessage("This will delete any currently saved entires. Continue?")
      		   .setCancelable(false)
      		   .setPositiveButton("Yup.", new DialogInterface.OnClickListener() {
      		       public void onClick(DialogInterface dialog, int id) {
      		    	   restoreBackup(selectedBackup, options[p]);
      		       }
      		   })
      		   .setNegativeButton("Nope.", new DialogInterface.OnClickListener() {
      		       public void onClick(DialogInterface dialog, int id) {
      		    	   dialog.dismiss();
      		 		}
      		   });
      		AlertDialog alert = builder.create();
      		alert.show();
		} else {
          	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
      		builder2.setTitle("Oppsie!");
      		builder2.setMessage("No backup found for this method.")
      		   .setCancelable(false)
      		   .setPositiveButton("Gotcha!", new DialogInterface.OnClickListener() {
      		       public void onClick(DialogInterface dialog, int id) {
      		    	   restoreBackup(selectedBackup, options[p]);
      		       }
      		   });
      		AlertDialog alert2 = builder2.create();
      		alert2.show();
		}
	}

	protected void restoreBackup(File selectedBackup, String type) {
		DBAdapter db = new DBAdapter(this);
	    db.open();
	    if (type.equals("Facebook")) {
	    	db.deleteTable("facebook_entries");
	    	restoreFacebook(selectedBackup);
	    } else if (type.equals("Twitter")) {
	    	db.deleteTable("twitter_entries");	
	    	restoreTweets(selectedBackup);
	    } else if (type.equals("SMS")) {
	    	db.deleteTable("sms_entries");
	    	restoreSMS(selectedBackup);
	    } else if (type.equals("Email")) {
	    	db.deleteTable("email_entries");
	    	restoreEmails(selectedBackup);
	    }
	    db.close();
	}
	
	private void restoreFacebook(File selectedBackup) {
		DBAdapter db = new DBAdapter(this);
	    db.open();
		FileInputStream fI = null;
		try {
			fI = new FileInputStream(selectedBackup);	
			DataInputStream dI = new DataInputStream(fI);
			BufferedReader bR = new BufferedReader(new InputStreamReader(dI));
			String l;
			while ((l = bR.readLine()) != null) {
				if (l.startsWith("///")) {
	       			String message = l.replaceFirst("///", "").split("//")[0];
	       			String send_wait = l.replaceFirst("///", "").split("//")[1];	       			
	       			String send_day = l.replaceFirst("///", "").split("//")[2];
	       			String send_time = l.replaceFirst("///", "").split("//")[3];
	       			String start_boot = l.replaceFirst("///", "").split("//")[4];
	       			String my_id = l.replaceFirst("///", "").split("//")[5];
	       			String send_date = l.replaceFirst("///", "").split("//")[6];
	       			if (send_wait.equals("--")) {
	       				send_wait = "";
	       			}
	       			if (send_time.equals("--")) {
	       				send_time = "";
	       			}
	       			if (send_date.equals("--")) {
	       				send_date = "";
	       			}
	       			db.insertFEntry(message, send_wait, send_day, send_time, start_boot, Integer.parseInt(my_id), send_date);
	       			db.updateActiveF(my_id, false);
				}				
			}
			dI.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.close();			
	}

	private void restoreEmails(File selectedBackup) {
		DBAdapter db = new DBAdapter(this);
	    db.open();
		FileInputStream fI = null;
		try {
			fI = new FileInputStream(selectedBackup);	
			DataInputStream dI = new DataInputStream(fI);
			BufferedReader bR = new BufferedReader(new InputStreamReader(dI));
			String l;
			while ((l = bR.readLine()) != null) {
				if (l.startsWith("///")) {
					String username = l.replaceFirst("///", "").split("//")[0];
	       			String send_date = l.replaceFirst("///", "").split("//")[1];
	       			String message = l.replaceFirst("///", "").split("//")[2];
	       			String subject = l.replaceFirst("///", "").split("//")[4];
	       			String send_to = l.replaceFirst("///", "").split("//")[5];
	       			String send_wait = l.replaceFirst("///", "").split("//")[6];	       			
	       			String send_day = l.replaceFirst("///", "").split("//")[7];
	       			String send_time = l.replaceFirst("///", "").split("//")[8];
	       			String start_boot = l.replaceFirst("///", "").split("//")[9];
	       			String my_id = l.replaceFirst("///", "").split("//")[10];
	       			if (send_wait.equals("--")) {
	       				send_wait = "";
	       			}
	       			if (send_time.equals("--")) {
	       				send_time = "";
	       			}
	       			if (send_date.equals("--")) {
	       				send_date = "";
	       			}
	       			db.insertEEntry(username, subject, message, send_wait, send_day, send_to, send_time, send_date, start_boot, Integer.parseInt(my_id));
	       			db.updateActiveE(username, message, false);
				}				
			}
			dI.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.close();		
	}

	private void restoreTweets(File selectedBackup) {
		DBAdapter db = new DBAdapter(this);
	    db.open();
		FileInputStream fI = null;
		try {
			fI = new FileInputStream(selectedBackup);	
			DataInputStream dI = new DataInputStream(fI);
			BufferedReader bR = new BufferedReader(new InputStreamReader(dI));
			String l;
			while ((l = bR.readLine()) != null) {
				if (l.startsWith("///")) {
					String username = l.replaceFirst("///", "").split("//")[0];
	       			String send_date = l.replaceFirst("///", "").split("//")[1];
	       			String message = l.replaceFirst("///", "").split("//")[2];
	       			String mentions = l.replaceFirst("///", "").split("//")[3];
	       			String send_wait = l.replaceFirst("///", "").split("//")[4];	       			
	       			String send_day = l.replaceFirst("///", "").split("//")[5];
	       			String send_time = l.replaceFirst("///", "").split("//")[6];
	       			String start_boot = l.replaceFirst("///", "").split("//")[7];
	       			String my_id = l.replaceFirst("///", "").split("//")[8];
	       			if (send_wait.equals("--")) {
	       				send_wait = "";
	       			}
	       			if (send_time.equals("--")) {
	       				send_time = "";
	       			}
	       			if (send_date.equals("--")) {
	       				send_date = "";
	       			}
	       			if (mentions.equals("--")) {
	       				mentions = "";
	       			}
	       			db.insertTEntry(username, message, send_wait, send_day, mentions, send_time, start_boot, Integer.parseInt(my_id), send_date);
	       			db.updateActiveT(username, message, false);
				}				
			}
			dI.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.close();		
	}

	private void restoreSMS(File selectedBackup) {
		DBAdapter db = new DBAdapter(this);
	    db.open();
		FileInputStream fI = null;
		try {
			fI = new FileInputStream(selectedBackup);	
			DataInputStream dI = new DataInputStream(fI);
			BufferedReader bR = new BufferedReader(new InputStreamReader(dI));
			String l;
			while ((l = bR.readLine()) != null) {
				if (l.startsWith("///")) {
	       			String message = l.replaceFirst("///", "").split("//")[0];
	       			String send_to = l.replaceFirst("///", "").split("//")[1];
	       			String send_wait = l.replaceFirst("///", "").split("//")[2];
	       			String send_day = l.replaceFirst("///", "").split("//")[3];
	       			String send_time = l.replaceFirst("///", "").split("//")[4];
	       			String start_boot = l.replaceFirst("///", "").split("//")[5];
	       			String my_id = l.replaceFirst("///", "").split("//")[6];
	       			String send_date = l.replaceFirst("///", "").split("//")[7];
	       			if (send_wait.equals("--")) {
	       				send_wait = "";
	       			}
	       			if (send_time.equals("--")) {
	       				send_time = "";
	       			}
	       			if (send_date.equals("--")) {
	       				send_date = "";
	       			}
	       			db.insertSEntry(message, send_wait, send_day, send_to, send_time, start_boot, Integer.parseInt(my_id), send_date);
	       			db.updateActiveS(my_id, false);
				}				
			}
			dI.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.close();		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
            default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
