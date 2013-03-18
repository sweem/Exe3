package com.example.exe3;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
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

public class NextActivity extends Activity {
	EditText txtErrand, txtPlace, txtNotes;
	String errand, place, notes;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nextactivity);
        
        txtErrand = (EditText) findViewById(R.id.txtErrand);
        txtPlace = (EditText) findViewById(R.id.txtPlace);        
        txtNotes = (EditText) findViewById(R.id.txtNotes);        
    }
    
    public void onClick(View view) {
    	errand = txtErrand.getText().toString();
    	place = txtPlace.getText().toString();
    	notes = txtNotes.getText().toString();
    	
    	String delim = "|";
    	
    	try {
    		FileOutputStream fOut = openFileOutput("reminders.txt", MODE_APPEND);
    		OutputStreamWriter osw = new OutputStreamWriter(fOut);
    		BufferedWriter writer = new BufferedWriter(osw);
    		
    		writer.write(errand + delim + place + delim  + notes);
    		writer.newLine();
    		writer.flush();
    		writer.close();
    		
    		//Toast.makeText(getBaseContext(), "Filed saved successfully!", Toast.LENGTH_SHORT).show();
    		
    		txtErrand.setText("");
    		txtPlace.setText("");
    		txtNotes.setText("");
    	}catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    		
    	startActivity(new Intent(this, Exe3Activity.class));
    	finish();
    }
}