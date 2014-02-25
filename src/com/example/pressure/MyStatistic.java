package com.example.pressure;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyStatistic extends Activity {
	private TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);
        name = (TextView) findViewById(R.id.profile_name);
        String profile_name = getIntent().getStringExtra("lvData");
        name.setText(profile_name);
    }
}