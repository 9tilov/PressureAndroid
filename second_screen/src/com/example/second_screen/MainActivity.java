package com.example.second_screen;

import android.os.Bundle;


import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class MainActivity extends Activity {
	
	public static final String SAVED_TEXT = "saved text";
	String profile_id;
	long cnt = 0;
	long a = 0;
	final String LOG_TAG = "myLogs";
	int mCurrentScore, mCurrentLevel;
	SharedPreferences sPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		a = loadText();
		Log.d(LOG_TAG, "a = " + a);
		if (a == 0) 
			setContentView(R.layout.activity_main);
		else {
			setContentView(R.layout.activity_main);
			Intent intent = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v)
	{
	    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
	    intent.putExtra("lvData", String.valueOf(cnt));
	    
	    cnt = 1;
	    
	    startActivity(intent);
	    saveText(cnt);
	    Log.d(LOG_TAG, "count = " + cnt);
	}
	
	
	void saveText(long cnt) {
	    sPref = getPreferences(MODE_PRIVATE);
	    Editor ed = sPref.edit();
	    ed.putLong(SAVED_TEXT, cnt);
	    ed.commit();
	    Log.d(LOG_TAG, "cnt = " + cnt);
	  }
	
	long loadText() {
	    sPref = getPreferences(MODE_PRIVATE);
	    a = sPref.getLong(SAVED_TEXT, 0);
	    Log.d(LOG_TAG, "string = " + a);
	    return a;
	    
	  }
	
}
