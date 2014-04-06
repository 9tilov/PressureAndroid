package com.example.pressure;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;

public class Graph extends TabActivity {

	String stat_id;
	String count_data_string;

	MyDB db;

	GraphView graphView;

	int period = 0;

	Button btnWeek, btnMonth, btn3Month, btnAll;

	final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);

		stat_id = getIntent().getStringExtra("id_stat_key");
		count_data_string = getIntent().getStringExtra("id_stat_count");

		Log.d(LOG_TAG, "stat_id= " + stat_id);
		db = new MyDB(this);
		db.open();
		TabHost tabHost = getTabHost();

		TabHost.TabSpec tabSpec;

		Intent intentWeek = new Intent(Graph.this, GraphWeek.class);
		Intent intentMonth = new Intent(Graph.this, GraphMonth.class);
		Intent intent3Month = new Intent(Graph.this, Graph3Month.class);
		Intent intentAllPeriod = new Intent(Graph.this, GraphAllPeriod.class);

		tabSpec = tabHost.newTabSpec("tag1");
		tabSpec.setIndicator("all period");
		intentAllPeriod.putExtra("id_stat_key_all_period", stat_id);
		intentAllPeriod.putExtra("id_stat_count_all_period",
				String.valueOf(count_data_string));
		tabSpec.setContent(intentAllPeriod);
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("tag2");
		tabSpec.setIndicator("week");
		intentWeek.putExtra("id_stat_key_week", stat_id);
		intentWeek.putExtra("id_stat_count_week",
				String.valueOf(count_data_string));
		tabSpec.setContent(intentWeek);
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("tag3");
		tabSpec.setIndicator("month");
		intentMonth.putExtra("id_stat_key_month", stat_id);
		intentMonth.putExtra("id_stat_count_month",
				String.valueOf(count_data_string));
		tabSpec.setContent(intentMonth);
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("tag4");
		tabSpec.setIndicator("3 months");
		intent3Month.putExtra("id_stat_key_3_month", stat_id);
		intent3Month.putExtra("id_stat_count_3_month",
				String.valueOf(count_data_string));
		tabSpec.setContent(intent3Month);
		tabHost.addTab(tabSpec);

	}

	protected void onDestroy() {
		// закрываем подключение при выходе
		db.close();
		super.onDestroy();
	}

}
