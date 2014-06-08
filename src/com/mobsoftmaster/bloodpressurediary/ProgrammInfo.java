package com.mobsoftmaster.bloodpressurediary;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

public class ProgrammInfo extends TrackedActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programm_info);

		TextView textContact = (TextView) findViewById(R.id.textViewContact);
		textContact.setTextColor(0xff0000ff);
		textContact.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);

				emailIntent.setType("plain/text");
				// Кому
				Resources res = getResources();
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "bloodPressureDiaryApp@gmail.com" });
				// Зачем
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Blood pressure diary errors");
				// О чём
				ProgrammInfo.this.startActivity(Intent.createChooser(
						emailIntent, res.getString(R.string.mail_sanding)));
			}
		});
	}
}