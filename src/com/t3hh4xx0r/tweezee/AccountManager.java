package com.t3hh4xx0r.tweezee;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AccountManager extends ListActivity {
	ImageView pic;
	TextView name;
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	ArrayList<String> names;
	Button mAddEntry;

	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.accounts);
		names = new ArrayList<String>();
		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		data = new Vector<RowData>();
		
		for(int i=0;i<MainActivity.users.length;i++){
			rd = new RowData(i,MainActivity.users[i].getName());
			data.add(rd);
			names.add(MainActivity.users[i].getName());
		}
		
        mAddEntry = (Button) findViewById(R.id.entry_b);
        mAddEntry.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(v.getContext());

			    DBAdapter db = new DBAdapter(v.getContext());
		       	db.open();
		       	Cursor c = db.getAllUsers();
		       	int count = c.getCount();
		       	c.close();
		       	db.close();
		       	if (count>0 && !prefs.getBoolean("isReg", false)) {
		           	AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		      		builder.setTitle("Upgrade to premium to enable multiple account support!");
		      		builder.setMessage("The free version is limited to only one account.\nPlease upgrade to access premium features.")
		      		   .setCancelable(false)
		      		   .setPositiveButton("Let\'s check it out!", new DialogInterface.OnClickListener() {
		      		       public void onClick(DialogInterface dialog, int id) {
		      		    	   Toast.makeText(getBaseContext(), "Premium version is currently not available. Sorry!", Toast.LENGTH_LONG).show();
		      		       }
		      		   })
		      		.setNegativeButton("Not today", new DialogInterface.OnClickListener() {
		      		       public void onClick(DialogInterface dialog, int id) {
		      		    	   dialog.dismiss();
		      		       }
		      		   });
		      		AlertDialog alert = builder.create();
		      		alert.show();
		       	} else {
		       		Intent si = new Intent(v.getContext(), TwitterAuth.class);
		       		si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		       		startActivityForResult(si, 0);
		       	}
        	}
        });	
        
		CustomAdapter adapter = new	 CustomAdapter(this, R.layout.row, R.id.name, data);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
						
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    switch (requestCode) {
	    case 0:
	    	MainActivity mA = new MainActivity();
	    	mA.getUsers(this);
            Intent mi = new Intent(this, AccountManager.class);
            startActivity(mi);
	        break;
	    default:
	        break;
	    }
	}
	public void onListItemClick(ListView lv, View v, int p, long id) {
		String msg = names.get(p);	
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Editor e = prefs.edit();
		e.putBoolean("account", true);
		e.commit();
		
		final Vibrator vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
    	vibe.vibrate(50);
    	BetterPopupWindow dw = new BetterPopupWindow.DemoPopupWindow(v, msg, p);
		dw.showLikeQuickAction(0, 30);
	}
	
	 private class RowData {
	       protected String mTitle;
	       RowData(int id, String title){
	    	   mTitle = title;
	       }
	 }
	 
	  private class CustomAdapter extends ArrayAdapter<RowData> {
		  String name = null;

		  public CustomAdapter(Context context, int resource, int textViewResourceId, List<RowData> objects) {               
			  super(context, resource, textViewResourceId, objects);
		  }
	  
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {   
			  ViewHolder holder = null;
			  TextView title = null;
	   		  ImageView i11=null;
			  RowData rowData= getItem(position);
			  if(null == convertView){
				  convertView = mInflater.inflate(R.layout.row, null);
				  holder = new ViewHolder(convertView);
				  convertView.setTag(holder);
			  }
			  
			  holder = (ViewHolder) convertView.getTag();
			  title = holder.gettitle();
			  title.setText(rowData.mTitle);                                                   
			  name = rowData.mTitle;
			  i11 = holder.getImage();
			  Drawable p;
			  p = setProfilePic(name);	
			  i11.setImageDrawable(p);
		  return convertView;
		  }
  
		  private class ViewHolder {
			  	private View mRow;
			  	private TextView title = null;
			  	private ImageView i11=null; 

			  	public ViewHolder(View row) {
			  		mRow = row;
			  	}
			  	
			  	public TextView gettitle() {
			  		if (null == title){
			  			title = (TextView) mRow.findViewById(R.id.name);
			  		}
			  		return title;
			  	}     

			  	public ImageView getImage() {
			  		if (null == i11){
			  			i11 = (ImageView) mRow.findViewById(R.id.pic);
                    }
			  		return i11;
			  	}
		  }
	  } 
	  
	  public Drawable setProfilePic(String name){
			Resources r = getResources();
			Drawable d;
			try {
	           Twitter twitter = new TwitterFactory().getInstance();
	           ProfileImage image = twitter.getProfileImage(name, ProfileImage.BIGGER);
	           URL src = new URL(image.getURL());

	           Bitmap bm = BitmapFactory.decodeStream(src.openConnection().getInputStream());
	           bm = Bitmap.createScaledBitmap(bm, 300, 300, true); 
	           d = new BitmapDrawable(bm);
			} catch (Exception e) {
				e.printStackTrace();
				d = r.getDrawable(R.drawable.acct_sel);
			}
	        return d;
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
