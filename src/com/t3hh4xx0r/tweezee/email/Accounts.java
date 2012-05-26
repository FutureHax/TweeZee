package com.t3hh4xx0r.tweezee.email;


public class Accounts {
	
	    private String username;
	    private String password;


	    public Accounts(){
	        this.password = "";
	        this.username = "";

	    }

	    public void setName(String name){
	        this.username = name;
	    }

	    public String getName(){
	        return this.username;
	    }

	    public void setPassword(String password){
	        this.password = password;
	    }

	    public String getPassword(){
	        return this.password;
	    }
	    
}
