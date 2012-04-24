package com.t3hh4xx0r.tweezee;

import android.util.Log;

public class RunnableTweets implements Runnable {

	    private String message; 

	    public RunnableTweets(String message){ 
	        this.message = message; 
	    }

	    public void run() { 
	    	Log.d("TWEET", message);
	    } 
	}