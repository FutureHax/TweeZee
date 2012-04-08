package com.t3hh4xx0r.tweezee;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class EntryAdd extends Activity {

	Button save;
	Button cancel;
	EditText et1;
	EditText et2;
	EditText et3;
	TextView tV;
	TextView tV2;
	TextView name;
	ImageView pic;
	int p;
	ArrayList<String> entryArray;
	String[] values;
	Resources res;
	long userID;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_entry);
		
	    res = getResources();
	    entryArray = new ArrayList<String>();
        Bundle extras = getIntent().getExtras();
        p = extras.getInt("pos");
		String usern = MainActivity.users[p].getName(); 
		userID = Long.parseLong(MainActivity.users[p].getId());

	    String[] weekdays = new DateFormatSymbols().getWeekdays();
	    values = new String[] {
	            weekdays[Calendar.MONDAY],
	            weekdays[Calendar.TUESDAY],
	            weekdays[Calendar.WEDNESDAY],
	            weekdays[Calendar.THURSDAY],
	            weekdays[Calendar.FRIDAY],
	            weekdays[Calendar.SATURDAY],
	            weekdays[Calendar.SUNDAY],
	    };
	    
		save = (Button)findViewById(R.id.save_b);
		save.setOnClickListener(new OnClickListener() {
			public void onClick (View v) {
				if (et1.getText().toString().length() != 0 && et2.getText().toString().length() != 0 && et3.getText().toString().length() != 0) {
			       final DBAdapter db = new DBAdapter(v.getContext());
		       	   db.open();
		       	   db.insertEntry(MainActivity.users[p].getName(), et1.getText().toString(), et2.getText().toString(), et3.getText().toString(), "sun");
		       	   db.close();
		       	   finish();
				} else {
					Toast.makeText(v.getContext(), "Do not leave any fields blank.", 99999).show();
				}
			}
		});
		cancel = (Button)findViewById(R.id.cancel_b);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick (View v) {
				finish();
			}
		});
		name = (TextView)findViewById(R.id.userN);
		name.setText("@"+usern);
		pic = (ImageView)findViewById(R.id.userP);
		tV2 = (TextView)findViewById(R.id.mentionsTv);
		tV2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
	            startActivity(new Intent(v.getContext(), MentionsActivity.class).putExtra("pos", p).putExtra("id", userID).putExtra("user", name.getText().toString().replace("@", "")));
			}
		});
		tV = (TextView)findViewById(R.id.dayTv);
		tV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        boolean[] array = {false, false, false, false, false, false, false};	    
				   new AlertDialog.Builder(v.getContext())
				      .setTitle("Days of week")
				      .setCancelable(true)
				      .setMultiChoiceItems(values, array, new OnMultiChoiceClickListener() {
				    	  public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				    		  
				    	  }
					})
				      .setPositiveButton("OK",new DialogInterface.OnClickListener() {
				    	  public void onClick(DialogInterface dialog, int whichButton){
				    	  }
				      })
				      .show();
			}
		});
		et1 = (EditText)findViewById(R.id.editMessage);
		et2 = (EditText)findViewById(R.id.editAmount);
		et3 = (EditText)findViewById(R.id.editInterval);
		
		Thread thread = new Thread() {
			Drawable p;
		    @Override
		    public void run() {
		    	 p = setProfilePic(name.getText().toString().replace("@", ""));		    			    
		         runOnUiThread(new Runnable() {
		               @Override
		               public void run() {
		   		        pic.setImageDrawable(p);
		               }
		           });		    			    		    	
		    }
		};
		thread.start();
	}
	
	public Drawable setProfilePic(String name){
		Drawable d;
		try {
           Twitter twitter = new TwitterFactory().getInstance();
           ProfileImage image = twitter.getProfileImage(name, ProfileImage.BIGGER);
           URL src = new URL(image.getURL());

           Bitmap bm = BitmapFactory.decodeStream(src.openConnection().getInputStream());
           bm = Bitmap.createScaledBitmap(bm, 150, 150, true); 
           d = new BitmapDrawable(bm);
		} catch (Exception e) {
			e.printStackTrace();
			d = res.getDrawable(R.drawable.acct_sel);
		}
        return d;

	}
    
	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
}
