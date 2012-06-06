package com.t3hh4xx0r.tweezee.facebook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;
import com.t3hh4xx0r.tweezee.sms.EntriesAdapter;

public class FacebookActivity extends SherlockListActivity {
	ListView lv1;
	Button mAddEntry;
	static EntriesAdapterF a;
	static ArrayList<String> mEntries;
	TextView userTV;
	String userID;
	String userN;
	ImageView userPic;
	ProgressBar pB;

	private static final String FACEBOOK_APPID = "405018012875515";
	private static final String FACEBOOK_PERMISSION = "publish_stream";
	public static FacebookConnector facebookConnector;

	File dir = new File(Environment.getExternalStorageDirectory()+"/t3hh4xx0r/ultimate_scheduler/backups");
	File backup = new File(dir+"/facebook.txt");
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        FacebookActivity.facebookConnector = new FacebookConnector(FACEBOOK_APPID, this, getApplicationContext(), new String[] {FACEBOOK_PERMISSION});
		setContentView(R.layout.facebook_main);
	    lv1 = (ListView) findViewById(android.R.id.list);   
	    userTV = (TextView) findViewById(R.id.user);
	    userPic = (ImageView) findViewById(R.id.userPic);
	    pB = (ProgressBar) findViewById(R.id.pB);
		checkLogin();
        lv1.setOnItemLongClickListener(new OnItemLongClickListener() {
        	@Override
        	public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {           	
    			ArrayList<String> mMessages = new ArrayList<String>();
    			DBAdapter db = new DBAdapter(v.getContext());
    		    db.open();
    		    Cursor c = db.getAllFEntries();
    		    	try {
    		       		while (c.moveToNext()) {
    		       			mMessages.add(c.getString(c.getColumnIndex("message")));
    		       		}
    		       	 } catch (Exception e1) {}
    		   c.close();
    		   db.close();
    		   
    		   final Vibrator vibe = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
        	   vibe.vibrate(50);
               String message = mMessages.get(position); 
               BetterPopupWindowF dw = new BetterPopupWindowF.DemoPopupWindow(v, message, position);
               dw.showLikeQuickAction(0, 30);
               return false;
        	}
        });
        
        mAddEntry = (Button) findViewById(R.id.entry_b);
        mAddEntry.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		Intent i = new Intent(FacebookActivity.this, EntryAddF.class);
        		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		i.putExtra("editing", false);
        		startActivity(i);
        	}
        });	   
	}

	private void checkLogin() {
		if (!facebookConnector.getFacebook().isSessionValid()) {
            startActivity(new Intent(this, FacebookSplash.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		} else {
			getUserInfo();
		}
	}

	private void getUserInfo() {
		Thread thread2 = new Thread() {
			Drawable p;
		    @Override
		    public void run() {
				try {
					String userReq = facebookConnector.getFacebook().request("me");
					JSONObject o = Util.parseJson(userReq);
					userN = o.optString("name", "Error retrieving username");
					userID = o.optString("id");				
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	p = getPic(userID);		    			    
				runOnUiThread(new Runnable() {
		               @Override
		               public void run() {
		            	   userTV.setText(userN);
		            	   userPic.setImageDrawable(p);
		               }
		         });		    			    		    	
		    }
		};
		thread2.start();		
	}

	private Drawable getPic(String id) {
		Resources res = getResources();
		File file = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/ultimate_scheduler/profileimages/facebook_"+ userN + "_image.jpg");
		File dir = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/ultimate_scheduler/profileimages/");
		Drawable d;
		if (!file.exists()) {
			if (!dir.exists()) {
				dir.mkdirs();
			}		
			try {
				URL imgUrl = new URL("http://graph.facebook.com/"+id+"/picture?type=large");
				Bitmap imgB = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
		        imgB.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
				d = new BitmapDrawable(imgB);
			} catch (MalformedURLException e) {
				d = res.getDrawable(R.drawable.acct_sel);
				e.printStackTrace();
			} catch (IOException e) {
				d = res.getDrawable(R.drawable.acct_sel);
				e.printStackTrace();
			}
		} else {
			Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
			d = new BitmapDrawable(bitmap);
		}
		return d;
	}

	public void onListItemClick(ListView lv, View v, int p, long id) {	
		ArrayList<String> mMessages = new ArrayList<String>();
		ArrayList<String> mIntervals = new ArrayList<String>();
		ArrayList<String> mDays = new ArrayList<String>();
		ArrayList<String> mTimes = new ArrayList<String>();
		ArrayList<String> mBoots = new ArrayList<String>();
		ArrayList<String> mDates = new ArrayList<String>();
		String m = null;
		String i = null;
		String d = null;
		String t = null;
		String date = null;
		String boot = null;
		DBAdapter db = new DBAdapter(v.getContext());
	    db.open();
	    Cursor c = db.getAllFEntries();
	    	try {
	       		while (c.moveToNext()) {
	       			mMessages.add(c.getString(c.getColumnIndex("message")));
	       			mIntervals.add(c.getString(c.getColumnIndex("send_wait")));
	       			mDays.add(c.getString(c.getColumnIndex("send_day")));
	       			mTimes.add(c.getString(c.getColumnIndex("send_time")));
	       			mBoots.add(c.getString(c.getColumnIndex("start_boot")));
       				mDates.add(c.getString(c.getColumnIndex("send_date")));
	       		}
	       	 } catch (Exception e1) {}
	    c.close();
	    db.close();		
	    m = mMessages.get(p);
	    i = mIntervals.get(p);
	    d = mDays.get(p);
	    date = mDates.get(p);
	    t = mTimes.get(p);
	    boot = mBoots.get(p);
        Bundle b = new Bundle();
        b.putBoolean("editing", true);
        b.putInt("pos", p);
        b.putString("message", m);
        b.putString("interval", i);
        b.putString("days", d);
        b.putString("boot", boot);
        if (t.length()>1) {
        	b.putString("time", t);
        } else {
        	b.putString("time", null);
        }
        if (date.length()>1) {
        	b.putString("date", date);
        }
        Intent mi = new Intent(v.getContext(), EntryAddF.class);
        mi.putExtras(b);
        startActivity(mi);	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		populateList(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		populateList(this);
	}
	
	void populateList(Context ctx) {
		mEntries = new ArrayList<String>();
		if (mEntries.size() != 0) {
			mEntries.clear();
		}
		a = new EntriesAdapterF(FacebookActivity.this, mEntries);
	    lv1.setAdapter(a);

		DBAdapter db = new DBAdapter(ctx);
	    db.open();
	    Cursor cu = db.getAllFEntries();
	    	try {
	       		while (cu.moveToNext()) {
	       			StringBuilder sB = new StringBuilder();
	       			sB.append(cu.getString(cu.getColumnIndex("message")));
	       			mEntries.add(sB.toString());
	       		}
	       	 } catch (Exception e1) {}
	   cu.close();
	   db.close();
	   
	   a.notifyDataSetChanged();
	   
	}
	
	 public static Handler handy = new Handler() {
		public void handleMessage(Message m) {
			switch (m.what) {
			case 0:			
				   a.notifyDataSetChanged();
				break;
			}
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.facebook_menu, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	        case android.R.id.home:
	            Intent hi = new Intent(this, MainActivity.class);
	            hi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(hi);
	            return true;
	        case R.id.share:
	        	Bundle parameters = new Bundle();
	        	parameters.putString( "message", "Check out this awesome new app!" );
	        	facebookConnector.getFacebook().dialog(this, "apprequests", parameters,
	        	  new Facebook.DialogListener() {
					@Override
					public void onComplete(Bundle values) {
						// TODO Auto-generated method stub						
					}
					@Override
					public void onFacebookError(FacebookError e) {
						// TODO Auto-generated method stub						
					}
					@Override
					public void onError(DialogError e) {
						// TODO Auto-generated method stub						
					}
					@Override
					public void onCancel() {
						// TODO Auto-generated method stub						
					}
	        	  }
	        	);	        	
	        	return true;
	        case R.id.sign_out:
	        	facebookConnector.logout();
	        	pB.setVisibility(View.VISIBLE);
	            return true;
	        case R.id.backup:
	        	prepareBackupEntries(this);
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	
	
	private void prepareBackupEntries(final Context c) {
		DBAdapter db = new DBAdapter(c);
	    db.open();
	    Cursor cu = db.getAllFEntries();
	    int count = cu.getCount();
	    cu.close();
	    db.close();
	    
	    if (count < 1) {
           	AlertDialog.Builder builder = new AlertDialog.Builder(this);
      		builder.setTitle("Whoopsie");
      		builder.setMessage("You've got no entires to backup!")
      		   .setCancelable(false)
      		   .setPositiveButton("Oh ya! My bad.", new DialogInterface.OnClickListener() {
      		       public void onClick(DialogInterface dialog, int id) {
      		    	   dialog.dismiss();
      		 		}
      		   });
      		AlertDialog alert = builder.create();
      		alert.show();
	    } else {
	    	if (backup.exists()) {
		    	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
	      		builder2.setTitle("Warning");
	      		builder2.setMessage("This will overwrite your current backup. Continue?")
	      		   .setCancelable(false)
	      		   .setPositiveButton("Yup.", new DialogInterface.OnClickListener() {
	      		       public void onClick(DialogInterface dialog, int id) {
	      		    	   backup.delete();
	      		    	   backupEntries(c);
	      		       }
	      		   })
	      		   .setNegativeButton("Nah.", new DialogInterface.OnClickListener() {
	      		       public void onClick(DialogInterface dialog, int id) {
	      		    	   dialog.dismiss();
	      		 		}
	      		   });
	      		AlertDialog alert2 = builder2.create();
	      		alert2.show();	    	    	
	    	} else {
	    	   backupEntries(c);
	    	}
	    }	    
	}

	protected void backupEntries(Context c) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		FileWriter fW = null;
		BufferedWriter bW = null;
		try {
			fW = new FileWriter(backup, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bW = new BufferedWriter(fW);
		
		DBAdapter db = new DBAdapter(c);
	    db.open();
	    Cursor cu = db.getAllFEntries();
	    int count = cu.getCount();
    	try {
       		while (cu.moveToNext()) {       			
       			StringBuilder sB = new StringBuilder();
       			String message = cu.getString(cu.getColumnIndex("message"));
       			String send_wait = cu.getString(cu.getColumnIndex("send_wait"));
       			String send_day = cu.getString(cu.getColumnIndex("send_day"));
       			String send_time = cu.getString(cu.getColumnIndex("send_time"));
       			String start_boot = cu.getString(cu.getColumnIndex("start_boot"));
       			String my_id = cu.getString(cu.getColumnIndex("my_id"));
       			String send_date = cu.getString(cu.getColumnIndex("send_date"));
       			sB.append("///");
       			sB.append(message+"//");
       			if (send_wait.length()>0) {
           			sB.append(send_wait+"//");
       			} else {
       				sB.append("--//");
       			}
       			sB.append(send_day+"//");
       			if (send_time.length()>0) {
           			sB.append(send_time+"//");
       			} else {
       				sB.append("--//");
       			}
           		sB.append(start_boot+"//");
           		sB.append(my_id+"//");
       			if (send_date.length()>0) {
           			sB.append(send_date+"//");
       			} else {
       				sB.append("--//");
       			}
       			bW.append(sB.toString());  
       			bW.newLine();
       			bW.newLine();
       			if (cu.getPosition() == count-1) {
       				bW.flush();
       				bW.close();
       			}
       		}
       	 } catch (Exception e1) {
       		 e1.printStackTrace();
       	 }	  
    	cu.close();
    	db.close();
	}	
}
