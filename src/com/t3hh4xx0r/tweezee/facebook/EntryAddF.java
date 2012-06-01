package com.t3hh4xx0r.tweezee.facebook;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;

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
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.TweezeeReceiver;

public class EntryAddF extends SherlockActivity {

	TextView timePre;
	TextView dayPre;
	TextView dayPickerTV;
	TextView intervalTV;
	TextView timePicker;
	TextView datePre;
	TextView datePickerTV;
	CheckBox timeCB;
	CheckBox bootCB;
	CheckBox dateCB;
	EditText intervalET;
	EditText messageET;
	StringBuilder selectedDays;
	String myDaysBooleans;
	boolean[] selectedDaysOfWeek;
	boolean time = false;
	boolean date;
	boolean startBoot;
	ArrayList<Boolean> selected;
	String[] daysOfWeek;
	static final int ID_TIMEPICKER = 0;
	static final int ID_DATEPICKER = 1;
	private int hour, minute, month, day, year;
	String timeValue = "";
	String dateValue = "";
	Bundle extras;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.entry_add_facebook);
        extras = getIntent().getExtras();
		messageET = (EditText) findViewById(R.id.messageET);
		
		timePre = (TextView)findViewById(R.id.time_pre);
		timePre.setVisibility(View.GONE);
		dayPre = (TextView)findViewById(R.id.day_pre);

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
		checkExtras();
	}

	private void checkExtras() {
	       if (extras.getBoolean("editing", false)) {
	        	startBoot = Boolean.parseBoolean(extras.getString("boot"));
	        	bootCB.setChecked(startBoot);
				messageET.setText(extras.getString("message"));
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
			if (messageET.getText().toString().length() != 0) {
				if (time) {
					if (timeValue == null || timeValue.equals("")) {
						Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
					} else {
						if (date) {
							if (dateValue == null || dateValue.equals("")) {
								Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();								
							} else{
								if (!extras.getBoolean("editing", false)) {
						       		db.insertFEntry(messageET.getText().toString(), intervalET.getText().toString(), "false,false,false,false,false,false,false,",  timeValue, Boolean.toString(startBoot), my_id, dateValue);
								    setupTimedPost(this,messageET.getText().toString(),myDaysBooleans, timeValue, false, my_id, dateValue);	        	
								    finish();
							    } else {
					        	    db.updateFEntry(messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans,  timeValue, Boolean.toString(startBoot), dateValue);
								    setupTimedPost(this,messageET.getText().toString(),myDaysBooleans, timeValue, true, 420, dateValue);	        	
								    finish();
							    }
							}
						} else {
							if (!extras.getBoolean("editing", false)) {
					       		db.insertFEntry(messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans,  timeValue, Boolean.toString(startBoot), my_id, "");
							    setupTimedPost(this,messageET.getText().toString(),myDaysBooleans, timeValue, false, my_id, null);	        	
							    finish();
							} else {
				        	    db.updateFEntry(messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans,  timeValue, Boolean.toString(startBoot), "");
							    setupTimedPost(this,messageET.getText().toString(),myDaysBooleans, timeValue, true, 420, null);	        	
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
								db.insertFEntry(messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans,  "", Boolean.toString(startBoot), my_id, dateValue);
								setupIntervalPost(this,messageET.getText().toString(),intervalET.getText().toString(),myDaysBooleans, false, my_id, dateValue);	        	
								finish();
							} else {
				        	    db.updateFEntry(messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans,  "", Boolean.toString(startBoot), dateValue);
					        	setupIntervalPost(this,messageET.getText().toString(),intervalET.getText().toString(),myDaysBooleans, true, 420, dateValue);	        	
					        	finish();
				           }
						} else {
							if (!extras.getBoolean("editing", false)) {
								db.insertFEntry(messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans, "", Boolean.toString(startBoot), my_id, "");
					        	setupIntervalPost(this,messageET.getText().toString(),intervalET.getText().toString(),myDaysBooleans, false, my_id, null);	        	
					        	finish();
							} else {
				        	    db.updateFEntry(messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans, "", Boolean.toString(startBoot), "");
								setupIntervalPost(this,messageET.getText().toString(),intervalET.getText().toString(),myDaysBooleans, true, 420, null);	        	
						      	finish();
					        }	
						}
					}
				}
			} else {
				Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
			}
	       	return true;
	    }
		return false;
	}
	
	private void setupIntervalPost(Context c, String message, String wait, String day, boolean updating, int id, String date) {
    	Toast.makeText(c, "New facebook post saved, "+message, Toast.LENGTH_LONG).show();
    	final DBAdapter db = new DBAdapter(this);
    	db.open();
    	if (id == 420) {
          	Cursor cu = db.getAllFEntries();
	    	try {
	       		while (cu.moveToNext()) {
	        		if ((cu.getString(cu.getColumnIndex("message")).equals(message))) {
	        			id = Integer.parseInt(cu.getString(cu.getColumnIndex("my_id")));
	        			break;
	        		}
	       		}
        	} catch (Exception e) {}
	    	cu.close();
	    }
        Intent myIntent = new Intent(c, TweezeeReceiver.class);
    	myIntent.putExtra("type", "facebook");
    	myIntent.putExtra("message", message);
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
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getDefault());
        if (date != null) {
        	calendar.set(Calendar.MONTH, Integer.parseInt(date.split("-")[0])-1);
        	calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.split("-")[1]));
        	calendar.set(Calendar.HOUR, 0);
        	calendar.set(Calendar.MINUTE, 0);  
        	calendar.set(Calendar.SECOND, 0);
        	calendar.set(Calendar.MILLISECOND, 0);
        } 
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Integer.parseInt(wait)*60000, pendingIntent);					
        db.updateActiveF(Integer.toString(id), true);
        db.close();
	}

	private int getReqID() {
		String id = Long.toString(System.currentTimeMillis());
		String my_id = new String(id.substring(5, id.length()-1));
		return Integer.parseInt(my_id);
	}

	private void setupTimedPost(Context c, String message, String day, String timeValue, boolean updating, int id, String date) {
    	Toast.makeText(c, "New facebook post saved, "+message, Toast.LENGTH_LONG).show();
       	final DBAdapter db = new DBAdapter(this);
    	db.open();
    	if (id == 420) {
          	Cursor cu = db.getAllFEntries();
	    	try {
	       		while (cu.moveToNext()) {
	        		if ((cu.getString(cu.getColumnIndex("message")).equals(message))) {
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
    	myIntent.putExtra("type", "facebook");
    	myIntent.putExtra("message", message);
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
        db.updateActiveF(Integer.toString(id), true);
        db.close();
	}
}
