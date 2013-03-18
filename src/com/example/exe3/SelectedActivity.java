package com.example.exe3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SelectedActivity extends Activity {
	EditText txtErrand, txtPlace, txtNotes;
	String err, pla, not, newErr, newPla, newNot;
	int pos;
	ArrayList arr;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectedactivity);
        
        arr = new ArrayList();
        
        Bundle bundle = getIntent().getExtras();
        pos = bundle.getInt("pos");
        Reminder rem = (Reminder)bundle.getSerializable("reminder");
        
        if(bundle.getInt("notificationID") == 1) {
        	NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        	nm.cancel(1);
        }
        
        txtErrand = (EditText) findViewById(R.id.txtErrand);
        err = rem.getErrand();
        //err = bundle.getString("errand");
        txtErrand.setText(err);
        txtPlace = (EditText) findViewById(R.id.txtPlace);
        pla = rem.getPlace();
        //pla = bundle.getString("place");
        txtPlace.setText(pla);
        txtNotes = (EditText) findViewById(R.id.txtNotes);
        not = rem.getNotes();
        //not = bundle.getString("notes");
        txtNotes.setText(not);
        //pos = bundle.getInt("pos");
        //Toast.makeText(getBaseContext(), "errand: " + err + " place: " + pla + " notes: " + not, Toast.LENGTH_SHORT).show();
    }
    
    public void onClickSave(View view) {
    	newErr = txtErrand.getText().toString();
    	newPla = txtPlace.getText().toString();
    	newNot = txtNotes.getText().toString();
    	
    	Intent i = new Intent(this, Exe3Activity.class);
    	
    	if((!err.equals(newErr)) || (!pla.equals(newPla)) || (!not.equals(newNot))) {
    		loadReminder();
    		Reminder tmp = new Reminder(newErr, newPla, newNot);
    		arr.set(pos, tmp);
    		saveReminder();
    		
    		if((!pla.equals(newPla))) {
    			i.putExtra("updateReminder", true);
    			i.putExtra("updRemId", pos);
    		}
    		//Toast.makeText(getBaseContext(), "Filed updated successfully!", Toast.LENGTH_SHORT).show();
	    }
    	startActivity(i);
    	finish();
    }
    
    public void onClickDelete(View view) {
    	arr = loadReminder();
    	arr.remove(pos);
    	saveReminder();
    	//Toast.makeText(getBaseContext(), "Filed updated successfully!", Toast.LENGTH_SHORT).show();
    	Intent i = new Intent(this, Exe3Activity.class);
    	i.putExtra("deleteReminder", true);
    	i.putExtra("delRemId", pos);
    	startActivity(i);
    	finish();
    }
    
    public ArrayList loadReminder() {
    	ArrayList newArr = new ArrayList();
    	try {
    		FileInputStream fIn = openFileInput("reminders.txt");
    		InputStreamReader isr = new InputStreamReader(fIn);
    		BufferedReader reader = new BufferedReader(isr);
    		Reminder rem;
    		
    		int pos = 0;
    		int beg = 0;
    		int delim = 0;
    		int end = 0;
    		
    		StringBuilder sb;
    		String line, errand, place, notes;
    		while(((line = reader.readLine()) != null)) {
    			sb = new StringBuilder();
    			sb.append(line);
    			//Toast.makeText(getBaseContext(), "Read line: " + sb.toString() + " with length: " + sb.length() + " at pos: " + pos, Toast.LENGTH_SHORT).show();
    			end = sb.length();
    			
    			delim = sb.indexOf("|", beg);
    			errand = sb.substring(beg, delim);
    			//errands.add(errand);
    			beg = delim + 1;
    			
    			delim = sb.indexOf("|", beg);
    			place = sb.substring(beg, delim);
    			beg = delim + 1;
    			
    			notes = sb.substring(beg, end);
    		
    			//Toast.makeText(getBaseContext(), "Errand: " + errand + " place: " + place + " notes: " + notes, Toast.LENGTH_SHORT).show();
    			rem = new Reminder(errand, place, notes);
    			newArr.add(rem);
    			//Toast.makeText(getBaseContext(), "Nbr of elements in the arr: " + arr.size(), Toast.LENGTH_SHORT).show();
    			pos++;
    			beg = 0;
    			delim = 0;
    			end = 0;
    		}
    		reader.close();
    		isr.close();
    		
    		//Toast.makeText(getBaseContext(), "File loaded successfully!", Toast.LENGTH_SHORT).show();
    		
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    	
		return newArr;
    }
    
    public void saveReminder() {
    	String delim = "|";
    	
    	try {
    		deleteFile("reminders.txt");
    		FileOutputStream fOut = openFileOutput("reminders.txt", MODE_APPEND);
    		OutputStreamWriter osw = new OutputStreamWriter(fOut);
    		BufferedWriter writer = new BufferedWriter(osw);
    		
    		for(int i = 0; i < arr.size(); i++) {
    			Reminder tmp = (Reminder) arr.get(i);
    			writer.write(tmp.getErrand() + delim + tmp.getPlace() + delim + tmp.getNotes());
    			writer.newLine();
    		}
    		
    		writer.flush();
    		writer.close();
    		
    		//Toast.makeText(getBaseContext(), "Filed saved successfully!", Toast.LENGTH_SHORT).show();
    		
    		txtErrand.setText("");
    		txtPlace.setText("");
    		txtNotes.setText("");
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    }
}