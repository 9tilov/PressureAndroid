package com.mobsoftmaster.bloodpressurediary2;

import java.util.HashMap;
import java.util.Locale;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mobsoftmaster.bloodpressurediary2.R;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class Locales extends Application {

	// The following line should be changed to include the correct property id.
	private static final String PROPERTY_ID = "UA-44823311-5";

	final String LOG_TAG = "myLogs";
	public static int GENERAL_TRACKER = 0;

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
						// roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
							// company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLocale();
	}

	private void setLocale() {
		Configuration c = new Configuration(getResources().getConfiguration());

		int language = SharedPreference.LoadLanguage(this);

		switch (language) {
		case 0:
			c.locale = Locale.getDefault();
			break;
		case 1:
			c.locale = Locale.ENGLISH;
			break;
		case 2:
			Locale myLocale = new Locale("ru", "RU");
			c.locale = myLocale;
			break;
		case 3:
			c.locale = Locale.CHINESE;
			break;
		}

		getResources().updateConfiguration(c,
				getResources().getDisplayMetrics());
	}

	synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
					.newTracker(R.xml.app_tracker) : analytics
					.newTracker(PROPERTY_ID);
			// : analytics.newTracker(R.xml.ecommerce_tracker);
			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}
}
