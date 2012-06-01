package com.t3hh4xx0r.tweezee.sms;

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

public class EntriesAdapter extends BaseAdapter {
	ArrayList<String> selectionList;
	
	 private LayoutInflater mInflater;
	 Context ctx;	 
	 String message;
	 String recipient;
	 boolean active;
	 
	 public EntriesAdapter(Context context, ArrayList<String> list) {
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
		  convertView = mInflater.inflate(R.layout.sms_entries, null);
		  holder = new ViewHolder();
		  holder.message = (TextView) convertView.findViewById(R.id.message);
		  holder.send_to = (TextView) convertView.findViewById(R.id.send_to);
		  holder.status_ic = (ImageView) convertView.findViewById(R.id.status_ic);
		  convertView.setTag(holder);   	   	  
	  } else {
		  holder = (ViewHolder) convertView.getTag();
	  }
	  message = selectionList.get(position).split(":")[0];
	  recipient = selectionList.get(position).split(":")[1];
	  DBAdapter db = new DBAdapter(ctx);
      db.open();
      Cursor c = db.getAllSEntries();
      try {
    	  while (c.moveToNext()) {
    		  if (c.getString(c.getColumnIndex("message")).equals(message)
       		      && c.getString(c.getColumnIndex("send_to")).equals(recipient)) {	 
    			  active = Boolean.parseBoolean(c.getString(c.getColumnIndex("active")));
       			  break;
       		  }
       	  }
       } catch (Exception e) {
    	   e.printStackTrace();
       }
       c.close();
       db.close();
       if (active) {
		  holder.status_ic.setImageResource(R.drawable.status_active);
	  } else{
		  holder.status_ic.setImageResource(R.drawable.status_inactive);		  
	  }	  holder.message.setText(message);
	  holder.send_to.setText(recipient);
	  return convertView;
	 }

	 static class ViewHolder {
	  TextView message;
	  TextView send_to;
	  ImageView status_ic;
	 }
	}