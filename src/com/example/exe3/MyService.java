package com.example.exe3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
	String ALERT = "PROX_ALERT";
	int notificationID = 1;
	int delRemId, updRemId;
	ArrayList arr;
	
	private final IBinder binder = new MyBinder();
	
	public class MyBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
		
		if(delRemId > -1) {
			//Toast.makeText(getBaseContext(), "Deleting id: " + delRemId, Toast.LENGTH_SHORT).show();
			removeProximityAlert(delRemId);
		}
		
		if(updRemId > -1) {
			//Toast.makeText(getBaseContext(), "Updating id: " + updRemId, Toast.LENGTH_SHORT).show();
			removeProximityAlert(updRemId);
			setProximityAlert(updRemId);
		}
		
		if(arr.size() > 0) {
			//Toast.makeText(getBaseContext(), "SetProximityAlerts", Toast.LENGTH_SHORT).show();
			setProximityAlerts();
		}
		
		registerReceiver(proxIntentReceiver, new IntentFilter(ALERT));
		Toast.makeText(getBaseContext(), "Registered broadcastreceiver", Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}
	
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(proxIntentReceiver);
		Toast.makeText(this, "Service destroyed", Toast.LENGTH_LONG).show();
	}
	
	public void setProximityAlerts() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		for(int i = 0; i < arr.size(); i++) {
			Reminder rem = (Reminder) arr.get(i);
			if(rem.isOnAlert() == false) {
				//Toast.makeText(getBaseContext(), "Rem with id: " + i + " is offAlert.", Toast.LENGTH_SHORT).show();
				GeoPoint p = getGeoPoint(rem.getPlace());
				double lat = p.getLatitudeE6() / 1E6;
				double lon = p.getLongitudeE6() / 1E6;
				Intent idInt = new Intent(ALERT);
				idInt.putExtra("pos", i);
				PendingIntent proxIntent = PendingIntent.getBroadcast(getBaseContext(), i, idInt, PendingIntent.FLAG_UPDATE_CURRENT);
				lm.addProximityAlert(lat, lon, 5, -1, proxIntent);
				rem.setOnAlert();
			}
		}
	}
	
	public void setProximityAlert(int i) {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Reminder rem = (Reminder) arr.get(i);
		GeoPoint p = getGeoPoint(rem.getPlace());
		double lat = p.getLatitudeE6() / 1E6;
		double lon = p.getLongitudeE6() / 1E6;
		Intent idInt = new Intent(ALERT);
		idInt.putExtra("pos", i);
		PendingIntent proxIntent = PendingIntent.getBroadcast(getBaseContext(), i, idInt, PendingIntent.FLAG_UPDATE_CURRENT);
		lm.addProximityAlert(lat, lon, 5, -1, proxIntent);
		rem.setOnAlert();
	}
	
	public void removeProximityAlert(int i) {
		//Toast.makeText(getBaseContext(), "Deleting rem with id: " + i, Toast.LENGTH_SHORT).show();
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Intent idInt = new Intent(ALERT);
		idInt.putExtra("pos", i);
		PendingIntent proxIntent = PendingIntent.getBroadcast(getBaseContext(), i, idInt, PendingIntent.FLAG_UPDATE_CURRENT);
		lm.removeProximityAlert(proxIntent);
	}
	
	private BroadcastReceiver proxIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			int index = extras.getInt("pos");
			//Toast.makeText(getBaseContext(), , Toast.LENGTH_SHORT).show();
			Reminder rem = (Reminder) arr.get(index);
			Intent i = new Intent(getBaseContext(), SelectedActivity.class);
			i.putExtra("reminder", rem);
			i.putExtra("notificationID", notificationID);
			i.putExtra("pos", index);
			//Toast.makeText(getBaseContext(), "pos: " + index + " errand: " + rem.getErrand() + " place: " + rem.getPlace() + " notes: " + rem.getNotes(), Toast.LENGTH_SHORT).show();
    		PendingIntent notIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    		NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    		Notification notif = new Notification(R.drawable.ic_launcher, "Reminder: " + rem.getErrand(), System.currentTimeMillis());
    		notif.setLatestEventInfo(context, "System Alarm", rem.notes, notIntent);
    		notif.vibrate = new long[] {100, 250, 100, 500};
    		nm.notify(notificationID, notif);
		}
    };
	
	public GeoPoint getGeoPoint(String place) {
		Geocoder geo = new Geocoder(this, Locale.getDefault());
		
		GeoPoint p = null;
		try {
			List<Address> addresses = geo.getFromLocationName(place, 5);
			
			if(addresses.size() > 0) {
				p = new GeoPoint((int) (addresses.get(0).getLatitude() * 1E6), (int) (addresses.get(0).getLongitude() * 1E6));
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return p;
	}
}
