package com.t3hh4xx0r.tweezee;

import java.util.ArrayList;

public class User {
	
	    private String _userid;
	    private String _username;
	    private String _token;
	    private String _secret;

	    public User(){
	        this._userid = "";
	        this._username = "";
	        this._token = "";
	        this._secret = "";	
	    }
	    
	    public void setId(String id){
	        this._userid = id;
	    }

	    public String getId(){
	        return this._userid;
	    }

	    public void setName(String name){
	        this._username = name;
	    }

	    public String getName(){
	        return this._username;
	    }

	    public void setSecret(String secret){
	        this._secret = secret;
	    }

	    public String getSecret(){
	        return this._secret;
	    }

	    public void setToken(String token){
	        this._token = token;
	    }

	    public String getToken(){
	        return this._token;
	    }
	    
}
