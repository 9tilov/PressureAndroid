package com.mobsoftmaster.bloodpressurediary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreference {
	
	private Context mCtx;
	
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
	
	public int LoadID() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		int id = sharedPreferences.getInt("idName", 1);
		return id;
	}
	
	public boolean LoadRotation() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		boolean rotation = sharedPreferences.getBoolean("rotation", false);
		return rotation;
	}
	
	public boolean LoadNotification() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		boolean notification = sharedPreferences.getBoolean("notification", false);
		return notification;
	}
	
	public boolean LoadState() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(mCtx);
		boolean state = sharedPreferences.getBoolean("state", true);
		return state;
	}

}
