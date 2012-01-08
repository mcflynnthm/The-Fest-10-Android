package com.thefestfl.android;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MyLocationOverlay;

public class FestMap extends MapActivity {

	MyLocationOverlay myloc;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    MapController mc = mapView.getController();
	    mc.setCenter(new GeoPoint(29651827,-82328131));
	    mc.setZoom(16);
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.stressface);
	    FestMapOverlay itemizedoverlay = new FestMapOverlay(drawable, this);
	    
	    myloc = new MyLocationOverlay(this, mapView);
	    mapView.getOverlays().add(myloc);
	    
	    // No Idea HQ; a test, more or less
	    GeoPoint point = new GeoPoint(29650240, -82335200);
	    OverlayItem overlayitem = new OverlayItem(point, "No Idea HQ", "Where the magic happens.");
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	    
	    // Holiday Inn
	    drawable = this.getResources().getDrawable(R.drawable.smallface);
	    itemizedoverlay = new FestMapOverlay(drawable, this);
	    point = new GeoPoint(29652184, -82338299);
	    overlayitem = new OverlayItem(point, "Holiday Inn", "Registration, fun times!");
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	    
	    // Venues; find new Drawable
	    itemizedoverlay = new FestMapOverlay(drawable, this);
	    point = new GeoPoint(29651911, -82327294);
	    overlayitem = new OverlayItem(point, "The Florida Theater of Gainesville", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    point = new GeoPoint(29651907, -82326825);
	    overlayitem = new OverlayItem(point, "8 Seconds", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);

	    point = new GeoPoint(29650409,-82327026);
	    overlayitem = new OverlayItem(point, "Double Down Live", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    point = new GeoPoint(29652312,-82324773);
	    overlayitem = new OverlayItem(point, "The Atlantic", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);

	    point = new GeoPoint(29651960, -82334230);
	    overlayitem = new OverlayItem(point, "1982", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    point = new GeoPoint(29651200, -82326400);
	    overlayitem = new OverlayItem(point, "Loosey's", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    point = new GeoPoint(29650643, -82324995);
	    overlayitem = new OverlayItem(point, "Rockey's Piano Bar", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    point = new GeoPoint(29652770, -82333420);
	    overlayitem = new OverlayItem(point, "The Laboratory", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    point = new GeoPoint(29652330, -82326750);
	    overlayitem = new OverlayItem(point, "Durty Nelly's Pub", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    point = new GeoPoint(29651290, -82323840);
	    overlayitem = new OverlayItem(point, "The Lunchbox", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    point = new GeoPoint(29647314,-82324666);
	    overlayitem = new OverlayItem(point, "Civic Media Center", "Venue");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    mapOverlays.add(itemizedoverlay);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		myloc.enableMyLocation();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		myloc.disableMyLocation();
	}

}
