/*
 *  John R. Flynn
 *  "The Fest! 10"
 * 
 */

package com.thefestfl.android;

import java.util.Arrays;
import java.util.Comparator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

// Activity to display and work with a user's schedule

public class Schedule extends ListActivity implements OnItemClickListener {
	
	Context mCtx;
	String[] shows;
	int[] pos;
	
	public void onResume(){
		super.onResume();
		onCreate(new Bundle());
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		MySchedule ms = new MySchedule(this);
		ms.open();
		
		mCtx = this;
		
		FestDBAdapter fest = new FestDBAdapter(this);
		fest.open();
		
		Cursor c = ms.getSched();
		c.moveToFirst();
		shows = new String[c.getCount()];
		pos = new int[c.getCount()];
		
		// Here I need to not just make an array of Strings, but something ordered
		// by the date-time of the show. Ugh.
		for(int x = 0; x< c.getCount(); x++){
			Cursor f = fest.fetchShowById(c.getInt(0));
			f.moveToFirst();
			shows[x] = f.getString(0);
			if(shows[x].contains("#")) shows[x] = shows[x].replace("#", "'");
			
			String showtime = f.getString(1), showday = showtime.substring(0, 11);
        	showtime = showtime.replace(showday, "");
        	if (showday.equals("2011-10-28 ")) showday = "Fri";
        	else if (showday.equals("2011-10-29 ")) showday = "Sat";
        	else if (showday.equals("2011-10-30 ")) showday = "Sun";
			
        	if(showtime.contains("24:")) {
        		showtime = showtime.replace("24:", "00:");
        		if(showday.equals("Fri")) showday = "Sat";
        		else if(showday.equals("Sat")) showday = "Sun";
        		else showday = "Mon";
        	}
        	else if(showtime.contains("25:")){
        		showtime = showtime.replace("25:", "01:");
        		if(showday.equals("Fri")) showday = "Sat";
        		else if(showday.equals("Sat")) showday = "Sun";
        		else showday = "Mon";
        	}
        	
        	int beghr, begmin, endhr, endmin;
        	String temp = showtime.substring(0,2);
        	temp = temp.replace(":", "");
        	beghr = Integer.parseInt(temp);
        	begmin = Integer.parseInt(showtime.substring(showtime.length()-2,showtime.length()));
        	
//        	if (beghr > 12) beghr = beghr - 12;
        	
        	endhr = beghr;
        	endmin = begmin + Integer.parseInt(f.getString(3));
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
			
			if(f.getString(4).equals("1")) shows[x] = shows[x]+ " (acoustic)";
			shows[x] = c.getInt(1)+"<"+shows [x]+"\n"+f.getString(2) +"\n"+showday+" "+showbeg+ " - " + showend; 
			c.moveToNext();
			shows[x] = shows[x].replace("#", "'");
		}
		
		c.close();
		fest.close();
		ms.close();
		
		Arrays.sort(shows, new Comparator<String>(){
			@Override
			public int compare (String entry1, String entry2){
				int day1, day2;
				if(entry1.contains("Fri ")) day1 = 1;
				else if (entry1.contains("Sat ")) day1 = 2;
				else if (entry1.contains("Mon ")) day1 = 4;
				else day1 = 3;
				
				if(entry2.contains("Fri ")) day2 = 1;
				else if (entry2.contains("Sat ")) day2 = 2;
				else if (entry2.contains("Mon ")) day2 = 4;
				else day2 = 3;
				
				if (day1 < day2) return -1; else if (day2 < day1) return 1; else {
					
					if(entry1.contains(" AM - ") && entry2.contains(" PM - ")) return -1;
					else if (entry1.contains(" PM - ") && entry2.contains(" AM - ")) return 1; 
					
					int pos1 = entry1.indexOf(":"), pos2 = entry2.indexOf(":");
					if ((pos1 != -1) && (pos2 != -1)){
						
						String t1 = entry1.substring(pos1-2, pos1+6);
						String t2 = entry2.substring(pos2-2, pos2+6);
						if((t1.contains(" 1:") && t1.contains("AM")) && (t2.contains("12:") && t2.contains("AM"))){
							return 1;
						} else if((t2.contains(" 1:") && t2.contains("AM")) && (t1.contains("12:") && t1.contains("AM"))){
							return -1;
						}
						
						int beghr, begmin, endhr, endmin;
						String temp;
						
						temp = entry1.substring(pos1-2, pos1);
						temp = temp.replace(" ", "");
						beghr = Integer.parseInt(temp);
//						if(beghr == 1) beghr = 13; else if (beghr == 0) beghr = 12;
						begmin = Integer.parseInt(entry1.substring(pos1+1, pos1+3));
						
						temp = entry2.substring(pos2-2, pos2);
						temp = temp.replace(" ", "");
						endhr = Integer.parseInt(temp);
//						if (endhr == 1) endhr = 13;
						endmin = Integer.parseInt(entry2.substring(pos2+1, pos2+3));
						
						if(beghr < endhr) return -1; else if (beghr > endhr) return 1; else{
							if (begmin < endmin) return -1;
							else return 1;
						}
					}
					
				}
				return 0;
			}
		});
		
		for(int x = 0; x < pos.length; x++){
			int temp = shows[x].indexOf("<");
			pos[x] = Integer.parseInt(shows[x].substring(0, temp));
			shows[x] = shows[x].substring(temp+1, shows[x].length());
		}
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, shows));
    	
    	ListView lv = getListView();
    	lv.setTextFilterEnabled(true);
    	
    	lv.setOnItemClickListener(this);
		
	}
	
	int posi;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		
		posi = position;
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
		        	
		        	//ListView lv = getListView();
		    		
		    		MySchedule ms = new MySchedule(mCtx);
		    		ms.open();
		    		
		    		ms.removeShow(pos[posi]);
		    		String[] newShows = new String[shows.length - 1];
		    		int [] newpos = new int[pos.length - 1];
		    		int x = 0, y = 0;
		    		while (x < shows.length){
		    			if (x != posi) {
		    				newShows[y] = shows[x];
		    				newpos[y] = pos[x];
		    				y++;
		    			}
		    			x++;
		    		}
		    		shows = newShows;
		    		pos = newpos;
		    		
		    		ms.close();
		    		
		    		setListAdapter(new ArrayAdapter<String>(mCtx, R.layout.list_item, shows));
		        	
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Remove show").setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
		    .setNegativeButton("No", dialogClickListener).show();
		
	}

	
}
