package com.example.pressure;

import java.io.File;

import android.R.bool;
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
	public static final String COLUMN_TXT = "txt";
	
	public static final String COLUMN_PULSE = "pulse";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_SYS_PRESSURE = "sys_pressure";
	public static final String COLUMN_DIAS_PRESSURE = "dias_pressure";
	public static final String COLUMN_UID = "uid";
 
	private static final String DB_CREATE = "create table " + DB_TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TXT
			+ " text" + ");";

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
	
	public boolean emptyDataBase () {
		String[] columns = new String[] { COLUMN_TXT };
		Cursor cursor = mDB.query(DB_TABLE, columns, null, null, null, null, null);
		if (cursor.getCount() == 0)
			return false;
		else 
			return true;
	}

	// получить все данные из таблицы DB_TABLE
	public Cursor getAllData() {
		return mDB.query(DB_TABLE, null, null, null, null, null, null);
	}

	public String getCurrentName(long id) {
		String[] columns = new String[] { COLUMN_TXT };
		Cursor cursor = mDB.query(DB_TABLE, columns, COLUMN_ID + "='"
				+ id + "'", null, null, null, null);
		String result = "";
		if (cursor != null) {
			cursor.moveToFirst();
			result = result + cursor.getString(0);
		}
		return result;
	}

	// добавить запись в DB_TABLE
	public void addRec(String txt) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TXT, txt);
		long rowID = mDB.insert(DB_TABLE, null, cv);
		Log.d(LOG_TAG, "row inserted, ID = " + rowID);
	}

	// удалить запись из DB_TABLE
	public void delRec(long id) {
		int delCount = mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
		Log.d(LOG_TAG, "deleted rows count = " + delCount);
	}

	// редактировать запись в DB_TABLE
	public void editRec(String txt, String id) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TXT, txt);
		mDB.update(DB_TABLE, cv, "_id = ?", new String[] { id });
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
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
