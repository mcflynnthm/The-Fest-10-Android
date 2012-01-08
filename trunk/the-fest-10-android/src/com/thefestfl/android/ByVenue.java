/*
 *  John R. Flynn
 *  "The Fest! 10"
 * 
 */

package com.thefestfl.android;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

// Generates a ListView of the venues involved in the Fest
// The venues here are hardcoded in, mostly for ease, since
// they are well known.

public class ByVenue extends ListActivity {
	
	Context mCtx;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
//	    setContentView(R.layout.byvenue);

	    mCtx = this;
	    
	    setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, venues));
    	
    	ListView lv = getListView();
    	lv.setTextFilterEnabled(true);
    	
    	lv.setOnItemClickListener(new OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
    			String venueName = (String)parent.getItemAtPosition(position);
    			SharedPreferences prefs = getSharedPreferences("FestBand", MODE_PRIVATE);
    			SharedPreferences.Editor editor = prefs.edit();
        	
    			Intent i = new Intent(mCtx, VenuePage.class);
    			editor.putString("venue", venueName);
    			editor.commit();
    			startActivity(i);
    			
    		}
    	});
	}
	
	
	// The venues! Aren't the wonderful, folks? And alphabetical
	private String[] venues = new String[] {
			"1982",
			"8 Seconds",
			"The Atlantic",
			"Civic Media Center",
			"Double Down Live",
			"Durty Nelly's",
			"Florida Theater of Gainesville",
			"The Laboratory",
			"Loosey's",
			"Lunchbox",
			"Rockey's Piano Bar"
	};
    		
}
