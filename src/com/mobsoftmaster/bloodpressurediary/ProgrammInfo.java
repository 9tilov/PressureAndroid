package com.mobsoftmaster.bloodpressurediary;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

public class ProgrammInfo extends TrackedActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programm_info);

		TextView textViewInfo = (TextView) findViewById(R.id.textViewInfo);

	}
}