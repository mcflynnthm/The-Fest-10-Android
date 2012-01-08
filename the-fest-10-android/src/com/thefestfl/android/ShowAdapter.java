/*
 *  John R. Flynn
 *  "The Fest! 10"
 * 
 */

package com.thefestfl.android;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

// ShowAdapter is like BandAdapter, but I had a better idea
// of what I was doing. It extends SimpleAdapter for use building
// a ListView where shows that are in the user's schedule
// are denoted by a green background on their TextView. Nifty, huh?

public class ShowAdapter extends SimpleAdapter {

	Context mCtx;
	int[] my;
	
	public ShowAdapter(Context c, List<HashMap<String, String>> items, int res, String[] from, int[] to){
		super(c, items, res, from, to);
		mCtx = c;
	}
	
	// this is the constructor that's *actually* useful
	// as it contains a place for an int array of shows the
	// user is going to
	public ShowAdapter(Context c, List<HashMap<String, String>> items, int res, String[] from, int[] to, int[] myshows){
		super(c, items, res, from, to);
		mCtx = c;
		my = myshows;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		View view = super.getView(pos, convertView, parent);
		
		if(my[pos] == 1) view.setBackgroundColor(Color.rgb(51, 102, 51)); else view.setBackgroundColor(Color.BLACK);
		
		
		return view;
	}
}
