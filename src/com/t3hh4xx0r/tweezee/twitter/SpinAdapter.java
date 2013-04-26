package com.t3hh4xx0r.tweezee.twitter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinAdapter extends ArrayAdapter<User> {
	// Your sent context
	private Context context;
	// Your custom values for the spinner (User)
	private User[] values;

	public SpinAdapter(Context context, int textViewResourceId, User[] values) {
		super(context, textViewResourceId, values);
		this.context = context;
		this.values = values;
	}

	public int getCount() {
		return values.length;
	}

	public User getItem(int position) {
		return values[position];
	}

	public long getItemId(int position) {
		return position;
	}

	// And the "magic" goes here
	// This is for the "passive" state of the spinner
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// I created a dynamic TextView here, but you can reference your own
		// custom layout for each spinner item
		TextView label = new TextView(context);
		// label.setTextColor(Color.BLACK);
		// Then you can get the current item using the values array (Users
		// array) and the current position
		// You can NOW reference each method you has created in your bean object
		// (User class)
		Log.d("ADAPTER", Integer.toString(position + 1));
		label.setText(values[position].getName());
		label.setTextSize(30);

		// And finally return your dynamic (or custom) view for each spinner
		// item
		return label;
	}

	// And here is when the "chooser" is popped up
	// Normally is the same view, but you can customize it if you want
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView label = new TextView(context);
		label.setTextSize(30);
		label.setPadding(5, 5, 5, 5);
		label.setText(values[position].getName());
		return label;
	}
}