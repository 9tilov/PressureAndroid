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

		final String[] viewName_ru = new String[] { "ic_screen_title_ru",
				"ic_screen_profile_ru", "ic_screen_profile_add_ru",
				"ic_screen_stat_ru", "ic_screen_stat_add_ru",
				"ic_screen_graph_ru", "ic_screen_settings_ru" };

		final String[] viewNameLand_ru = new String[] {
				"ic_screen_title_land_ru", "ic_screen_profile_land_ru",
				"ic_screen_profile_add_land_ru", "ic_screen_stat_land_ru",
				"ic_screen_stat_add_land_ru", "ic_screen_graph_ru",
				"ic_screen_settings_land_ru" };

		final String[] viewName_en = new String[] { "ic_screen_title_en",
				"ic_screen_profile_en", "ic_screen_profile_add_en",
				"ic_screen_stat_en", "ic_screen_stat_add_en",
				"ic_screen_graph_en", "ic_screen_settings_en" };

		final String[] viewNameLand_en = new String[] {
				"ic_screen_title_land_en", "ic_screen_profile_land_en",
				"ic_screen_profile_add_land_en", "ic_screen_stat_land_en",
				"ic_screen_stat_add_land_en", "ic_screen_graph_en",
				"ic_screen_settings_land_en" };

		final String locale_define = Locale.getDefault().getLanguage();
		Log.d(LOG_TAG, "LANG1 = " + locale_define);

		int resID = 0;
		if (locale_define.equals("ru")) {

			Log.d(LOG_TAG, "LANG2 = " + locale_define + " - ru");
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				resID = getResources().getIdentifier("ic_screen_title_land_ru",
						"raw", getPackageName());
			else
				resID = getResources().getIdentifier("ic_screen_title_ru",
						"raw", getPackageName());
		} else {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				resID = getResources().getIdentifier("ic_screen_title_land_en",
						"raw", getPackageName());
			else
				resID = getResources().getIdentifier("ic_screen_title_en",
						"raw", getPackageName());
		}

		screen.setImageResource(resID);

		if (i == 0)
			btnPrevious.setVisibility(View.INVISIBLE);

		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int resID;
				if (locale_define.equals("ru")) {
					if (i == viewName_ru.length - 1) {
						sharedPref
								.SavePreferences(sharedPref.s_tutorial, false);
						onBackPressed();
					} else {
						i++;
						btnPrevious.setVisibility(View.VISIBLE);

						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							resID = getResources()
									.getIdentifier(viewNameLand_ru[i], "raw",
											getPackageName());
						} else
							resID = getResources().getIdentifier(
									viewName_ru[i], "raw", getPackageName());
						screen.setImageResource(resID);
					}
				} else {
					if (i == viewName_en.length - 1) {
						sharedPref
								.SavePreferences(sharedPref.s_tutorial, false);
						onBackPressed();
					} else {
						i++;
						btnPrevious.setVisibility(View.VISIBLE);

						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							resID = getResources()
									.getIdentifier(viewNameLand_en[i], "raw",
											getPackageName());
						} else
							resID = getResources().getIdentifier(
									viewName_en[i], "raw", getPackageName());
						screen.setImageResource(resID);
					}
				}
			}
		});

		btnPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (i > 0) {
					if (i == 1)
						btnPrevious.setVisibility(View.INVISIBLE);
					i--;
					int resID;
					if (locale_define.equals("ru")) {
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							resID = getResources()
									.getIdentifier(viewNameLand_ru[i], "raw",
											getPackageName());
						} else
							resID = getResources().getIdentifier(
									viewName_ru[i], "raw", getPackageName());
						screen.setImageResource(resID);
					} else {
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							resID = getResources()
									.getIdentifier(viewNameLand_en[i], "raw",
											getPackageName());
						} else
							resID = getResources().getIdentifier(
									viewName_en[i], "raw", getPackageName());
						screen.setImageResource(resID);
					}
				}
			}
		});
	}
}
