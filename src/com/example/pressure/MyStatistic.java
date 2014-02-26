package com.example.pressure;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyStatistic extends Activity {
	private TextView name;
	MyDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);
        
        db = new MyDB(this);
		db.open();
		
        name = (TextView) findViewById(R.id.profile_name);
        String profile_id = getIntent().getStringExtra("lvData");
        String profile_name = db.getCurrentName(Long.parseLong(profile_id));
        name.setText(profile_name);
    }
    protected void onDestroy() {
		super.onDestroy();
		// закрываем подключение при выходе
		db.close();
	}
}