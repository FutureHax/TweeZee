package com.t3hh4xx0r.tweezee.facebook;

import java.util.ArrayList;

import com.t3hh4xx0r.tweezee.DBAdapter;
import com.t3hh4xx0r.tweezee.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EntriesAdapterF extends BaseAdapter {
	ArrayList<String> selectionList;
	
	 private LayoutInflater mInflater;
	 Context ctx;	 
	 String message;
	 boolean active;
	 
	 public EntriesAdapterF(Context context, ArrayList<String> list) {
	  selectionList = list;
	  mInflater = LayoutInflater.from(context);
	  ctx = context;	  
	 }
	 
	 public int getCount() {
	  return selectionList.size();
	 }

	 public Object getItem(int position) {
	  return selectionList.get(position);
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(final int position, View convertView, ViewGroup parent) {
	  final ViewHolder holder;
	  if (convertView == null) {
		  convertView = mInflater.inflate(R.layout.facebook_entries, null);
		  holder = new ViewHolder();
		  holder.message = (TextView) convertView.findViewById(R.id.message);
		  holder.status_ic = (ImageView) convertView.findViewById(R.id.status_ic);
		  convertView.setTag(holder);   	   	  
	  } else {
		  holder = (ViewHolder) convertView.getTag();
	  }
	  message = selectionList.get(position).split(":")[0];
	  DBAdapter db = new DBAdapter(ctx);
      db.open();
      Cursor c = db.getAllFEntries();
      try {
    	  while (c.moveToNext()) {
    		  if (c.getString(c.getColumnIndex("message")).equals(message)) {	 
    			  active = Boolean.parseBoolean(c.getString(c.getColumnIndex("active")));
       			  break;
       		  }
       	  }
       } catch (Exception e) {
    	   e.printStackTrace();
       }
       if (active) {
		  holder.status_ic.setImageResource(R.drawable.status_active);
	  } else{
		  holder.status_ic.setImageResource(R.drawable.status_inactive);		  
	  }	  holder.message.setText(message);
	  return convertView;
	 }

	 static class ViewHolder {
	  TextView message;
	  ImageView status_ic;
	 }
	}