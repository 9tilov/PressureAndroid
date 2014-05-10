package com.example.pressure;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import android.R.integer;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private static final int CM_EDIT_ID = 0, CM_DELETE_ID = 1;

	static SharedPreferences sPref;

	MyDB db;
	SimpleCursorAdapter scAdapter;

	Receiver receiver;

	final String SAVED_TEXT = "saved_text";
	final String SAVED_NAME = "saved_name";
	final String CHECKED_GRAPH = "isCheckedGraph";
	final String CHECKED_NOTIF = "isCheckedNotif";

	long idCurrentName;
	EditText editName, editMail, addName, editNotif;

	String[] currentProfile = new String[] { "", "" };

	TimePicker timePicker;

	Button btnAddNotif;

	long id_name;

	static class time {
		public static int hour = 0;
		public static int minute = 0;
	}

	String[] savedValues = new String[4];

	CheckBox checkBoxGraph, checkBoxNotif;
	long rotation = 0;
	int notification = 1;
	int stateActivity;

	static boolean active = false;

	final String LOG_TAG = "Pressure";
	final int DIALOG_EDIT = 1, DIALOG_ADD = 2, DIALOG_SETTINGS = 3;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// открываем подключение к БД
		db = new MyDB(this);
		db.open();

		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		startService(new Intent(this, Receiver.class));

		savedValues = LoadPreferences();
		rotation = Long.valueOf(savedValues[1]);
		stateActivity = Integer.valueOf(savedValues[3]);
		notification = Integer.valueOf(savedValues[2]);

		if (stateActivity == 0) {
			setContentView(R.layout.activity_main);
		} else {
			setContentView(R.layout.activity_main);

			Intent intent = new Intent(MainActivity.this, MyStatistic.class);
			startActivity(intent);
		}

		// тут жесть

		int count_element_notif = db.getCountElementsSettings();

		String[] data_notif_fields = new String[count_element_notif];
		for (int i = 0; i < count_element_notif; ++i) {
			data_notif_fields[i] = "";
		}

		String[] data_notif = new String[] { "", "", "" };

		LinkedList<String[]> list = new LinkedList<String[]>();

		Cursor cursor = db.getAllDataNotif();
		for (int i = 0; i < count_element_notif; ++i) {

			if (cursor != null) {
				cursor.moveToNext();
				data_notif_fields[i] = data_notif_fields[i]
						+ cursor.getString(0);
			}

			data_notif = db.getCurrentNotif(Long.valueOf(data_notif_fields[i]));
			list.add(data_notif);

			if ((notification == 0))
				setRepeatingAlarm(am, Integer.valueOf(data_notif_fields[i]),
						list.get(i)[0], Integer.valueOf(list.get(i)[1]),
						Integer.valueOf(list.get(i)[2]), notification);
		}

		// жесть кончилась

		// формируем столбцы сопоставления
		String[] from = new String[] { MyDB.COLUMN_NAME };
		int[] to = new int[] { R.id.tvName };

		ImageButton addProfile = (ImageButton) findViewById(R.id.addProfile);

		addProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				idCurrentName = 0;
				currentProfile[0] = "";
				currentProfile[1] = "";
				showDialog(DIALOG_ADD);
			}
		});

		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from,
				to, 0);
		final ListView lvData = (ListView) findViewById(R.id.lvData);
		// добавляем контекстное меню к списку
		registerForContextMenu(lvData);

		// создаем лоадер для чтения данных
		getSupportLoaderManager().initLoader(0, null, this);

		if (db.emptyDataBase() == false) {
			db.addRec("Guest");
			lvData.setAdapter(scAdapter);
			getSupportLoaderManager().getLoader(0).forceLoad();
		} else {
			lvData.setAdapter(scAdapter);
		}

		lvData.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.this, MyStatistic.class);
				Cursor cur = (Cursor) lvData.getAdapter().getItem(position);
				id_name = cur.getLong(cur.getColumnIndex("_id"));
				SavePreferences("idName", String.valueOf(id_name));
				stateActivity = 1;
				SavePreferences("state", String.valueOf(stateActivity));
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setRepeatingAlarm(AlarmManager am, int id, String message,
			int hour, int minute, int notif) {
		Calendar cal_alarm;
		Calendar now = Calendar.getInstance();
		cal_alarm = Calendar.getInstance();
		cal_alarm.setTimeInMillis(System.currentTimeMillis());
		cal_alarm.set(Calendar.HOUR_OF_DAY, hour);
		cal_alarm.set(Calendar.MINUTE, minute);

		long alarm = 0;

		if (cal_alarm.getTimeInMillis() <= now.getTimeInMillis())
			alarm = cal_alarm.getTimeInMillis()
					+ (AlarmManager.INTERVAL_DAY + 1);
		else
			alarm = cal_alarm.getTimeInMillis();

		Intent intent = new Intent(this, Receiver.class);
		intent.putExtra("message", message);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, alarm,
				AlarmManager.INTERVAL_DAY, pendingIntent);
		if (notif == 1)
			am.cancel(pendingIntent);
	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private String[] LoadPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String[] data = new String[4];
		data[0] = sharedPreferences.getString("idName", "");
		data[1] = sharedPreferences.getString("rotation", "0");
		data[2] = sharedPreferences.getString("notification", "1");
		data[3] = sharedPreferences.getString("state", "0");
		return data;
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_EDIT) {
			editName = (EditText) dialog.getWindow()
					.findViewById(R.id.editName);
			editMail = (EditText) dialog.getWindow()
					.findViewById(R.id.editMail);
			editName.setText(currentProfile[0]);
			editMail.setText(currentProfile[1]);
		} else if (id == DIALOG_ADD) {
			addName = (EditText) dialog.getWindow().findViewById(
					R.id.addNewName);
			addName.setText("");
		}
	}

	DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE:
				if (idCurrentName != 0) {
					if ((!(isValidEmail(editMail.getText().toString())))) {
						break;
					} else {
						db.editRec(editName.getText().toString(), editMail
								.getText().toString(), String
								.valueOf(idCurrentName));
						getSupportLoaderManager().getLoader(0).forceLoad();
						saveData();
						break;
					}
				} else {
					if ((0 == addName.getText().toString().length())) {
						inCorrectData();
						break;
					} else {
						db.addRec(addName.getText().toString());
						getSupportLoaderManager().getLoader(0).forceLoad();
						addData();
						break;
					}
				}
				// нейтральная кнопка
			case Dialog.BUTTON_NEUTRAL:
				break;
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		if (id == DIALOG_EDIT) {

			LinearLayout view = (LinearLayout) getLayoutInflater().inflate(
					R.layout.dialog, null);
			// устанавливаем ее, как содержимое тела диалога
			adb.setView(view);

			// кнопка положительного ответа
			adb.setPositiveButton(R.string.yes, myClickListener);
			// кнопка нейтрального ответа
			adb.setNeutralButton(R.string.cancel, myClickListener);

			Dialog dialog = adb.create();
			return dialog;
		} else if (id == DIALOG_ADD) {
			LinearLayout view = (LinearLayout) getLayoutInflater().inflate(
					R.layout.dialog_add_name, null);
			// устанавливаем ее, как содержимое тела диалога
			adb.setView(view);

			// кнопка положительного ответа
			adb.setPositiveButton(R.string.yes, myClickListener);
			// кнопка нейтрального ответа
			adb.setNeutralButton(R.string.cancel, myClickListener);

			Dialog dialog = adb.create();
			return dialog;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			Intent intent = new Intent(MainActivity.this, Settings.class);
			startActivity(intent);
			break;
		}
		return super.onKeyDown(keycode, e);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_ID) {
			// извлекаем id записи и удаляем соответствующую запись в БД
			db.delRec(acmi.id);
			// получаем новый курсор с данными
			getSupportLoaderManager().getLoader(0).forceLoad();
			deleteData();
			return true;
		} else if (item.getItemId() == CM_EDIT_ID) {
			currentProfile = db.getCurrentName(acmi.id);
			idCurrentName = acmi.id;
			showDialog(DIALOG_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	boolean isValidEmail(String target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	void saveData() {
		Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
	}

	void deleteData() {
		Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
	}

	void addData() {
		Toast.makeText(this, R.string.add, Toast.LENGTH_SHORT).show();
	}

	void inCorrectData() {
		Toast.makeText(this, R.string.correct_name, Toast.LENGTH_SHORT).show();
	}

	protected void onDestroy() {
		// db.close();
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new MyCursorLoader(this, db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	static class MyCursorLoader extends CursorLoader {

		MyDB db;

		public MyCursorLoader(Context context, MyDB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor = db.getAllData();
			return cursor;
		}
	}
}