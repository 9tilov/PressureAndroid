package com.example.second_screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends MainActivity
{
	static String profile_id;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
//		profile_id = getIntent().getStringExtra("lvData");
//		Intent intent = new Intent(AboutActivity.this, MainActivity.class);
//        intent.putExtra("lvData1", String.valueOf(cnt));
    }
    
    public void onClick(View v)
	{
    	cnt = 0;
    	saveText(cnt);
    	Log.d(LOG_TAG, "count2 = " + cnt);
//    	cnt = Long.parseLong(profile_id);
    	//a = loadText();
    	super.onBackPressed();
    	
//    	a = 0;
//    	Intent intent = new Intent(AboutActivity.this, MainActivity.class);
//      intent.putExtra("lvData1", String.valueOf(cnt));
	}
    
    
    
}