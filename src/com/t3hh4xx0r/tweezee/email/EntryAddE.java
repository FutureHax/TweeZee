package com.t3hh4xx0r.tweezee.email;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.Encryption;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.TweezeeReceiver;

public class EntryAddE extends Activity {
	MultiAutoCompleteTextView recipientsMACTV;
	public ArrayList<String> contacts = new ArrayList<String>();
	TextView name;
	String usern;
	String usernE;
	TextView subjectTV;
	EditText subjectET;
	int p;
	Bundle extras;
	TextView dayPre;
	TextView dayPickerTV;
	TextView datePre;
	TextView datePickerTV;
	String[] daysOfWeek;
	ArrayList<Boolean> selected;
	StringBuilder selectedDays;
	String myDaysBooleans;
	boolean[] selectedDaysOfWeek;
	TextView timePre;
	boolean date;
	static final int ID_TIMEPICKER = 0;
	static final int ID_DATEPICKER = 1;
	private int hour, minute, month, day, year;
	String timeValue = "";
	TextView timePicker;
	CheckBox timeCB;
	CheckBox dateCB;
	boolean time = false;
	TextView countTV;
	EditText messageET;
	TextView intervalTV;
	EditText intervalET;
	CheckBox bootCB;
	boolean startBoot;
	String pass;
	String dateValue;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_entry_email);
		
		recipientsMACTV = (MultiAutoCompleteTextView) findViewById(R.id.recipientsMACTV);
		getContacts();
		
        extras = getIntent().getExtras();
        p = extras.getInt("pos");
		usern = EmailActivity.accounts[p].getName(); 
		usernE = Encryption.encryptString(usern, Encryption.KEY);
		pass = EmailActivity.accounts[p].getPassword();

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

		
		countTV = (TextView) findViewById(R.id.countTV);
		messageET = (EditText) findViewById(R.id.messageET);
		
		timePre = (TextView)findViewById(R.id.time_pre);
		timePre.setVisibility(View.GONE);
		dayPre = (TextView)findViewById(R.id.day_pre);

		datePickerTV = (TextView) findViewById(R.id.dateTv);
		datePre = (TextView) findViewById(R.id.date_pre);
		datePre.setVisibility(View.GONE);
		subjectTV = (TextView) findViewById(R.id.subject);
		subjectET = (EditText) findViewById(R.id.editSubject);
		
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
	 
		intervalET = (EditText) findViewById(R.id.editInterval);
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
		
		timeCB = (CheckBox)findViewById(R.id.timeCB);
		timeCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					time = true;
					intervalET.setVisibility(View.GONE);
					timePicker.setVisibility(View.VISIBLE);
					timePre.setVisibility(View.VISIBLE);
					intervalTV.setVisibility(View.GONE);
					intervalET.setText("");
					timePre.setText(timeValue);
				} else {
					time = false;
					intervalET.setVisibility(View.VISIBLE);
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
					dayPickerTV.setVisibility(View.GONE);
					dayPre.setVisibility(View.GONE);
					datePre.setText(dateValue);
				} else {
					date = false;
					dayPickerTV.setVisibility(View.VISIBLE);
					dayPre.setVisibility(View.VISIBLE);
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

		dayPickerTV = (TextView)findViewById(R.id.dayTv);
		dayPickerTV.setOnClickListener(new OnClickListener() {
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
				    			  dayPre.setText(dPre.toString());
				    		  } else {
				    			  dayPre.setText("No days selected.");
				    		  }
				    		  dialog.dismiss();
				    	  }
				      })
				      .show();
			}
		});
		messageET = (EditText)findViewById(R.id.editMessage);
		checkExtras();		
	}

	private void checkExtras() {
	       if (extras.getBoolean("editing", false)) {
	    	    
	        	startBoot = Boolean.parseBoolean(extras.getString("boot"));
	        	bootCB.setChecked(startBoot);
	        	recipientsMACTV.setText(extras.getString("recipient"));
				messageET.setText(extras.getString("message"));
				subjectET.setText(extras.getString("subject"));
				time = extras.getString("time") != null;
				date = extras.getString("date") != null;
				intervalET.setText(extras.getString("interval"));
				if (date) {
					dateValue = extras.getString("date");
				} else {
					datePre.setVisibility(View.GONE);
				}
				dateCB.setChecked(date);
				if (time) {
					timeValue = extras.getString("time");
				} else {
					timePre.setVisibility(View.GONE);
				}
				timeCB.setChecked(time);
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
            	if (dPre.toString().length()>6) {
            		dayPre.setText(dPre.toString());
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
	}
	
	private void getContacts() {
		String cEmail = null;
		Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);
		while (c.moveToNext()) {
			cEmail = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));			
			contacts.add(cEmail);
		}
		String[] contactString = (String[]) contacts.toArray(new String[contacts.size()]);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, contactString);
		recipientsMACTV.setAdapter(adapter);
		recipientsMACTV.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
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

	private int getReqID() {
		String id = Long.toString(System.currentTimeMillis());
		String my_id = new String(id.substring(5, id.length()-1));
		return Integer.parseInt(my_id);
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
     	    int my_id = getReqID();
			if (selectedDays != null) {
				myDaysBooleans = selectedDays.toString();
			} else {
				if (extras.getString("days") != null) {
					myDaysBooleans = extras.getString("days");
				} else {
					myDaysBooleans = "false,false,false,false,false,false,false,";
				}
			}
			final DBAdapter db = new DBAdapter(this);
	       	db.open();
			if (subjectET.getText().toString().length() !=0 && messageET.getText().toString().length() != 0 && recipientsMACTV.getText().toString().length() != 0) {
				if (time) {
					if (timeValue == null || timeValue.equals("")) {
						Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
					} else {
						if (date) {
							if (dateValue == null || dateValue.equals("")) {
								Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();								
							} else{
								if (!extras.getBoolean("editing", false)) {
								   db.insertEEntry(usernE, subjectET.getText().toString(), messageET.getText().toString(), intervalET.getText().toString(), "false,false,false,false,false,false,false,", recipientsMACTV.getText().toString(), timeValue, dateValue, Boolean.toString(startBoot), my_id);
								   setupTimedEMail(this, usernE, pass, subjectET.getText().toString(), messageET.getText().toString(),myDaysBooleans, recipientsMACTV.getText().toString(), timeValue, false, my_id, dateValue);	        	
						      	   finish();
								} else {
					        	   db.updateEEntry(usernE, subjectET.getText().toString(), messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), timeValue, Boolean.toString(startBoot), dateValue);
						      	   setupTimedEMail(this, usernE, pass, subjectET.getText().toString(), messageET.getText().toString(),myDaysBooleans, recipientsMACTV.getText().toString(), timeValue, true, 420, dateValue);	        	
						      	   finish();
					           }
							}
						} else {
							if (!extras.getBoolean("editing", false)) {
					        	   db.insertEEntry(usernE, subjectET.getText().toString(), messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans,recipientsMACTV.getText().toString(), timeValue, "", Boolean.toString(startBoot), my_id);
						      	   setupTimedEMail(this, usernE, pass, subjectET.getText().toString(), messageET.getText().toString(),myDaysBooleans, recipientsMACTV.getText().toString(), timeValue, false, my_id, null);	        	
						      	   finish();
								} else {
					        	   db.updateEEntry(usernE, subjectET.getText().toString(), messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), timeValue, Boolean.toString(startBoot), "");
						      	   setupTimedEMail(this, usernE, pass, subjectET.getText().toString(), messageET.getText().toString(),myDaysBooleans, recipientsMACTV.getText().toString(), timeValue, true, 420, null);	        	
						      	   finish();
					           }	
						}
					}
				} else {	
					if (intervalET.getText().toString().equals("")) {
						Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
					} else {
						if (date) {
							if (dateValue == null || dateValue.equals("")) {
								Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
							}
							if (!extras.getBoolean("editing", false)) {
				        	   db.insertEEntry(usernE, subjectET.getText().toString(), messageET.getText().toString(), intervalET.getText().toString(), "false,false,false,false,false,false,false,",recipientsMACTV.getText().toString(), "", dateValue, Boolean.toString(startBoot), my_id);
							   setupIntervalEmail(this, usernE, pass, subjectET.getText().toString(), messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), false, my_id, dateValue);	        	
					      	   finish();
							} else {
				        	   db.updateEEntry(usernE, subjectET.getText().toString(), messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), "", Boolean.toString(startBoot), dateValue);
							   setupIntervalEmail(this, usernE, pass, subjectET.getText().toString(), messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), true, 420, dateValue);	        	
					      	   finish();
				           }
						} else {
							if (!extras.getBoolean("editing", false)) {
					        	   db.insertEEntry(usernE, subjectET.getText().toString(), messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans,recipientsMACTV.getText().toString(), "",  "", Boolean.toString(startBoot), my_id);
								   setupIntervalEmail(this, usernE, pass, subjectET.getText().toString(), messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), false, my_id, null);	        	
						      	   finish();
								} else {
					        	   db.updateEEntry(usernE, subjectET.getText().toString(), messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), "", Boolean.toString(startBoot), "");
								   setupIntervalEmail(this, usernE, pass, subjectET.getText().toString(), messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), true, 420, null);	        	
						      	   finish();
					           }	
						}
					}
				}
			} else {
				Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
			}
			db.close();
			break;
        	default:
	            return super.onOptionsItemSelected(item);
	    }
		return false;
	}
	
	private void setupIntervalEmail(Context c, String username, String pass, String subject, String message, String wait, String day, String recipients, boolean updating, int id, String date) {
    	Toast.makeText(c, "New email saved, "+message, Toast.LENGTH_LONG).show();
       	final DBAdapter db = new DBAdapter(this);
    	db.open();
    	if (id == 420) {
          	Cursor cu = db.getAllEEntries();
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
    	myIntent.putExtra("type", "email");
    	myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message + "\n\n\nSent via UltimateScheduler");
    	myIntent.putExtra("recipient", recipients);
    	myIntent.putExtra("day", day);
    	myIntent.putExtra("pass", pass);
    	myIntent.putExtra("subject", subject);
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
        db.updateActiveE(username, message, true);
        db.close();
	}
	
	private void setupTimedEMail(Context c, String username, String pass, String subject, String message, String day, String recipients, String timeValue, boolean updating, int id, String date) {
    	Toast.makeText(c, "New email saved, "+message, Toast.LENGTH_LONG).show();
       	final DBAdapter db = new DBAdapter(this);
    	db.open();
    	if (id == 420) {
          	Cursor cu = db.getAllEEntries();
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
    	myIntent.putExtra("type", "email");
    	myIntent.putExtra("username", username);
    	myIntent.putExtra("message", message + "\n\n\nSent via UltimateScheduler");
    	myIntent.putExtra("recipient", recipients);
    	myIntent.putExtra("day", day);
    	myIntent.putExtra("pass", pass);
    	myIntent.putExtra("subject", subject);      
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
        db.updateActiveE(username, message, true);
        db.close();
	}	
}
