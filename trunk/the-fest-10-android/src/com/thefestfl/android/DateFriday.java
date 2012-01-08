/*
 *  John R. Flynn
 *  "The Fest! 10"
 * 
 */

package com.thefestfl.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

// Activity for the Friday tab in ByDate

public class DateFriday extends ListActivity {
	
	Context mCtx;
	int[] my;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mCtx = this;
        
        FestDBAdapter festDB = new FestDBAdapter(this);
        festDB.open();
        
        MySchedule ms = new MySchedule(this);
        ms.open();
        
        Cursor c = festDB.fetchDateShows(1);
        c.moveToFirst();
        
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        String [] from = new String[] {"show"};
        int[] to = new int[] {R.id.show};
        
        my = new int[c.getCount()];
        
        for(int x = 0; x < c.getCount(); x++){
        	HashMap<String, String> map = new HashMap<String, String>();

        	String showtime = c.getString(1);
        	showtime = showtime.replace("2011-10-28 ", "");
        	if(showtime.contains("24:")) showtime = showtime.replace("24:", "00:");
        	else if(showtime.contains("25:")) showtime = showtime.replace("25:", "01:");
        	
        	int beghr, begmin, endhr, endmin;
        	String temp = showtime.substring(0,2);
        	temp = temp.replace(":", "");
        	beghr = Integer.parseInt(temp);
        	begmin = Integer.parseInt(showtime.substring(showtime.length()-2,showtime.length()));
        	
//        	if (beghr > 12) beghr = beghr - 12;
        	
        	endhr = beghr;
        	endmin = begmin + Integer.parseInt(c.getString(3));
        	if (endmin >= 60) {
        		endhr = endhr + 1;
        		if(endhr > 23) endhr = 0;
        		endmin = endmin - 60;
        	}
        	
        	String showbeg, showend;
        	
        	if  (begmin == 0) temp = ":00";
        	else if (begmin < 10) temp = ":0"+begmin;
        	else temp = ":"+begmin;
        	if ((beghr > 12)&&(beghr < 24)) {
        		temp = temp + " PM";
        		beghr = beghr - 12;
        	}
        	else{
        		temp = temp + " AM";
        		if (beghr == 0) beghr = 12;
        		if (beghr == 25) beghr = 1;
        	}
        	showbeg = beghr + temp;
        	
        	if (endmin < 1) temp = ":00";
        	else if (endmin< 10) temp = ":0"+endmin;
        	else temp = ":"+endmin;
        	if ((endhr > 12)&&(endhr < 24)) {
        		temp = temp + " PM";
        		endhr = endhr - 12;
        	}
        	else{
        		temp = temp + " AM";
        		if (endhr == 0) endhr = 12;
        		if (endhr == 25) endhr = 1;
        	}
        	showend = endhr + temp;
        	
//        	String bandname = c.getString(0).replace("#", "'");
        	String show = c.getString(0) + "\n"+ c.getString(2)+"\n" + 
        			   showbeg + " - " + showend;
        	show = show.replace("#", "'");
        	map.put("show", show);
        	if(!(show.contains("*removed*"))){
        		if(ms.checkShow(c.getInt(5))) my[x] = 1; else my[x] = 0;
        		fillMaps.add(map);
        	}
        	c.moveToNext();
        }
        
        ms.close();
        festDB.close();
        c.close();
        
        ShowAdapter adapter = new ShowAdapter(this, fillMaps, R.layout.list_item, from, to, my);
        
        setListAdapter(adapter);
    	
    	ListView lv = getListView();
    	lv.setTextFilterEnabled(true);
    	
    	lv.setOnItemClickListener(new OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
    			MySchedule ms = new MySchedule(mCtx);
    			ms.open();
    			
    			FestDBAdapter festDB = new FestDBAdapter(mCtx);
    	        festDB.open();
    	        
    	        Cursor c = festDB.fetchDateShows(1);    			
    			c.moveToPosition(position);
    			
    			if(ms.checkShow(c.getInt(5))){
    				int showId = ms.getShowId(c.getInt(5));
    				ms.removeShow(showId);
    				view.setBackgroundColor(Color.BLACK);
    				my[position] = 0;
    			} else {
    				my[position] = 1;
    				view.setBackgroundColor(Color.rgb(51,102,51));
    				ms.addShow(c.getInt(5));
    			
    			}
    			
    			festDB.close();
    			ms.close();
    			c.close();
    		}
    	});
    }
}
