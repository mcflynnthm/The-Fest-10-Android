/*
 *  John R. Flynn
 *  "The Fest! 10"
 * 
 */

package com.thefestfl.android;

import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class BandPage extends Activity implements Runnable{

	String name;
	String pic;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.bandpage);
        
        // Open the two databases: FestDB for shows
        // and BandDB for the band info.
        BandDbAdapter bandDb = new BandDbAdapter(this);
        bandDb.open();
        
        FestDBAdapter festDb = new FestDBAdapter(this);
        festDb.open();
        
        MySchedule my = new MySchedule(this);
        my.open();
        
        // Now who were we talking about? Oh, right.
        SharedPreferences prefs = getSharedPreferences("FestBand", MODE_PRIVATE);
        name = prefs.getString("band", "Red City Radioz");
        
        if(name.contains("'")) name = name.replace("'", "#");
        Log.i("name", "*"+name+"*");
        if(bandDb.checkBand(name))Log.i("in there", "true"); else Log.i("in there", "false");
        
        Cursor c = bandDb.fetchBand(name);
        c.moveToFirst();
        
        Cursor b = festDb.fetchShows(name);
        b.moveToFirst();
        
        // Set it up
        String bname, info;
        bname = c.getString(0);
        pic = "http://www.thefestfl.com"+c.getString(1);
        info = c.getString(2);
        
        // Alter the page all dynamic-like
        TextView tv = (TextView)findViewById(R.id.bandName);
        tv.setText(bname);
        
        TextView infov = (TextView)findViewById(R.id.bandInfo);
        infov.setText(info);
        
//        ImageView imgV = (ImageView)findViewById(R.id.bandPic);
//        imgV.setImageBitmap(getPic(pic));
        picHandler();
        
        // Set up the GridView for the shows this band will be playing
        
        String[] shows = null;
        shows = new String[b.getCount()];
        int[] mys = new int[b.getCount()];
        for (int x = 0; x<b.getCount(); x++){
        	
        	if(my.checkShow(b.getInt(4))) mys[x] = 1; else mys[x] = 0;
        	
        	String showtime = b.getString(0), showday = showtime.substring(0, 11);
        	showtime = showtime.replace(showday, "");
        	Log.i("Info", showtime + " " + showday);
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
        	
/*        	if (beghr > 12) {
        		beghr = beghr - 12;
        		temp = " PM";
        	}*/
        	
        	endhr = beghr;
        	endmin = begmin + Integer.parseInt(b.getString(3));
        	if (endmin >= 60) {
        		endhr = endhr + 1;
//        		if(endhr > 12) endhr = 1;
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
        	
        	
        	shows[x] = b.getString(1)+"\n"+showday+" "+showbeg + " - " + showend;
        	b.moveToNext();
        }
        
        c.close();
        b.close();
        my.close();
        bandDb.close();
        festDb.close();
        
        GridView gv = (GridView)findViewById(R.id.bandShows);
        gv.setAdapter(new BandAdapter(this, shows, mys));
        gv.setVerticalSpacing(1);
        gv.setBackgroundColor(Color.WHITE);
        
        gv.setOnItemClickListener(
        	new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					Context mCtx = getBaseContext();
					MySchedule ms = new MySchedule(mCtx);
	    			ms.open();
	    			
	    			FestDBAdapter festDB = new FestDBAdapter(mCtx);
	    	        festDB.open();
	    	        
	    	        Cursor c = festDB.fetchShows(name);    			
	    			c.moveToPosition(position);
	    			
	    			if(ms.checkShow(c.getInt(4))){
	    				int showId = ms.getShowId(c.getInt(4));
	    				ms.removeShow(showId);
	    				view.setBackgroundColor(Color.WHITE);
	    			} else {
	    				view.setBackgroundColor(Color.rgb(51,102,51));
	    				ms.addShow(c.getInt(4));
	    			
	    			}
	    			
	    			c.close();
	    			festDB.close();
	    			ms.close();
	    			
				}
        		
        	}
        );
        
        // Add onClickListener to gv that add/removes show as applicable

	}
	
	/*
	 *  Retrieves the band's picture from the Fest website
	 *  Happily, they're already there, and formatted to a
	 *  standard size, 111px x 200px!
	 *  Well, they're there until they decide to remove all of
	 *  last year's Fest stuff :(
	 */
	private Bitmap getPic(String srcUrl){
		Bitmap bitmap = null;
		InputStream in = null;
		
		try{
			in = OpenHttpConnection(srcUrl);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (IOException e){
			e.printStackTrace();
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noband);
		} catch (NullPointerException e){
			e.printStackTrace();
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.noband);
		}
		return bitmap;
		
	}
	
	/*
	 * Opens that HTTP connection for the picture above.
	 */
	private InputStream OpenHttpConnection(String urlString) 
    throws IOException
    {
        InputStream in = null;
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
                 
        if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");
        
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }                     
        }
        catch (Exception ex)
        {
            throw new IOException("Error connecting");            
        }
        return in;     
    }
	
	public int mProgressStatus = 0;
    public Handler mHandler = new Handler();
    public ProgressDialog mProgress;
    public Bitmap image;
    
	public void picHandler(){
		image = null;
		mProgress = ProgressDialog.show(this, "Loading band", "Please Wait...", false);
        
        // Start lengthy operation in a background thread
        new Thread(this).start();
        
	}
	
	public void run() {
    	
        while (mProgressStatus < 100) {
            image = getPic(pic);
            if(image != null) mProgressStatus = 100;

            // Update the progress bar
            mHandler.post(new Runnable() {
                public void run() {
                    mProgress.setProgress(mProgressStatus);
                }
            });
        }
        handle.sendEmptyMessage(0);
        
    }
	
	private Handler handle = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			mProgress.dismiss();
	        ImageView imgV = (ImageView)findViewById(R.id.bandPic);
	        imgV.setImageBitmap(image);
		}
	};
}
