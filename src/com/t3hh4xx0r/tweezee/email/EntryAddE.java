package com.t3hh4xx0r.tweezee.email;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.t3hh4xx0r.tweezee.R;

public class EntryAddE extends Activity {
	MultiAutoCompleteTextView recipientsMACTV;
	public ArrayList<String> contacts = new ArrayList<String>();
	TextView name;
	String usern;
	int p;
	Bundle extras;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_entry_email);
		
		recipientsMACTV = (MultiAutoCompleteTextView) findViewById(R.id.recipientsMACTV);
		getContacts();
		
        extras = getIntent().getExtras();
        p = extras.getInt("pos");
		usern = EmailActivity.accounts[p].getName(); 

		name = (TextView)findViewById(R.id.userN);
		name.setText(usern);
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
}
