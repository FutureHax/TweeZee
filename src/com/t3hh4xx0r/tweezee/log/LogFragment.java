package com.t3hh4xx0r.tweezee.log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.t3hh4xx0r.tweezee.R;

public class LogFragment extends SherlockListFragment {
	Context ctx;
	View v;
	Button mAddEntry;
	int pos;
	String type;
	public ArrayList<String> entryArray;
	ListView listView;
	LogAdapter a;
	public static ArrayList<String> idArray;
	TextView warn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = container.getContext();
		if (v != null) {
			v.invalidate();
		}

		entryArray = new ArrayList<String>();

		v = inflater.inflate(R.layout.log_fragment, container, false);
		listView = (ListView) v.findViewById(android.R.id.list);
		warn = (TextView) v.findViewById(R.id.warn);
		pos = getArguments().getInt("p");
		type = getArguments().getString("type");
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		populateList();
	}

	@Override
	public void onResume() {
		super.onResume();
		populateList();
	}

	void populateList() {
		entryArray.clear();
		a = new LogAdapter(ctx, entryArray);
		setListAdapter(a);

		FileInputStream fI = null;
		try {
			fI = new FileInputStream(Environment.getExternalStorageDirectory()
					+ "/t3hh4xx0r/ultimate_scheduler/log.txt");
			DataInputStream dI = new DataInputStream(fI);
			BufferedReader bR = new BufferedReader(new InputStreamReader(dI));
			String l;
			while ((l = bR.readLine()) != null) {
				if (l.startsWith("///")
						&& l.replace("///", "").startsWith(type)) {
					if (!entryArray.contains(l)) {
						entryArray.add(l.replace("///", ""));
					}
				}
			}
			dI.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		a.notifyDataSetChanged();
		if (entryArray.size() < 1) {
			warn.setVisibility(View.VISIBLE);
		}
	}

}