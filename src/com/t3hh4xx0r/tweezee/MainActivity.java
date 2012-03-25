package com.t3hh4xx0r.tweezee;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import twitter4j.ProfileImage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	SharedPreferences p;
	ArrayList<HashMap<String,String>> LIST;
	
    private Spinner mySpinner;
    private SpinAdapter adapter;
    User[] users;
    
    ImageView iV;
    Button b;
    
    public static int user;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

        try {
			new SimpleEula(this).show();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

        iV = (ImageView) findViewById(R.id.profile_image);

        b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) { 
                AccessToken aT = new AccessToken(users[user].getToken(), users[user].getSecret());
        	    Twitter twitter = new TwitterFactory().getInstance();
        	    twitter.setOAuthConsumer(OAUTH.CONSUMER_KEY, OAUTH.CONSUMER_SECRET);
        	    twitter.setOAuthAccessToken(aT);
        	    try {
					Status status = twitter.updateStatus("Hello from TweeZee! You jelly?");
				} catch (TwitterException e) {
	        		Toast.makeText(v.getContext(), "FAILED", Toast.LENGTH_LONG).show();
				}
        		Toast.makeText(v.getContext(), users[user].getToken()+" : "+users[user].getSecret()+" : "+users[user].getId()+" : "+users[user].getName(), Toast.LENGTH_LONG).show();

        	}
        });
        mySpinner = (Spinner) findViewById(R.id.account_spinner);
        mySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                setProfilePic(adapter.getItem(position).getName());
                user = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {            	
            }
        });
        
		getUsers();        
    }
	
	public void getUsers() {
        	ArrayList<String> aL = new ArrayList<String>();
	        LIST = new ArrayList<HashMap<String, String>>();
	        Map<String, ?> map = getSharedPreferences("TwitterOAUTH", 0).getAll();
	        Iterator<String> iterator = map.keySet().iterator();
	        do {
	            if(!iterator.hasNext())
	                break;
	            String s = (String)iterator.next();
	            HashMap<String,String> hashmap = new HashMap<String,String>();
	            if (!aL.contains(Character.toString(s.charAt(s.length()-1)))) {
		            aL.add(Character.toString(s.charAt(s.length()-1)));
	            }
	            if(map.get(s).toString() != null && !map.get(s).toString().equals("")) {
	                if(s.startsWith("username")) {
	                    hashmap.put("key", s);
	                    hashmap.put("username", map.get(s).toString());
	                } else if (s.startsWith("user_id")){
                        hashmap.put("key", s);
                        hashmap.put("userid", map.get(s).toString());
	                } else if (s.startsWith("oauth_token_secret")) {
                        hashmap.put("key", s);
                        hashmap.put("secret", map.get(s).toString());
	                } else if (s.startsWith("oauth_token")) {
                        hashmap.put("key", s);
                        hashmap.put("token", map.get(s).toString());
                    }
	                LIST.add(hashmap);
	            }
	        } while(true);
	        users = new User[LIST.size()/4];
	        for (int a=0;a<aL.size();a++) {
		        for (int i=0;i<LIST.size();i++) {
		        	if (LIST.get(i).get("key").equals("username"+aL.get(a))) {
		        		users[Integer.parseInt(aL.get(a))] = new User();
		        	}
		        }
		        for (int i=0;i<LIST.size();i++) {
		        	if (LIST.get(i).get("key").equals("username"+aL.get(a))) {
			        	users[Integer.parseInt(aL.get(a))].setName(LIST.get(i).get("username"));	
		        	} else if (LIST.get(i).get("key").equals("user_id"+aL.get(a))) {
			        	users[Integer.parseInt(aL.get(a))].setId(LIST.get(i).get("userid"));	
		        	} else if (LIST.get(i).get("key").equals("oauth_token"+aL.get(a))) {
			        	users[Integer.parseInt(aL.get(a))].setToken(LIST.get(i).get("token"));	
		        	} else if (LIST.get(i).get("key").equals("oauth_token_secret"+aL.get(a))) {
			        	users[Integer.parseInt(aL.get(a))].setSecret(LIST.get(i).get("secret"));			    	        Log.d("MAP", users[Integer.parseInt(aL.get(a))].getName());
		        	}
		        }
	        }

			adapter = new SpinAdapter(MainActivity.this,
	                android.R.layout.simple_spinner_item,
	                users);
	        mySpinner.setAdapter(adapter);
	}
	
	public void setProfilePic(String name){
        Twitter twitter = new TwitterFactory().getInstance();
        try {
            ProfileImage image = twitter.getProfileImage(name, ProfileImage.NORMAL);
            String src = image.getURL();
        	Drawable i = ImageOperations(this, src ,name);

        	iV.setImageDrawable(i);
        } catch (TwitterException e) {
			e.printStackTrace();
			iV.setImageResource(R.drawable.acct_sel);
		}
	}
	
	public void prefClean() {
		ArrayList<String> n = new ArrayList<String>();
        Map<String, ?> items = p.getAll();

        for(String s : items.keySet()){
            if (s.startsWith("username")) {
            	n.add(items.get(s).toString());
            }
        }
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.main_menu, menu);
		return true;
	}	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.settings:
	            Intent s = new Intent(this, SettingsMenu.class);
	            s.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(s);
	        break;
	        case R.id.sign_in:
	            Intent si = new Intent(this, TwitterAuth.class);
	            si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(si);
	        break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		return false;
	}	
	
	@Override
	public void onResume() {
		super.onResume();
		getUsers();
	}
	
    private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
    
	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
}
