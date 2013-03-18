package com.example.exe3;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Exe3Activity extends ListActivity {
	String[] errands;
	ArrayList arr, delArr;
	
	MyService serviceBinder;
	Intent iCon;
	int delRemId, updRemId;
	
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceBinder = ((MyService.MyBinder)service).getService();
			//Toast.makeText(getBaseContext(), "ServiceConnected", Toast.LENGTH_SHORT).show();
			serviceBinder.arr = arr;
			serviceBinder.delRemId = delRemId;
			serviceBinder.updRemId = updRemId;
			startService(iCon);
		}
		
		public void onServiceDisconnected(ComponentName name) {
			serviceBinder = null;
			//Toast.makeText(getBaseContext(), "ServiceDisconnected", Toast.LENGTH_SHORT).show();
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        delRemId = -1;
        updRemId = -1;
        
        Bundle b = getIntent().getExtras();
        if(b != null) {
        	//Toast.makeText(this, "Has extras", Toast.LENGTH_SHORT).show();
	        if(b.getInt("notificationID") == 1) {
	            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	            nm.cancel(b.getInt("notificationID"));
	        }
	        else if(b.getBoolean("deleteReminder") == true) {
	        	delRemId = b.getInt("delRemId");
	        	//Toast.makeText(getBaseContext(), "DelRemId in onCreate: " + delRemId, Toast.LENGTH_SHORT).show();
	        }
	        else if(b.getBoolean("updateReminder") == true) {
	        	updRemId = b.getInt("updRemId");
	        	//Toast.makeText(getBaseContext(), "UpdRemID in onCreate: " + updRemId, Toast.LENGTH_SHORT).show();
	        }
        }
        
        arr = new ArrayList();
        loadReminder();
        
		errands = new String[arr.size()];
		
		for(int i = 0; i < arr.size(); i++) {
			Reminder rem = (Reminder) arr.get(i);
			errands[i] = rem.getErrand();
		}
        
        ListView lstV = getListView();
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, errands));
        
        startService();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	CreateMenu(menu);
		return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	return MenuChoice(item);
    }
    
    private void CreateMenu(Menu menu) {
    	MenuItem mnu1 = menu.add(0, 0, 0, "New Reminder");
    	{
    		mnu1.setAlphabeticShortcut('a');
    		mnu1.setIcon(R.drawable.ic_launcher);
    	}
    	MenuItem mnu2 = menu.add(0, 1, 1, "Quit");
    	{
    		mnu2.setAlphabeticShortcut('b');
    		mnu2.setIcon(R.drawable.ic_launcher);
    	}
    }
    
    private boolean MenuChoice(MenuItem item) {
    	switch(item.getItemId()) {
    	case 0:
    		//Toast.makeText(this, "You clicked on New Reminder", Toast.LENGTH_LONG).show();
        	startActivity(new Intent(this, NextActivity.class));
        	finish();
    		return true;
    	case 1:
    		//Toast.makeText(this, "You clicked on Quit", Toast.LENGTH_LONG).show();
    		finish();
    		return true;
    	}
		return false;
    }
    
    public void startService() { 	
    	iCon = new Intent(Exe3Activity.this, MyService.class);
    	bindService(iCon, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onListItemClick(ListView p, View v, int pos, long id) {
    	Intent i = new Intent(this, SelectedActivity.class);
    	Reminder tmp = (Reminder) arr.get(pos);
    	i.putExtra("reminder", tmp);
    	i.putExtra("pos", pos);
        startActivity(i);
        finish();
    }
    
    public void loadReminder() {
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
    			end = sb.length();
    			
    			delim = sb.indexOf("|", beg);
    			errand = sb.substring(beg, delim);
    			beg = delim + 1;
    			
    			delim = sb.indexOf("|", beg);
    			place = sb.substring(beg, delim);
    			beg = delim + 1;
    			
    			notes = sb.substring(beg, end);
 
    			rem = new Reminder(errand, place, notes);
    			arr.add(rem);
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
    }
}