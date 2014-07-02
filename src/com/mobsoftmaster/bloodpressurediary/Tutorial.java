package com.mobsoftmaster.bloodpressurediary;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Tutorial extends Activity {

	final String LOG_TAG = "myLogs";
	int i = 0;

	SharedPreference sharedPref;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial);

		final ImageView screen = (ImageView) findViewById(R.id.imageViewScreenProfile);
		ImageView btnNext = (ImageView) findViewById(R.id.imageViewNext);
		final ImageView btnPrevious = (ImageView) findViewById(R.id.imageViewPrevious);

		sharedPref = new SharedPreference(this);

		final String[] viewName = new String[] { "ic_screen_title_",
				"ic_screen_profile_", "ic_screen_profile_add_",
				"ic_screen_stat_", "ic_screen_stat_add_", "ic_screen_graph_",
				"ic_screen_settings_", "ic_title_end_" };

		final String locale_define = Locale.getDefault().getLanguage();
		Log.d(LOG_TAG, "LANG1 = " + locale_define);

		if (locale_define.equals("ru")) {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				showView(viewName[0] + "land_ru");
			else
				showView(viewName[0] + "ru");
		} else {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				showView(viewName[0] + "land_en");
			else
				showView(viewName[0] + "en");
		}

		if (i == 0)
			btnPrevious.setVisibility(View.INVISIBLE);

		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (i == viewName.length - 1) {
					sharedPref.SavePreferences(sharedPref.s_tutorial, false);
					onBackPressed();
				} else {
					i++;
					btnPrevious.setVisibility(View.VISIBLE);
					if (locale_define.equals("ru")) {
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
							showView(viewName[i] + "land_ru");
						else
							showView(viewName[i] + "ru");
					} else {
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							showView(viewName[i] + "land_en");
						} else
							showView(viewName[i] + "en");
					}
				}
			}
		});

		btnPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (i == 1)
					btnPrevious.setVisibility(View.INVISIBLE);
				i--;
				if (locale_define.equals("ru")) {
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						showView(viewName[i] + "land_ru");
					} else
						showView(viewName[i] + "ru");
				} else {
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
						showView(viewName[i] + "land_en");
					} else
						showView(viewName[i] + "en");
				}
			}
		});
	}

	void showView(String viewName) {
		final ImageView screen = (ImageView) findViewById(R.id.imageViewScreenProfile);
		int resID = getResources().getIdentifier(viewName, "raw",
				getPackageName());
		screen.setImageResource(resID);
	}
}
