package com.mobsoftmaster.bloodpressurediary;

import java.util.Locale;

import android.app.Application;
import android.content.res.Configuration;

public class Locales extends Application {

	SharedPreference sharedPref;

	final String LOG_TAG = "myLogs";

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLocale();
	}

	private void setLocale() {
		Configuration c = new Configuration(getResources().getConfiguration());

		sharedPref = new SharedPreference(this);

		int language = sharedPref.LoadLanguage();

		switch (language) {
		case 0:
			c.locale = Locale.ENGLISH;
			break;
		case 1:
			Locale myLocale = new Locale("ru", "RU");
			c.locale = myLocale;
			break;
		case 2:
			c.locale = Locale.CHINESE;
			break;
		}

		getResources().updateConfiguration(c,
				getResources().getDisplayMetrics());
	}
}
