package com.mobsoftmaster.bloodpressurediary2;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {

	static final public String s_language = "language";
	static final public String s_id = "idName";
	static final public String s_rotation = "rotation";
	static final public String s_state = "state";
	static final public String s_tutorial = "tutrorial";
	static final public String s_notification = "notification";

	static public void SavePreferences(Context ctx, String key, Boolean value) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	static public boolean LoadPreference(Context ctx) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences("isProfileAdition",
				Context.MODE_PRIVATE);
		boolean value = sharedPreferences.getBoolean("isProfileAdition", false);
		return value;
	}

	static public void saveID(Context ctx, String key, int value) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.putInt(key, value);
		editor.commit();
	}

	/*
	 * Languages 0 - default 1 - english 2 - russian 3 - chinese
	 */
	static public void saveLanguage(Context ctx, String key, int value) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.putInt(key, value);
		editor.commit();
	}

	static public int LoadLanguage(Context ctx) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(
				s_language, Context.MODE_PRIVATE);
		int id = sharedPreferences.getInt(s_language, 0);
		return id;
	}

	static public int LoadID(Context ctx) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(s_id,
				Context.MODE_PRIVATE);
		int id = sharedPreferences.getInt(s_id, 1);
		return id;
	}

	static public boolean LoadRotation(Context ctx) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(
				s_rotation, Context.MODE_PRIVATE);
		boolean rotation = sharedPreferences.getBoolean(s_rotation, false);
		return rotation;
	}

	static public boolean LoadNotification(Context ctx) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(
				s_notification, Context.MODE_PRIVATE);
		boolean notification = sharedPreferences.getBoolean(s_notification,
				false);
		return notification;
	}

	static public boolean LoadState(Context ctx) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(s_state,
				Context.MODE_PRIVATE);
		boolean state = sharedPreferences.getBoolean(s_state, true);
		return state;
	}

	static public boolean LoadTutorial(Context ctx) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(
				s_tutorial, Context.MODE_PRIVATE);
		boolean state = sharedPreferences.getBoolean(s_tutorial, true);
		return state;
	}

}
