package com.t3hh4xx0r.tweezee.email;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.Encryption;
import com.t3hh4xx0r.tweezee.R;

public class EmailLogin extends Activity {
	EditText userET;
	EditText passET;
	Button loginB;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.email_login);
		
		userET = (EditText) findViewById(R.id.userET);
		passET = (EditText) findViewById(R.id.passET);
		loginB = (Button) findViewById(R.id.loginB);
		loginB.setOnClickListener(new OnClickListener() {
			@Override 
			public void onClick(final View v) {
				final String encryptedUser = Encryption.encryptString(userET.getText().toString(), Encryption.KEY);
				final String encryptedPass = Encryption.encryptString(passET.getText().toString(), Encryption.KEY);

	           	AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
	      		builder.setTitle("Warning");
	      		builder.setMessage("Please be sure your user/pass combo are correct. No authentication checks will be made until messages are sent.\n\n"+
	      		"Your username and password will be encrypted and stored in a database locally. They will never be used other than to send your messages.")
	      		   .setCancelable(false)
	      		   .setPositiveButton("Ok, I\'m cool with that.", new DialogInterface.OnClickListener() {
	      		       public void onClick(DialogInterface dialog, int id) {
	      		    	   DBAdapter db = new DBAdapter(v.getContext());
	      				   db.open();
	      				   db.insertEUser(encryptedUser, encryptedPass);
	      				   db.close();
	      				   dialog.dismiss();
	      				   finish();	
	      		 		}
	      		   })
	      		.setNegativeButton("Nah, nevermind.", new DialogInterface.OnClickListener() {
	      		       public void onClick(DialogInterface dialog, int id) {
	      		    	   dialog.dismiss();
	      		       }
	      		   });
	      		AlertDialog alert = builder.create();
	      		alert.show();	      			    
			}
		});
	}
}
