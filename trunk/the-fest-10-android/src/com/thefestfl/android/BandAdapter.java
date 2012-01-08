/*
 *  John R. Flynn
 *  "The Fest! 10"
 * 
 */

package com.thefestfl.android;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// Legacy class used to populate shows in BandPage.
// Future implementations will use ShowAdapter instead.

public class BandAdapter extends BaseAdapter {

	Context mContext;
	String[] Bands;
	int[] my;
	
	public BandAdapter(Context c, String[] fest){
		mContext = c;
		Bands = fest;
		my = null;
	}
	
	public BandAdapter(Context c, String[] fest, int[] mine){
		mContext = c;
		Bands = fest;
		my = mine;
	}
	
	@Override
	public int getCount() {
		// returns how many items
		return Bands.length;
	}

	@Override
	public String getItem(int x) {
		// returns an item
		return Bands[x];
	}

	@Override
	public long getItemId(int arg0) {
		// Returns an item id
		return 0;
	}

	// Add check against MySchedule, set bg color green if yes
	@Override
	public View getView(int pos, View arg1, ViewGroup arg2) {
		// Generates the TextView for the schedule
		TextView tv = new TextView(mContext);
		tv.setTextSize(14);
		tv.setTextColor(Color.BLACK);
		if(my != null){
			if(my[pos] == 1) tv.setBackgroundColor(Color.rgb(51,102,51));
		}
		tv.setText(Bands[pos]);
		return tv;
	}

}
