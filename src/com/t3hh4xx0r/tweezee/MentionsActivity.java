package com.t3hh4xx0r.tweezee;

import java.util.ArrayList;

import com.google.common.primitives.Longs;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MentionsActivity extends Activity {
	String userN;
	Long userID;
	int pos;
	Twitter twitter;
	ListView lv1;
	Button allB;
	ArrayList<SelectionResults> results;
	SelectionAdapter a;
	static SharedPreferences prefs;
	public static int count = 0;
	static ArrayList<String> users;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle extras = getIntent().getExtras();
        userN = extras.getString("user");
        userID = extras.getLong("id");
        pos = extras.getInt("pos");
        
        lv1 = (ListView) findViewById(android.R.id.list);
        allB = (Button) findViewById(R.id.all_button);
        allB.setOnClickListener(new OnClickListener() {
			  public void onClick(View v) {
				  count = 0;
				  users.clear();
				  String[] mentions = SelectionAdapter.selections.toArray(new String[SelectionAdapter.selections.size()]);
				  	for (int i=0;i<mentions.length;i++) {
				  		for (int j=0;j<mentions[i].length();j++) {
				  			count++;
				  		}
				  		if (i!=mentions.length-1) {
				  			count++;
				  		}
				  		users.add("@"+mentions[i]);
				  	}
				  	finish();
			  }
		});
        
        results = new ArrayList<SelectionResults>();
        users = new ArrayList<String>();
        a = new SelectionAdapter(MentionsActivity.this, results);
        lv1.setAdapter(a);
        
        twitter = new TwitterFactory().getInstance();
        AccessToken t = new AccessToken(MainActivity.users[pos].getToken(), MainActivity.users[pos].getSecret());
        twitter.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
        twitter.setOAuthAccessToken(t);
		long[] f = null;
		if (getFriends(userN) == null) { 
			try {
				f = twitter.getFriendsIDs(userID, -1).getIDs();
		        new CreateArrayListTask().execute(f);
			} catch (TwitterException e) {
				notifyAPI(false);
			}
		} else {
			int fCount = 0;
			try {
				fCount = twitter.getFriendsIDs(userID, -1).getIDs().length;
			} catch (TwitterException e) {
				notifyAPI(true);
			}		
			if (fCount != 0) { 
				ArrayList<Long> longArray = new ArrayList<Long>(); 
				if (fCount == getFriends(userN).size()) {							
					for (int i=0;i<getFriends(userN).size();i++) {
					      SelectionResults resultsArray =  new SelectionResults();
					      resultsArray.setFile(getFriends(userN).get(i));
					      results.add(resultsArray);
					      a.notifyDataSetChanged();
					}
				} else {
					try {
						f = twitter.getFriendsIDs(userID, -1).getIDs();
						for (int i=0;i<f.length;i++) {
							longArray.add(f[i]);
						}
					} catch (TwitterException e) {
						e.printStackTrace();
					}
					for (int i=0;i<getFriends(userN).size();i++) {
					      SelectionResults resultsArray =  new SelectionResults();
					      resultsArray.setFile(getFriends(userN).get(i));
					      results.add(resultsArray);
					      a.notifyDataSetChanged();
					      longArray.remove(0);
					}
			        new CreateArrayListTask().execute(Longs.toArray(longArray));	
				}
			}
		}
    }
    
    public ArrayList<String> getFriends(String u) {  
    	ArrayList<String> fList = new ArrayList<String>();
    	
    	boolean success = false;
		String[] storedFriendsArray = null;
		String d = "-";
        DBAdapter db = new DBAdapter(getBaseContext());
   		db.open();
   		Cursor c = db.getAllUsers();
   		c.moveToFirst();
   		do {
   			if (!c.getString(c.getColumnIndex("username")).equals(u)) {
   				c.moveToNext();
   			} else {
		       	storedFriendsArray = c.getString(5).split(d);
		       	success = true;
   				break;
   			}
   		} while (true);
   		c.close();
   		db.close();    
   		
   		if (success) {
   			for (int i=0;i<storedFriendsArray.length;i++) {
   				fList.add(storedFriendsArray[i]);   				
   			}
   		} else {
   			return null;
   		}
   		if (fList.size()>1) {
   			return fList;
   		} else {
   			return null;
   		}
    }
    
	public void checkService() {

        if (prefs.getBoolean("notifyAvailable", false)) {
      		Intent intent = new Intent(getBaseContext(), Receiver.class).putExtra("pos", pos);
    		PendingIntent pendingIntent = PendingIntent.getBroadcast(
    				getBaseContext().getApplicationContext(), 234324243, intent, 0);
    		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 90000, pendingIntent);
        } else {
            Intent myIntent = new Intent(getBaseContext(), Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 234324243, myIntent, 0);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
	}
    
    private class CreateArrayListTask extends AsyncTask<long[], String, String> {       

        StringBuilder sB;
        StringBuilder sBI;

        @Override
        protected void onPreExecute() {
			sB = new StringBuilder();
			sBI = new StringBuilder();
        }
        
        @Override
        protected void onProgressUpdate(String... params) {
        	if (params[0].equals("shit")) {
        		a.notifyDataSetChanged();
        	} else if (params[0].equals("notify")) {
	        	notifyAPI(true);
        	}
        }
        
		@Override
		protected String doInBackground(long[]... params ) {
			if (params != null) {
				String d = "-";	       		       		
				for (int i=0;i<params[0].length;i++) {
					SelectionResults resultsArray =  new SelectionResults();
					String u = null;
					try {
						u = twitter.showUser(params[0][i]).getScreenName();
						sB.append(u+d);
						sBI.append(params[0][i]+d);
					} catch (TwitterException e) {
						publishProgress("notify");
						break;
					}

					resultsArray.setFile(u);
					results.add(resultsArray);
					publishProgress("shit");
				}
			}			
			return userN;
		}
		
		@Override 
		protected void onPostExecute(String user) {
			StringBuilder f = new StringBuilder();
			StringBuilder i = new StringBuilder();
			
	        DBAdapter db = new DBAdapter(getBaseContext());
       		db.open();
       		Cursor c = db.getAllUsers();
       		c.moveToFirst();
       		do {
       			if (!c.getString(c.getColumnIndex("username")).equals(userN)) {
       				c.moveToNext();
       			} else {
       				f.append(c.getString(5));
       				i.append(c.getString(6));
       				break;
       			}
       		} while (true);
       		c.close();
       		f.append(sB.toString());
       		i.append(sBI.toString());
	       	db.addFriends(user, f.toString());
	       	db.addFriendIds(user, i.toString());	
       		db.close();     
		}
    }
    
    public void notifyAPI(final boolean partial) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Yikes!");
	    builder.setMessage("Our request was denied.\nMaybe you hit the hourly API limit.\n\nWould you like a notification when the app will be available again?")
			   .setCancelable(false)
			   .setPositiveButton("Yes please!", new DialogInterface.OnClickListener() {
			       public void onClick(DialogInterface dialog, int id) {
			    	   Editor e = prefs.edit();
			    	   e.putBoolean("notifyAvailable", true);
			    	   e.commit();
			    	   checkService();
			    	   dialog.dismiss();
			    	   		if (!partial) {
			    	   			finish();
			    	   		}	
			       }
			   })
			   .setNegativeButton("No thank you.", new DialogInterface.OnClickListener() {
			       public void onClick(DialogInterface dialog, int id) {
	  		    	Editor e = prefs.edit();
	  		    	e.putBoolean("notifyAvailable", false);
	  		    	e.commit();
	  		    	checkService();
	  		    	dialog.dismiss();
			       }
			   });
			AlertDialog alert = builder.create();
			alert.show();
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
