package com.t3hh4xx0r.tweezee;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class UserFragment extends Fragment {
	  Context ctx;
	  View v;
	  Button b;
	  int p; 
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    ctx = container.getContext();
		    if (container == null) 
		        return null;

		    if (v != null) 
		        return v;

		    v = inflater.inflate(R.layout.user_fragment, container, false);
		    p = getArguments().getInt("p");
	        b = (Button) v.findViewById(R.id.button1);
	        b.setOnClickListener(new OnClickListener() {
	        	@Override
	        	public void onClick(View v) { 
	                AccessToken aT = new AccessToken(MainActivity.users[p].getToken(), MainActivity.users[p].getSecret());
	        	    Twitter twitter = new TwitterFactory().getInstance();
	        	    twitter.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
	        	    twitter.setOAuthAccessToken(aT);
	        	    Calendar cal = Calendar.getInstance(); 
	        	    Date time = cal.getTime(); 
	        	    DateFormat date = new SimpleDateFormat("HH:mm:ss"); 
	        	    String curTime = date.format(time); 
	        	    try {
						Status status = twitter.updateStatus(curTime+": Hello from TweeZee! You jelly?");
					} catch (TwitterException e) {
		        		Toast.makeText(v.getContext(), "FAILED", Toast.LENGTH_LONG).show();
		        		e.printStackTrace();
					}
	        		Toast.makeText(v.getContext(), MainActivity.users[p].getToken()+" : "+MainActivity.users[p].getSecret()+" : "+MainActivity.users[p].getId()+" : "+MainActivity.users[p].getName(), Toast.LENGTH_LONG).show();
	
	        	}
	        });
		    return v;	   
	   }	    	    
}