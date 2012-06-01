package com.t3hh4xx0r.tweezee.twitter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;

import twitter4j.ProfileImage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.common.primitives.Longs;
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.TweezeeReceiver;

public class EntryAdd extends SherlockActivity {

	EditText et1;
	EditText et3;
	TextView tV;
	TextView myCount;
	TextView tV2;
	MultiAutoCompleteTextView mPreview;
	TextView dPreview;
	TextView timePre;
	TextView name;
	TextView timePicker;
	TextView intervalTV;
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
	boolean time = false;
	boolean startBoot;
	ArrayList<Boolean> selected;
	String usern;
	String timeValue = "";
	CheckBox timeCB;
	CheckBox bootCB;
	SharedPreferences prefs;
	AccessToken aToken;
	TextView datePre;
	TextView datePickerTV;
	CheckBox dateCB;
	boolean date;
	static final int ID_TIMEPICKER = 0;
	static final int ID_DATEPICKER = 1;
	private int hour, minute, month, day, year;
	String dateValue = "";

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_entry);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

	    res = getResources();
	    entryArray = new ArrayList<String>();
        extras = getIntent().getExtras();
        p = extras.getInt("pos");
        startBoot = Boolean.parseBoolean(extras.getString("boot", "false"));
		usern = TwitterActivity.users[p].getName(); 
		userID = Long.parseLong(TwitterActivity.users[p].getId());

		timePre = (TextView)findViewById(R.id.time_pre);
		intervalTV = (TextView)findViewById(R.id.interval);
	    String[] weekdays = new DateFormatSymbols().getWeekdays();
	    daysOfWeek = new String[] {
	            weekdays[Calendar.SUNDAY],
	            weekdays[Calendar.MONDAY],
	            weekdays[Calendar.TUESDAY],
	            weekdays[Calendar.WEDNESDAY],
	            weekdays[Calendar.THURSDAY],
	            weekdays[Calendar.FRIDAY],
	            weekdays[Calendar.SATURDAY],
	    };
		selected = new ArrayList<Boolean>();
		selected.clear();
		for (int i=0;i<daysOfWeek.length;i++) {
			selected.add(false);
		}
		datePickerTV = (TextView) findViewById(R.id.dateTv);
		datePickerTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Calendar c = Calendar.getInstance();
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
				year = c.get(Calendar.YEAR);
				showDialog(ID_DATEPICKER);
			}
		});
		datePre = (TextView) findViewById(R.id.date_pre);
		datePre.setVisibility(View.GONE);	 
		timePicker = (TextView)findViewById(R.id.timePicker);
		timePicker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    final Calendar c = Calendar.getInstance();
			       hour = c.get(Calendar.HOUR_OF_DAY);
			       minute = c.get(Calendar.MINUTE);
			       showDialog(ID_TIMEPICKER);
			}
		});
		
		timeCB = (CheckBox)findViewById(R.id.timeCB);
		timeCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					time = true;
					et3.setVisibility(View.GONE);
					timePicker.setVisibility(View.VISIBLE);
					timePre.setVisibility(View.VISIBLE);
					intervalTV.setVisibility(View.GONE);
					et3.setText("");
					timePre.setText(timeValue);
				} else {
					time = false;
					et3.setVisibility(View.VISIBLE);
					timePicker.setVisibility(View.GONE);
					timePre.setVisibility(View.GONE);
					intervalTV.setVisibility(View.VISIBLE);
					timePre.setText("No time set.");
				}
			}			
		});
		dateCB = (CheckBox)findViewById(R.id.dateCB);
		dateCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					date = true;
					datePickerTV.setVisibility(View.VISIBLE);
					datePre.setVisibility(View.VISIBLE);					
					tV.setVisibility(View.GONE);
					dPreview.setVisibility(View.GONE);
					datePre.setText(dateValue);
				} else {
					date = false;
					tV.setVisibility(View.VISIBLE);
					dPreview.setVisibility(View.VISIBLE);
					datePickerTV.setVisibility(View.GONE);
					datePre.setVisibility(View.GONE);
					datePre.setText("No date set.");
				}
			}			
		});
		bootCB = (CheckBox)findViewById(R.id.bootCB);
		bootCB.setChecked(startBoot);
		bootCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					startBoot = true;
				} else {
					startBoot = false;
				}
			}			
		});
		
		name = (TextView)findViewById(R.id.userN);
		name.setText("@"+usern);
		pic = (ImageView)findViewById(R.id.userP);
		mPreview = (MultiAutoCompleteTextView)findViewById(R.id.mentionsMACTV);
		getFollowing();
		dPreview = (TextView)findViewById(R.id.day_pre);
		tV2 = (TextView)findViewById(R.id.mentionsTv);
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
				totalC = 2 + mLength + s.length() + incomingT;
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
			date = extras.getString("date") != null;
			if (date) {
				dateValue = extras.getString("date");
			} else {
				datePre.setVisibility(View.GONE);
			}
			dateCB.setChecked(date);
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
        et3 = (EditText)findViewById(R.id.editInterval);
		if (extras.getBoolean("editing", false)) {
        	et3.setText(extras.getString("interval"));
        	if (extras.getString("time", "").length()>1) {
        		timeValue = extras.getString("time");
				time = true;
				timePre.setText(timeValue);
			} else {
				time = false;
			}
			timeCB.setChecked(time);
		}
		if (time) {
			et3.setVisibility(View.GONE);
			timePicker.setVisibility(View.VISIBLE);
			timePre.setVisibility(View.VISIBLE);
			intervalTV.setVisibility(View.GONE);			
		} else {
			et3.setVisibility(View.VISIBLE);
			timePicker.setVisibility(View.GONE);
			timePre.setVisibility(View.GONE);			
			intervalTV.setVisibility(View.VISIBLE);
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

	private void getFollowing() {	
	     Twitter t = new TwitterFactory().getInstance();
	     t.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
    	 aToken = getToken();
	     t.setOAuthAccessToken(aToken);
	     ArrayList<String> names = new ArrayList<String>();
	     try {
	    	int start = 0;
	    	int finish = 100;
	    	ArrayList<Long> IDS = new ArrayList<Long>();
			long[] friendsID =	t.getFriendsIDs(userID, -1).getIDs();
			boolean check = true;
			while (check) {
				for (int i=start;i<finish;i++) {		
					IDS.add(friendsID[i]);
					if (friendsID.length-1 == i) {
						check = false;
						break;						
					}
				}
				start = start+100;
				finish = finish+100;
				long[] ids = Longs.toArray(IDS);
				ResponseList<User> userName = t.lookupUsers(ids);
				IDS.clear();
				for (User u : userName) {
					names.add("@"+u.getScreenName());
				}
			}
			String[] screenNames = (String[]) names.toArray(new String[names.size()]);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, screenNames);
			mPreview.setAdapter(adapter);
			mPreview.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
	     } catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	private AccessToken getToken() {
		 String token = null;
		 String secret = null;
		 DBAdapter dba = new DBAdapter(this);
     	 dba.open();
     	 Cursor cu = dba.getAllTUsers();
 		 try {
 			while (cu.moveToNext()) {
 				if (cu.getString(1).equals(usern)) {
 					token = cu.getString(3);
 					secret = cu.getString(4);
 				}
 			}
 		 } catch (Exception e) {
 			 e.printStackTrace();
 		 }
 		 cu.close();
 		 dba.close();
 		 
 		 try {
 			 return new AccessToken(token, secret);
 		 } catch (Exception e) {
 			 e.printStackTrace();
 			 return null; 
 		 }
	}
	  public Drawable setProfilePic(String name){
			Resources r = getResources();
			File file = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/ultimate_scheduler/profileimages/twitter_"+ name + "_image_small.jpg");
			File dir = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/ultimate_scheduler/profileimages/");
			Drawable d;
			if (!file.exists()) {
				if (!dir.exists()) {
					dir.mkdirs();
				}	
				try {
		           Twitter twitter = new TwitterFactory().getInstance();
		           ProfileImage image = twitter.getProfileImage(name, ProfileImage.BIGGER);
		           URL src = new URL(image.getURL());
	
		           Bitmap bm = BitmapFactory.decodeStream(src.openConnection().getInputStream());
		           bm = Bitmap.createScaledBitmap(bm, 150, 150, true); 
		           bm.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
		           d = new BitmapDrawable(bm);
				} catch (Exception e) {
					e.printStackTrace();
					d = r.getDrawable(R.drawable.acct_sel);
				}
			} else {
				Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
				d = new BitmapDrawable(bitmap);
			}
			return d;
	}	 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	    case 0:
	    	mLength = dPreview.getText().length();
	    	totalC = mLength+et1.getText().length();
			myCount.setText(String.valueOf(totalC));
			if (totalC>140) {
				myCount.setTextColor(Color.RED);
			} else {
				myCount.setTextColor(getResources().getColor(R.color.ics));
			}
	        break;
	    default:
	        break;
	    }
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.add_menu, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
		} else if (item.getItemId() == R.id.save) {
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
				users = mPreview.getText().toString();
			}
			final DBAdapter db = new DBAdapter(this);
	       	db.open();
    	    int my_id = getReqID();
			if (totalC<140) {		       	
				if (et1.getText().toString().length() != 0) {
					if (time) {
						if (timeValue == null || timeValue.equals("")) {
							Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
						} else {
							if (date) {
								if (dateValue == null || dateValue.equals("")) {
									Toast.makeText(this, "Do not leave any fields blank. dateValue", Toast.LENGTH_LONG).show();								
								} else{
									if (!extras.getBoolean("editing", false)) {
							       		db.insertTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), et3.getText().toString(), "false,false,false,false,false,false,false,", users, timeValue, Boolean.toString(startBoot), my_id, dateValue);
							       		setupTimedTweet(this,usern,et1.getText().toString(),myDaysBooleans,users, timeValue, false, my_id, dateValue);	        	
									    finish();
								    } else {
								    	db.updateTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), users, extras.getString("message"), et3.getText().toString(), myDaysBooleans, timeValue, Boolean.toString(startBoot), dateValue);
									    setupTimedTweet(this,usern,et1.getText().toString(),myDaysBooleans,users, timeValue, true, 420, dateValue);	        	
									    finish();
								    }
								}
							} else {
								if (!extras.getBoolean("editing", false)) {
						       		db.insertTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), et3.getText().toString(), "false,false,false,false,false,false,false,", users, timeValue, Boolean.toString(startBoot), my_id, "");
								    setupTimedTweet(this,usern,et1.getText().toString(),myDaysBooleans,users, timeValue, false, my_id, null);	        	
								    finish();
								} else {
							    	db.updateTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), users, extras.getString("message"), et3.getText().toString(), myDaysBooleans, timeValue, Boolean.toString(startBoot), "");
								    setupTimedTweet(this,usern,et1.getText().toString(),myDaysBooleans,users, timeValue, true, 420, null);	        	
								    finish();
					           }	
							}
						}
					} else {	
						if (et3.getText().toString().length() == 0) {
							Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
						} else {
							if (date) {
								if (dateValue == null || dateValue.equals("")) {
									Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
								}
								if (!extras.getBoolean("editing", false)) {
						       		db.insertTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), et3.getText().toString(), myDaysBooleans, users, "", Boolean.toString(startBoot), my_id, dateValue);
 								    setupIntervalTweet(this,usern,et1.getText().toString(),et3.getText().toString(),myDaysBooleans,users, false, my_id, dateValue);	        	
									finish();
								} else {
 					        	    db.updateTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), users, extras.getString("message"), et3.getText().toString(), myDaysBooleans, "", Boolean.toString(startBoot), dateValue);
 								    setupIntervalTweet(this,usern,et1.getText().toString(),et3.getText().toString(),myDaysBooleans,users, true, 420, dateValue);	        	
						        	finish();
					           }
							} else {
								if (!extras.getBoolean("editing", false)) {
						       		db.insertTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), et3.getText().toString(), myDaysBooleans, users, "", Boolean.toString(startBoot), my_id, "");
 								    setupIntervalTweet(this,usern,et1.getText().toString(),et3.getText().toString(),myDaysBooleans,users, false, my_id, null);	        	
						        	finish();
								} else {
 					        	    db.updateTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), users, extras.getString("message"), et3.getText().toString(), myDaysBooleans, "", Boolean.toString(startBoot), "");
 								    setupIntervalTweet(this,usern,et1.getText().toString(),et3.getText().toString(),myDaysBooleans,users, true, 420, null);	        	
							      	finish();
						        }	
							}
						}
					}
				} else {
					Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
				}
			} else {
				new AlertDialog.Builder(this)
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
			return true;
	    }
		return false;
	}

	private void setupIntervalTweet(Context c, String username, String message, String wait, String day, String mentions, boolean updating, int id, String date) {
    	Toast.makeText(c, "New tweet saved, "+message, Toast.LENGTH_LONG).show();
       	final DBAdapter db = new DBAdapter(this);
    	db.open();
    	Cursor cu = null;
    	if (id == 420) {
          	cu = db.getAllTEntries();
	    	try {
	       		while (cu.moveToNext()) {
	        		if ((cu.getString(cu.getColumnIndex("message")).equals(message)) && cu.getString(cu.getColumnIndex("username")).equals(username)) {
	        			id = Integer.parseInt(cu.getString(cu.getColumnIndex("my_id")));
	        			break;
	        		}
	       		}
        	} catch (Exception e) {}
	    	cu.close();
    	}
    	Intent myIntent = new Intent(c, TweezeeReceiver.class);
    	myIntent.putExtra("type", "tweet");
    	myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("mentions", mentions);
    	myIntent.putExtra("day", day);
    	if (date != null) {
    		myIntent.putExtra("dated", true);
    	}
    	myIntent.setAction(Integer.toString(id));
        myIntent.setData(Uri.parse(Integer.toString(id)));   
        PendingIntent pendingIntent;
        if (updating) {
        	pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, 0);
        }        
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (date != null) {
        	calendar.set(Calendar.MONTH, Integer.parseInt(date.split("-")[0])-1);
        	calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.split("-")[1]));
        	calendar.set(Calendar.HOUR, 0);
        	calendar.set(Calendar.MINUTE, 0);        	
        	calendar.set(Calendar.SECOND, 0);
        	calendar.set(Calendar.MILLISECOND, 0);
        } 
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Integer.parseInt(wait)*60000, pendingIntent);					
        db.updateActiveT(username, message, true);
    	db.close();
	}

	private void setupTimedTweet(Context c, String username, String message, String day, String mentions, String timeValue, boolean updating, int id, String date) {
    	Toast.makeText(c, "New tweet saved, "+message, Toast.LENGTH_LONG).show();
       	final DBAdapter db = new DBAdapter(this);
    	db.open();
    	Cursor cu = null;
    	if (id == 420) {
          	cu = db.getAllTEntries();
	    	try {
	       		while (cu.moveToNext()) {
	        		if ((cu.getString(cu.getColumnIndex("message")).equals(message)) && cu.getString(cu.getColumnIndex("username")).equals(username)) {
	        			id = Integer.parseInt(cu.getString(cu.getColumnIndex("my_id")));
	        			break;
	        		}
	       		}
        	} catch (Exception e) {}
	    	cu.close();
    	}  	
        Intent myIntent = new Intent(c, TweezeeReceiver.class);        
        myIntent.setAction(Integer.toString(id));
        myIntent.setData(Uri.parse(Integer.toString(id)));   
    	myIntent.putExtra("type", "tweet");
    	myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("mentions", mentions);
    	myIntent.putExtra("day", day);        
    	if (date != null) {
    		myIntent.putExtra("dated", true);
    	}
    	PendingIntent pendingIntent;
        if (updating) {
        	pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(c, id, myIntent, 0);
        }
        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeValue.split(":")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeValue.split(":")[1]));
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	if (date != null) {
        	calendar.set(Calendar.MONTH, Integer.parseInt(date.split("-")[0])-1);
        	calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.split("-")[1]));
        } 
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);					
        db.updateActiveT(username, message, true);
    	db.close();
	}
	
	private int getReqID() {
		String id = Long.toString(System.currentTimeMillis());
		String my_id = new String(id.substring(5, id.length()-1));
		return Integer.parseInt(my_id);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
	    case ID_TIMEPICKER:
	    	return new TimePickerDialog(this, timeSetListener, hour, minute, false); 
	    case ID_DATEPICKER:	    	
	    	return new DatePickerDialog(this, dateSetListener, year, month, day);
	    default:
	    	return null;	    	
	    }
	}
	
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int Year, int Month,
				int Day) {
			StringBuilder sB = new StringBuilder();
			sB.append(Month+1).append("-");
			sB.append(Day).append("-");
			sB.append(Year);
			dateValue = sB.toString();
			datePre.setText(dateValue);			
		}
	};
	  
	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener(){	  
		  @Override
		  public void onTimeSet(android.widget.TimePicker arg0, int hour, int min) {			  
			  timeValue = String.valueOf(hour) + ":" + String.valueOf(min);
			  timePre.setText(timeValue);
		  }
	};
}
