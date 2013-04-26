package com.t3hh4xx0r.tweezee.log;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.t3hh4xx0r.tweezee.R;

public class LogAdapter extends BaseAdapter {
	ArrayList<String> selectionList;

	private LayoutInflater mInflater;
	Context ctx;
	String message;
	String recipient;
	boolean success;
	String time;

	public LogAdapter(Context context, ArrayList<String> list) {
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
			convertView = mInflater.inflate(R.layout.log_entries, null);
			holder = new ViewHolder();
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.send_to = (TextView) convertView.findViewById(R.id.send_to);
			holder.send_time = (TextView) convertView
					.findViewById(R.id.send_time);
			holder.send_status = (TextView) convertView
					.findViewById(R.id.send_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		message = selectionList.get(position).split("//")[2];
		recipient = selectionList.get(position).split("//")[3];
		time = selectionList.get(position).split("//")[1];
		success = Boolean
				.parseBoolean(selectionList.get(position).split("//")[4]);
		holder.message.setText(message);
		holder.send_to.setText(recipient);
		holder.send_time.setText(time);
		if (success) {
			holder.send_status.setText("SUCCESS");
		} else {
			holder.send_status.setText("FAILED");
		}
		return convertView;
	}

	static class ViewHolder {
		TextView message;
		TextView send_to;
		TextView send_time;
		TextView send_status;
	}
}