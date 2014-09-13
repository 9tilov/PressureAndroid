package com.mobsoftmaster.bloodpressurediary2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreference {

	private Context mCtx;

	public String s_language = "language";
	public String s_id = "idName";
	public String s_rotation = "rotation";
	public String s_state = "state";
	public String s_tutorial = "tutrorial";

	public boolean notification = false;
	public String s_notification = "notification";

	public SharedPreference(Context ctx) {
		mCtx = ctx;
	}

	public void SavePreferences(String key, Boolean value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void saveID(String key, int value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/*
	 * Languages 0 - default 1 - english 2 - russian 3 - chinese
	 */
	public void saveLanguage(String key, int value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int LoadLanguage() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		int id = sharedPreferences.getInt(s_language, 0);
		return id;
	}

	public int LoadID() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		int id = sharedPreferences.getInt(s_id, 1);
		return id;
	}

	public boolean LoadRotation() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		boolean rotation = sharedPreferences.getBoolean(s_rotation, false);
		return rotation;
	}

	public boolean LoadNotification() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		boolean notification = sharedPreferences.getBoolean(s_notification,
				false);
		return notification;
	}

	public boolean LoadState() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		boolean state = sharedPreferences.getBoolean(s_state, true);
		return state;
	}

	public boolean LoadTutorial() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		boolean state = sharedPreferences.getBoolean(s_tutorial, true);
		return state;
	}

}
