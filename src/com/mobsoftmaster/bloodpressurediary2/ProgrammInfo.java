package com.mobsoftmaster.bloodpressurediary2;

import com.mobsoftmaster.bloodpressurediary2.R;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgrammInfo extends TrackedActivity {

	String TAG = "myLogs";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programm_info);

		Typeface font = Typeface.createFromAsset(getAssets(), "Dashley.ttf");
		TextView textViewInfo = (TextView) findViewById(R.id.textViewInfo);
		textViewInfo.setTypeface(font);

		ImageView imageViewReview = (ImageView) findViewById(R.id.imageViewReview);
		imageViewReview.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Uri uri = Uri
						.parse("https://play.google.com/store/apps/details?id=com.mobsoftmaster.bloodpressurediary2");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.close_window_start,
				R.anim.close_window_end);
	}
}