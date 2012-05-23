package com.t3hh4xx0r.tweezee.twitter;

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
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.primitives.Longs;
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.TweezeeReceiver;

public class EntryAdd extends Activity {

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

	static final int ID_TIMEPICKER = 0;
	private int hour, minute;
	
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

		SelectionAdapter.selections.clear();

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
					Log.d("USER", Long.toString(friendsID[i]));
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
					names.add(u.getScreenName());
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
//			
//			for (int i=0;i<MentionsActivity.users.size();i++) {
//				s.append(MentionsActivity.users.get(i));
//				s.append(" ");
//			}
//			users = s.toString();
//			if (users.length()>2) {
//	    		mPreview.setText(users);
//			} else {
//	    		mPreview.setText("No mentions");
//			}
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
	    switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        case R.id.save:
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
				if (et1.getText().toString().length() != 0 && et3.getText().toString().length() != 0 && !time) {
				   final DBAdapter db = new DBAdapter(this);
		       	   db.open();
		           if (!extras.getBoolean("editing", false)) {
		        	    int my_id = getReqID();
			       		db.insertTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), et3.getText().toString(), myDaysBooleans, users, "", Boolean.toString(startBoot), my_id);
					    setupIntervalTweet(p,this,usern,et1.getText().toString(),et3.getText().toString(),myDaysBooleans,users, false, null, my_id);	        	
		           } else {
		        	   db.updateTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), users, extras.getString("message"), et3.getText().toString(), myDaysBooleans, "", Boolean.toString(startBoot));
				       setupIntervalTweet(p,this,usern,et1.getText().toString(),et3.getText().toString(),myDaysBooleans,users, true, extras.getString("message"), 420);	        	
		           }
			       db.close();
		           finish();
				} else {
					if (time && timeValue != "" && timeValue != null) {
						   final DBAdapter db = new DBAdapter(this);
				       	   db.open();
				           if (!extras.getBoolean("editing", false)) {
				        	    int my_id = getReqID();
					       		db.insertTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), et3.getText().toString(), myDaysBooleans, users, timeValue, Boolean.toString(startBoot), my_id);
							    setupTimedTweet(p,this,usern,et1.getText().toString(),myDaysBooleans,users, timeValue, false, null, my_id);	        	
				           } else {
				        	   db.updateTEntry(TwitterActivity.users[p].getName(), et1.getText().toString(), users, extras.getString("message"), et3.getText().toString(), myDaysBooleans, timeValue, Boolean.toString(startBoot));
						       setupTimedTweet(p,this,usern,et1.getText().toString(),myDaysBooleans,users, timeValue, true, extras.getString("message"), 420);	        	
				           }
				       	   db.close();
				           finish();
					} else {
						Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
					}
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
			break;
        	default:
	            return super.onOptionsItemSelected(item);
	    }
		return false;
	}

	private void setupIntervalTweet(int position, Context c, String username, String message, String wait, String day, String mentions, boolean updating, String og, int id) {
    	Toast.makeText(c, "New tweet saved, "+message, Toast.LENGTH_LONG).show();
       	final DBAdapter db = new DBAdapter(this);
    	db.open();
    	if (id == 420) {
          	Cursor cu = db.getAllSEntries();
	    	try {
	       		while (cu.moveToNext()) {
	        		if ((cu.getString(cu.getColumnIndex("message")).equals(message)) && cu.getString(cu.getColumnIndex("username")).equals(username)) {
	        			id = Integer.parseInt(cu.getString(cu.getColumnIndex("my_id")));
	        			break;
	        		}
	       		}
        	} catch (Exception e) {}
	    }
    	Intent myIntent = new Intent(c, TweezeeReceiver.class);
    	myIntent.putExtra("type", "tweet");
    	myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("mentions", mentions);
    	myIntent.putExtra("day", day);
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
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Integer.parseInt(wait)*60000, pendingIntent);					
	}

	private void setupTimedTweet(int position, Context c, String username, String message, String day, String mentions, String timeValue, boolean updating, String og, int id) {
    	Toast.makeText(c, "New tweet saved, "+message, Toast.LENGTH_LONG).show();
       	final DBAdapter db = new DBAdapter(this);
    	db.open();
    	if (id == 420) {
          	Cursor cu = db.getAllSEntries();
	    	try {
	       		while (cu.moveToNext()) {
	        		if ((cu.getString(cu.getColumnIndex("message")).equals(message)) && cu.getString(cu.getColumnIndex("username")).equals(username)) {
	        			id = Integer.parseInt(cu.getString(cu.getColumnIndex("my_id")));
	        			break;
	        		}
	       		}
        	} catch (Exception e) {}
	    }  	
        Intent myIntent = new Intent(c, TweezeeReceiver.class);        
        myIntent.setAction(Integer.toString(id));
        myIntent.setData(Uri.parse(Integer.toString(id)));   
    	myIntent.putExtra("type", "tweet");
    	myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message);
    	myIntent.putExtra("mentions", mentions);
    	myIntent.putExtra("day", day);        
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
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);					
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
	    default:
	    	return null;
	    }
	}
	  
	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener(){	  
		  @Override
		  public void onTimeSet(android.widget.TimePicker arg0, int hour, int min) {			  
			  timeValue = String.valueOf(hour) + ":" + String.valueOf(min);
			  timePre.setText(timeValue);
		  }
	};
}
