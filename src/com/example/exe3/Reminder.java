package com.example.exe3;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

public class Reminder implements Serializable {
	String errand, place, notes;
	GeoPoint p;
	Boolean onAlert;
	
	public Reminder (String err, String pla, String not) {
		errand = err;
		place = pla;
		notes = not;
		onAlert = false;
	}
	
	public String getErrand() {
		return errand;
	}
	
	public String getPlace() {
		return place;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setOnAlert() {
		onAlert = true;
	}
	
	public void setOffAlert() {
		onAlert = false;
	}
	
	public Boolean isOnAlert() {
		return onAlert;
	}
}
