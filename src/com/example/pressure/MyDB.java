package com.example.pressure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDB {
	private static final String DB_NAME = "mydb";
	private static final int DB_VERSION = 1;

	private static final String DB_TABLE = "mytab";
	private static final String DB_TABLE_STAT = "mytabstat";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_EMAIL = "e_mail";

	public static final String COLUMN_PULSE = "pulse";
	public static final String COLUMN_SYS_PRESSURE = "sys";
	public static final String COLUMN_DIAS_PRESSURE = "dias";
	public static final String COLUMN_UID = "uid";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TIME = "time";

	private static final String DB_CREATE = "create table " + DB_TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME
			+ " text, " + COLUMN_EMAIL + " text" + ");";

	private static final String DB_STAT_CREATE = "create table "
			+ DB_TABLE_STAT + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_PULSE + " text, "
			+ COLUMN_SYS_PRESSURE + " text, " + COLUMN_DIAS_PRESSURE
			+ " text, " + COLUMN_UID + " text, " + COLUMN_DATE + " text, " + COLUMN_TIME + " text" + ");";

	private final Context mCtx;

	private DBHelper mDBHelper;
	private SQLiteDatabase mDB;
	final String LOG_TAG = "myLogs";

	public MyDB(Context ctx) {
		mCtx = ctx;
	}

	// открыть подключение
	public void open() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getWritableDatabase();
	}

	// закрыть подключение
	public void close() {
		if (mDBHelper != null)
			mDBHelper.close();
	}

	public boolean emptyDataBase() {
		String[] columns = new String[] { COLUMN_NAME };
		Cursor cursor = mDB.query(DB_TABLE, columns, null, null, null, null,
				null);
		if (cursor.getCount() == 0)
			return false;
		else
			return true;
	}

	// получить все данные из таблицы DB_TABLE
	public Cursor getAllData() {
		return mDB.query(DB_TABLE, null, null, null, null, null, null);
	}

	public Cursor getAllDataStat(String id) {
		return mDB.query(DB_TABLE_STAT, null, COLUMN_UID + "='" + id + "'",
				null, null, null, null);
	}

	public String[] getCurrentName(long id) {
		Cursor cursor = mDB.query(DB_TABLE, null, COLUMN_ID + "='" + id
				+ "'", null, null, null, null);
		String[] profile= new String[] {"", ""};
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < profile.length; ++i) {
				profile[i] = profile[i] + cursor.getString(i + 1);
			}
		}
		return profile;
	}
	
	public String[] getCurrentStat (long id) {
		Cursor cursor = mDB.query(DB_TABLE_STAT, null, COLUMN_ID + "='" + id
				+ "'", null, null, null, null);
		String[] statistics = new String[] {"", "", "", "", "", ""};
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < statistics.length; ++i) {
				statistics[i] = statistics[i] + cursor.getString(i + 1);
			}
		}
		return statistics;
	}
	
	// добавить запись в DB_TABLE
	public void addRec(String name, String e_mail) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME, name);
		cv.put(COLUMN_EMAIL, e_mail);
		long rowID = mDB.insert(DB_TABLE, null, cv);
		Log.d(LOG_TAG, "row inserted, ID = " + rowID);
	}

	public void addStat(String pulse, String sys, String dias, String uid, String date, String time) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PULSE, pulse);
		cv.put(COLUMN_SYS_PRESSURE, sys);
		cv.put(COLUMN_DIAS_PRESSURE, dias);
		cv.put(COLUMN_UID, uid);
		cv.put(COLUMN_DATE, date);
		cv.put(COLUMN_TIME, time);
		long rowID = mDB.insert(DB_TABLE_STAT, null, cv);
		Log.d(LOG_TAG, "row inserted, pulse = " + rowID);
	}

	// удалить запись из DB_TABLE
	public void delRec(long id) {
		int delCount = mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
		Log.d(LOG_TAG, "deleted rows count = " + delCount);
	}

	public void delRecStat(long id) {
		mDB.delete(DB_TABLE_STAT, COLUMN_ID + " = " + id, null);
	}

	// редактировать запись в DB_TABLE
	public void editRec(String name, String e_mail, String id) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME, name);
		cv.put(COLUMN_EMAIL, e_mail);
		mDB.update(DB_TABLE, cv, "_id = ?", new String[] { id });
	}

	public void editStat(String[] statistics, String id) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PULSE, statistics[0].toString());
		cv.put(COLUMN_SYS_PRESSURE, statistics[1].toString());
		cv.put(COLUMN_DIAS_PRESSURE, statistics[2].toString());
		mDB.update(DB_TABLE_STAT, cv, "_id = ?", new String[] { id });
	}
	
	// класс по созданию и управлению БД
	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		// создаем и заполняем БД
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE);
			db.execSQL(DB_STAT_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}