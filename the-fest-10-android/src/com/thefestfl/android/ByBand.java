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
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

// Generates a ListView of the bands that are playing
// Clicking on a band will bring up a BandPage for that
// act.

public class ByBand extends ListActivity {

	Context mCtx;
	
	public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	
    	mCtx = this;
    	
		BandDbAdapter bandDB = new BandDbAdapter(this);
    	bandDB.open();
    	
    	Cursor c = bandDB.fetchBands();

    	String [] bands = new String[c.getCount()];
    	int act = 0;
    	c.moveToFirst();
    	String name = null;
    	do{
    		name = c.getString(0);
    		if(name.contains("#")) name = name.replace("#", "'");
    		if (!(name.equals("*removed*")) && !(name.equals("Mystery Band"))){
    			bands[act] = name;
    			act++;
    		}
//    		index++;
    		c.moveToNext();
    	} while (c.getPosition() != c.getCount());
    	
    	String [] rbands = new String[act];
    	for (int x = 0; x < act; x++){
    		rbands[x] = bands[x];
    	}
    	
    	bandDB.close();
    	c.close();
    	
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, rbands));
    	
    	ListView lv = getListView();
    	lv.setTextFilterEnabled(true);
    	
    	lv.setOnItemClickListener(new OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
    			String bandName = (String)parent.getItemAtPosition(position);
    			//bandName = DatabaseUtils.sqlEscapeString(bandName);
    			SharedPreferences prefs = getSharedPreferences("FestBand", MODE_PRIVATE);
    			SharedPreferences.Editor editor = prefs.edit();
        	
    			Intent i = new Intent(mCtx, BandPage.class);
    			editor.putString("band", bandName);
    			editor.commit();
    			startActivity(i);
    		}
    	});
    	
	}
}
