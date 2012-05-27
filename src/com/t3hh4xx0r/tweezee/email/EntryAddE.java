package com.t3hh4xx0r.tweezee.email;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.Encryption;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;

public class EntryAddE extends Activity {
	MultiAutoCompleteTextView recipientsMACTV;
	public ArrayList<String> contacts = new ArrayList<String>();
	TextView name;
	String usern;
	String usernE;
	int p;
	Bundle extras;
	TextView dayPre;
	TextView dayPickerTV;
	String[] daysOfWeek;
	ArrayList<Boolean> selected;
	StringBuilder selectedDays;
	String myDaysBooleans;
	boolean[] selectedDaysOfWeek;
	TextView timePre;
	static final int ID_TIMEPICKER = 0;
	private int hour, minute;
	String timeValue = "";
	TextView timePicker;
	CheckBox timeCB;
	boolean time = false;
	TextView countTV;
	EditText messageET;
	TextView intervalTV;
	EditText intervalET;
	CheckBox bootCB;
	boolean startBoot;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_entry_email);
		
		recipientsMACTV = (MultiAutoCompleteTextView) findViewById(R.id.recipientsMACTV);
		getContacts();
		
        extras = getIntent().getExtras();
        p = extras.getInt("pos");
		usern = EmailActivity.accounts[p].getName(); 
		usernE = Encryption.encryptString(usern, Encryption.KEY);

		countTV = (TextView) findViewById(R.id.countTV);
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
				time = extras.getString("time") != null;
				intervalET.setText(extras.getString("interval"));
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
	    default:
	    	return null;
	    }
	}

	private int getReqID() {
		String id = Long.toString(System.currentTimeMillis());
		String my_id = new String(id.substring(5, id.length()-1));
		return Integer.parseInt(my_id);
	}
	
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
			if (selectedDays != null) {
				myDaysBooleans = selectedDays.toString();
			} else {
				if (extras.getString("days") != null) {
					myDaysBooleans = extras.getString("days");
				} else {
					myDaysBooleans = "false,false,false,false,false,false,false,";
				}
			}
			if (messageET.getText().toString().length() != 0 && recipientsMACTV.getText().toString().length() != 0 && !time) {
			   final DBAdapter db = new DBAdapter(this);
	       	   db.open();
	           if (!extras.getBoolean("editing", false)) {
	        	   int my_id = getReqID();
	        	   db.insertEEntry(usernE, messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), "", Boolean.toString(startBoot), my_id);
				   //setupIntervalSMS(this,messageET.getText().toString(),intervalET.getText().toString(),myDaysBooleans,txtPhoneNo.getText().toString(), false, null, my_id);	        	
	           } else {
	        	   db.updateEEntry(usernE, messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), "", Boolean.toString(startBoot));
				   //setupIntervalSMS(this,messageET.getText().toString(),intervalET.getText().toString(),myDaysBooleans,txtPhoneNo.getText().toString(), true, extras.getString("message"), 420);	        	
	           }
		       db.close();
	           finish();
			} else {
				if (time && timeValue != "" && timeValue != null) {
				   final DBAdapter db = new DBAdapter(this);
		       	   db.open();
		           if (!extras.getBoolean("editing", false)) {
		        	   int my_id = getReqID();
			      	   db.insertEEntry(usernE, messageET.getText().toString(), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), timeValue, Boolean.toString(startBoot), my_id);
			           //setupTimedSMS(this,messageET.getText().toString(),myDaysBooleans,txtPhoneNo.getText().toString(), timeValue, false, null, my_id);	        	
				   } else {
				       db.updateEEntry(usernE, messageET.getText().toString(), extras.getString("message"), intervalET.getText().toString(), myDaysBooleans, recipientsMACTV.getText().toString(), timeValue, Boolean.toString(startBoot));
					   //setupTimedSMS(this,messageET.getText().toString(),myDaysBooleans,txtPhoneNo.getText().toString(), timeValue, true, extras.getString("message"), 420);	        	
				   }
				   db.close();
				   finish();
				} else {
					Toast.makeText(this, "Do not leave any fields blank.", Toast.LENGTH_LONG).show();
				}
			}
			break;
        	default:
	            return super.onOptionsItemSelected(item);
	    }
		return false;
	}
}