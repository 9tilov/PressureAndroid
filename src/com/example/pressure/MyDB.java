package com.example.pressure;

import java.util.LinkedList;

import android.R.string;
import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class MyDB {
	private static final String DB_NAME = "mydb";
	private static final int DB_VERSION = 1;

	private static final String DB_TABLE = "mytab";
	private static final String DB_TABLE_STAT = "mytabstat";
	private static final String DB_TABLE_NOTIF = "mytabnotif";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_EMAIL = "e_mail";

	public static final String COLUMN_PULSE = "pulse";
	public static final String COLUMN_SYS_PRESSURE = "sys";
	public static final String COLUMN_DIAS_PRESSURE = "dias";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_UID = "uid";

	public static final String COLUMN_NOTIF_MESSAGE = "notif_message";
	public static final String COLUMN_NOTIF_HOUR = "notif_hour";
	public static final String COLUMN_NOTIF_MINUTE = "notif_minute";

	private static final String DB_CREATE = "create table " + DB_TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME
			+ " text, " + COLUMN_EMAIL + " text" + ");";

	private static final String DB_STAT_CREATE = "create table "
			+ DB_TABLE_STAT + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_PULSE + " text, "
			+ COLUMN_SYS_PRESSURE + " text, " + COLUMN_DIAS_PRESSURE
			+ " text, " + COLUMN_DATE + " text, " + COLUMN_TIME + " text, "
			+ COLUMN_UID + " text" + ");";

	private static final String DB_CREATE_NOTIF = "create table "
			+ DB_TABLE_NOTIF + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NOTIF_MESSAGE
			+ " text, " + COLUMN_NOTIF_HOUR + " text, " + COLUMN_NOTIF_MINUTE
			+ " text" + ");";

	private final Context mCtx;

	private DBHelper mDBHelper;
	private SQLiteDatabase mDB;
	final String LOG_TAG = "myLogs";
	
	MainActivity mainActivity;

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
		if (!mDB.isOpen()) {
			return null;
		}
		return mDB.query(DB_TABLE_STAT, null, COLUMN_UID + "='" + id + "'",
				null, null, null, null);
	}

	public Cursor getAllDataNotif() {
		return mDB.query(DB_TABLE_NOTIF, null, null, null, null, null, null);
	}

//	public void getAllNotifValues(int notification, AlarmManager am) {
//		mainActivity = new MainActivity();
//		Cursor cursor = mDB.query(DB_TABLE_NOTIF, null, null, null, null, null,
//				null);
//		String[] data_notif_fields = new String[] { "", "", "", "" };
//		for (int i = 0; i < getCountElementsSettings(); ++i) {
//			data_notif_fields[i] = "";
//		}
//
//		String[] data_notif = new String[] { "", "", "" };
//
//		LinkedList<String[]> list = new LinkedList<String[]>();
//
//		cursor = getAllDataNotif();
//		for (int i = 0; i < getCountElementsSettings(); ++i) {
//			if (cursor != null) {
//				cursor.moveToNext();
//				data_notif_fields[i] = data_notif_fields[i]
//						+ cursor.getString(0);
//			}
//
//			data_notif = getCurrentNotif(Long.valueOf(data_notif_fields[i]));
//			list.add(data_notif);
//			
//			if ((notification == 0))
//				mainActivity.setRepeatingAlarm(am, Integer.valueOf(data_notif_fields[i]),
//						list.get(i)[0], Integer.valueOf(list.get(i)[1]),
//						Integer.valueOf(list.get(i)[2]), notification);
//		}
//	}

	public String[] getCurrentName(long id) {
		Cursor cursor = mDB.query(DB_TABLE, null, COLUMN_ID + "='" + id + "'",
				null, null, null, null);
		String[] profile = new String[] { "", "" };
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < profile.length; ++i) {
				profile[i] = profile[i] + cursor.getString(i + 1);
			}
		}
		return profile;
	}

	public int getCountElementsStat() {
		String sql = "SELECT COUNT(*) FROM " + DB_TABLE_STAT;
		SQLiteStatement statement = mDB.compileStatement(sql);
		int count = (int) statement.simpleQueryForLong();
		return count;
	}

	public int getCountElementsSettings() {
		String sql = "SELECT COUNT(*) FROM " + DB_TABLE_NOTIF;
		SQLiteStatement statement = mDB.compileStatement(sql);
		int count = (int) statement.simpleQueryForLong();
		return count;
	}

	public LinkedList<String[]> getStat(long id, int period) {

		LinkedList<String[]> list = new LinkedList<String[]>();

		int count = getCountElementsStat();

		Cursor cursor = mDB.query(DB_TABLE_STAT, null, COLUMN_UID + "='" + id
				+ "'", null, null, null, null);
		String[] tempPulse = new String[count];
		String[] tempSys = new String[count];
		String[] tempDias = new String[count];
		String[] pulse = new String[period];
		String[] sys = new String[period];
		String[] dias = new String[period];

		Log.d(LOG_TAG, "COUNT = " + count);
		if (cursor != null) {
			cursor.moveToFirst();
			for (int j = 0; j < count; ++j) {
				tempPulse[j] = cursor.getString(1);
				tempSys[j] = cursor.getString(2);
				tempDias[j] = cursor.getString(3);
				cursor.moveToNext();
			}
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = count - period; j < count; ++j) {
				pulse[j - (count - period)] = tempPulse[j];
				sys[j - (count - period)] = tempSys[j];
				dias[j - (count - period)] = tempDias[j];
			}
			list.add(pulse);
			list.add(sys);
			list.add(dias);
		}
		return list;
	}

	public String[] getCurrentStat(long id) {
		Cursor cursor = mDB.query(DB_TABLE_STAT, null, COLUMN_ID + "='" + id
				+ "'", null, null, null, null);
		String[] statistics = new String[] { "", "", "", "", "", "" };
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < statistics.length; ++i) {
				statistics[i] = statistics[i] + cursor.getString(i + 1);
			}
		}
		return statistics;
	}

	public String[] getCurrentNotif(long id) {
		Cursor cursor = mDB.query(DB_TABLE_NOTIF, null, COLUMN_ID + "='" + id
				+ "'", null, null, null, null);
		String[] notif = new String[] { "", "", "" };
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < notif.length; ++i) {
				notif[i] = notif[i] + cursor.getString(i + 1);
			}
		}
		return notif;
	}

	// добавить запись в DB_TABLE
	public void addRec(String name) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME, name);
		cv.put(COLUMN_EMAIL, "");
		long rowID = mDB.insert(DB_TABLE, null, cv);
		Log.d(LOG_TAG, "row inserted, ID = " + rowID);
	}

	public void addStat(String pulse, String sys, String dias, String uid,
			String date, String time) {
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

	public void addNotif(String message, String hour, String minute) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NOTIF_MESSAGE, message);
		cv.put(COLUMN_NOTIF_HOUR, hour);
		cv.put(COLUMN_NOTIF_MINUTE, minute);
		long rowID = mDB.insert(DB_TABLE_NOTIF, null, cv);
	}

	// удалить запись из DB_TABLE
	public void delRec(long id) {
		int delCount = mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
		Log.d(LOG_TAG, "deleted rows count = " + delCount);
	}

	public void delRecStat(long id) {
		mDB.delete(DB_TABLE_STAT, COLUMN_ID + " = " + id, null);
	}

	public void delRecAllStat(long id) {
		mDB.delete(DB_TABLE_STAT, COLUMN_UID + " = " + id, null);
	}

	public void delRecNotif(long id) {
		mDB.delete(DB_TABLE_NOTIF, COLUMN_ID + " = " + id, null);
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

	public void editNotif(String notif, String hour, String minute, String id) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NOTIF_MESSAGE, notif);
		cv.put(COLUMN_NOTIF_HOUR, hour);
		cv.put(COLUMN_NOTIF_MINUTE, minute);
		mDB.update(DB_TABLE_NOTIF, cv, "_id = ?", new String[] { id });
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
			db.execSQL(DB_CREATE_NOTIF);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}