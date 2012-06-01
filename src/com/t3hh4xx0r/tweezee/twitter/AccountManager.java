package com.t3hh4xx0r.tweezee.twitter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.t3hh4xx0r.tweezee.MainActivity;
import com.t3hh4xx0r.tweezee.R;

public class AccountManager extends SherlockListActivity {
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
		
		for(int i=0;i<TwitterActivity.users.length;i++){
			rd = new RowData(i,TwitterActivity.users[i].getName());
			data.add(rd);
			names.add(TwitterActivity.users[i].getName());
		}
		
        mAddEntry = (Button) findViewById(R.id.entry_b);
        mAddEntry.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
		       	Intent si = new Intent(v.getContext(), TwitterAuth.class);
		       	si.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		       	startActivityForResult(si, 0);
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
	    	TwitterActivity mA = new TwitterActivity();
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
    	BetterPopupWindow dw = new BetterPopupWindow.DemoPopupWindow(v, msg, p, p);
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
			File file = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/ultimate_scheduler/profileimages/twitter_"+ name + "_image_large.jpg");
			File dir = new File(Environment.getExternalStorageDirectory() + "/t3hh4xx0r/ultimate_scheduler/profileimages/");
			Drawable d;
			if (!file.exists()) {
				if (!dir.exists()) {
					dir.mkdirs();
				}	
				try {
		           Twitter twitter = new TwitterFactory().getInstance();
		           ProfileImage image = twitter.getProfileImage(name, ProfileImage.BIGGER);
		           URL src = new URL(image.getURL());
	
		           Bitmap bm = BitmapFactory.decodeStream(src.openConnection().getInputStream());
		           bm = Bitmap.createScaledBitmap(bm, 300, 300, true); 
		           bm.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
		           d = new BitmapDrawable(bm);
				} catch (Exception e) {
					e.printStackTrace();
					d = r.getDrawable(R.drawable.acct_sel);
				}
			} else {
				Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
				d = new BitmapDrawable(bitmap);
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
