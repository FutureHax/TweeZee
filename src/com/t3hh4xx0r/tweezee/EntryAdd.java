package com.t3hh4xx0r.tweezee;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;

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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EntryAdd extends Activity {

	Button save;
	Button cancel;
	EditText et1;
	EditText et2;
	EditText et3;
	TextView tV;
	TextView myCount;
	TextView tV2;
	TextView mPreview;
	TextView dPreview;
	TextView name;
	ImageView pic;
	int p;
	int mLength = 0;
	int totalC = 0;
	int incomingT = 0;
	ArrayList<String> entryArray;
	String[] daysOfWeek;
	Resources res;
	long userID;
	String users;
	Bundle extras;
	StringBuilder selectedDays;
	String myDaysBooleans;
	boolean[] selectedDaysOfWeek;
	ArrayList<Boolean> selected;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_entry);
		

	    res = getResources();
	    entryArray = new ArrayList<String>();
        extras = getIntent().getExtras();
        p = extras.getInt("pos");
		String usern = MainActivity.users[p].getName(); 
		userID = Long.parseLong(MainActivity.users[p].getId());
		SelectionAdapter.selections.clear();

	    String[] weekdays = new DateFormatSymbols().getWeekdays();
	    daysOfWeek = new String[] {
	            weekdays[Calendar.MONDAY],
	            weekdays[Calendar.TUESDAY],
	            weekdays[Calendar.WEDNESDAY],
	            weekdays[Calendar.THURSDAY],
	            weekdays[Calendar.FRIDAY],
	            weekdays[Calendar.SATURDAY],
	            weekdays[Calendar.SUNDAY],
	    };
		selected = new ArrayList<Boolean>();
		selected.clear();
		for (int i=0;i<daysOfWeek.length;i++) {
			selected.add(false);
		}
	 
		save = (Button)findViewById(R.id.save_b);
		save.setOnClickListener(new OnClickListener() {
			public void onClick (View v) {
				if (selectedDays != null) {
					myDaysBooleans = selectedDays.toString();
				} else {
					if (extras.getString("days") != null) {
						myDaysBooleans = extras.getString("days");
					} else {
						myDaysBooleans = "false,false,false,false,false,false,false,";
					}
				}
				if (users == null) {
					if (extras.getString("mentions") == null) {
						users = "";
					} else {
						users = extras.getString("mentions");
					}
				}
				if (totalC<140) {
					if (et1.getText().toString().length() != 0 && et2.getText().toString().length() != 0 && et3.getText().toString().length() != 0) {
					   final DBAdapter db = new DBAdapter(v.getContext());
			       	   db.open();
			           if (!extras.getBoolean("editing", false)) {
				       		db.insertEntry(MainActivity.users[p].getName(), et1.getText().toString(), et2.getText().toString(), et3.getText().toString(), myDaysBooleans, users);
			           } else {
			        	   db.updateEntry(MainActivity.users[p].getName(), et1.getText().toString(), users, extras.getString("message"), et2.getText().toString(), et3.getText().toString(), myDaysBooleans);
			           }
			       	   db.close();
			           finish();
					} else {
						Toast.makeText(v.getContext(), "Do not leave any fields blank.", 99999).show();
					}
				} else {
					new AlertDialog.Builder(v.getContext())
					.setTitle("Yikes!")
                    .setMessage("The Twitter character limit is 140.\nYou are "+Integer.toString(totalC-140)+" over the limit.")
                    .setPositiveButton("Whoops!",
                            new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            	dialog.dismiss();
                            }
                    })
                    .setCancelable(false)
                    .create().show();					
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
		mPreview = (TextView)findViewById(R.id.mentions_pre);
		dPreview = (TextView)findViewById(R.id.day_pre);
		tV2 = (TextView)findViewById(R.id.mentionsTv);
		tV2 = (TextView)findViewById(R.id.mentionsTv);
		tV2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				if (extras.getBoolean("editing", false)) {
					Intent i = new Intent(v.getContext(), MentionsActivity.class);
					Bundle b = new Bundle();
					b.putInt("pos", p);
					b.putLong("id", userID);
					b.putString("user", name.getText().toString().replace("@", ""));
					b.putString("users", extras.getString("mentions"));
					i.putExtras(b);
					startActivityForResult(i, 0);					
				} else {
					Intent i = new Intent(v.getContext(), MentionsActivity.class);
					Bundle b = new Bundle();
					b.putInt("pos", p);
					b.putLong("id", userID);
					b.putString("user", name.getText().toString().replace("@", ""));
					i.putExtras(b);
					startActivityForResult(i, 0);
				}
			}
		});
		myCount = (TextView)findViewById(R.id.myCount);
		if (extras.getBoolean("editing", false)) {
			incomingT = extras.getString("mentions").replaceAll("@", "").length();
		}
		myCount.setText("0");
		tV = (TextView)findViewById(R.id.dayTv);
		tV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedDays = new StringBuilder();
				new AlertDialog.Builder(v.getContext())
				      .setTitle("Days of week")
				      .setCancelable(true)
				      .setMultiChoiceItems(daysOfWeek, selectedDaysOfWeek, new OnMultiChoiceClickListener() {
				    	  public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				    		  if (isChecked) {
				    			  ArrayUtils.add(selectedDaysOfWeek, which, true);
				    		  }
				    	  }
				      })
				      .setPositiveButton("OK",new DialogInterface.OnClickListener() {
				    	  public void onClick(DialogInterface dialog, int whichButton){		
				    		  StringBuilder dPre = new StringBuilder();
				    		  for (int i=0;i<selectedDaysOfWeek.length;i++) {
				    			  if (selectedDaysOfWeek[i]) {
				    				  selectedDays.append("true,");
			    	        		  dPre.append(daysOfWeek[i]+",");
				    			  } else {
				    				  selectedDays.append("false,");
				    			  }
				    		  }
				    		  if (dPre.toString().length()>2) {
				    			  dPreview.setText(dPre.toString());
				    		  } else {
				    			  dPreview.setText("No days selected.");
				    		  }
				    		  dialog.dismiss();
				    	  }
				      })
				      .show();
			}
		});
		et1 = (EditText)findViewById(R.id.editMessage);
	    et1.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				totalC = mLength + s.length() + incomingT;
				myCount.setText(String.valueOf(totalC));
				if (totalC>140) {
					myCount.setTextColor(Color.RED);
				} else {
					myCount.setTextColor(getResources().getColor(R.color.ics));
				}
			}	    	
	    });
        if (extras.getBoolean("editing", false)) {
        	et1.setText(extras.getString("message"));
        	String[] days = extras.getString("days").split(",");
        	selectedDaysOfWeek = new boolean[] {
        			Boolean.parseBoolean(days[0]),
        			Boolean.parseBoolean(days[1]),
        			Boolean.parseBoolean(days[2]),
        			Boolean.parseBoolean(days[3]),
        			Boolean.parseBoolean(days[4]),
        			Boolean.parseBoolean(days[5]),
        			Boolean.parseBoolean(days[6]),
        	};
        	StringBuilder dPre = new StringBuilder();
        	for (int i=0;i<selectedDaysOfWeek.length;i++) {
        		if (selectedDaysOfWeek[i]) {
        			dPre.append(daysOfWeek[i]+", ");
        		}
        	}
        	if (extras.getString("mentions").replaceAll("-", ", ").length()>2) {
        		mPreview.setText(extras.getString("mentions").replaceAll("-", ", ")); 
        	}
        	if (dPre.toString().length()>6) {
        		dPreview.setText(dPre.toString());
        	}
        } else {
    	    selectedDaysOfWeek = new boolean[] {
    	            false,
    	            false,
    	            false,
    	            false,
    	            false,
    	            false,
    	            false,
    	    };
        }
		et2 = (EditText)findViewById(R.id.editAmount);
        if (extras.getBoolean("editing", false)) {
        	et2.setText(extras.getString("sends"));
        }
        et3 = (EditText)findViewById(R.id.editInterval);
        if (extras.getBoolean("editing", false)) {
        	et3.setText(extras.getString("interval"));
        }		
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	    case 0:
	    	StringBuilder s = new StringBuilder();
	    	mLength = MentionsActivity.count;
	    	totalC = mLength+et1.getText().length();
			myCount.setText(String.valueOf(totalC));
			if (totalC>140) {
				myCount.setTextColor(Color.RED);
			} else {
				myCount.setTextColor(getResources().getColor(R.color.ics));
			}
			
			for (int i=0;i<MentionsActivity.users.size();i++) {
				s.append(MentionsActivity.users.get(i));
				s.append(" ");
			}
			users = s.toString();
			if (users.length()>2) {
	    		mPreview.setText(users);
			} else {
	    		mPreview.setText("No mentions");
			}
	        break;
	    default:
	        break;
	    }
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
