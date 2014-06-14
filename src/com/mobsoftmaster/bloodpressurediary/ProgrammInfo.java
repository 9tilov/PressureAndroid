package com.mobsoftmaster.bloodpressurediary;

import android.os.Bundle;
import android.widget.TextView;

public class ProgrammInfo extends TrackedActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programm_info);

		TextView textViewInfo = (TextView) findViewById(R.id.textViewInfo);

	}
}