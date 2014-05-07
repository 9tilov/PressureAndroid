package com.example.pressure;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.CompoundButton;
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

	final String SAVED_TEXT = "saved_text";
	final String SAVED_NAME = "saved_name";
	final String CHECKED_GRAPH = "isCheckedGraph";
	final String CHECKED_NOTIF = "isCheckedNotif";

	long idCurrentName;
	EditText editName, editMail, addName, editNotif;

	String[] currentProfile = new String[] { "", "" };

	TimePicker timePicker;

	Calendar cal_alarm;

	Button btnAddNotif;

	long id_name;

	enum window {
		profile, data
	}

	static class time {
		public static int hour = 0;
		public static int minute = 0;
	}

	long[] mas = new long[3];
	CheckBox checkBoxGraph, checkBoxNotif;
	long rotation = 0;
	long notification = 1;

	static boolean active = false;

	AlarmManager am;

	final String LOG_TAG = "Pressure";
	final int DIALOG_EDIT = 1, DIALOG_ADD = 2, DIALOG_SETTINGS = 3;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		startService(new Intent(this, Receiver.class));

		mas = loadState();
		rotation = mas[2];

		if (mas[0] == 0) {
			setContentView(R.layout.activity_main);
		} else {
			setContentView(R.layout.activity_main);
			Intent intent = new Intent(MainActivity.this, MyStatistic.class);
			intent.putExtra("id_profile_key", String.valueOf(mas[1]));
			intent.putExtra("rotation", String.valueOf(rotation));
			startActivityForResult(intent, 1);
		}
		setRepeatingAlarm();

		// открываем подключение к БД
		db = new MyDB(this);
		db.open();

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

		Intent intent = new Intent(MainActivity.this, MyStatistic.class);
		intent.putExtra("rotation", String.valueOf(rotation));

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
		} else
			lvData.setAdapter(scAdapter);

		lvData.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.this, MyStatistic.class);
				Cursor cur = (Cursor) lvData.getAdapter().getItem(position);
				id_name = cur.getLong(cur.getColumnIndex("_id"));
				intent.putExtra("id_profile_key", String.valueOf(id_name));
				intent.putExtra("rotation", String.valueOf(rotation));
				startActivityForResult(intent, 1);
				saveState(window.data, id_name, rotation, notification);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setRepeatingAlarm() {
		cal_alarm = Calendar.getInstance();
		cal_alarm.setTimeInMillis(System.currentTimeMillis());
		cal_alarm.set(Calendar.HOUR_OF_DAY, time.hour);
		cal_alarm.set(Calendar.MINUTE, time.minute);
		Intent intent = new Intent(this, Receiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pendingIntent);
	}

//	public long checkCheckBox(View v) {
//		CheckBox checkBoxGraph = (CheckBox) v;
//		if (!checkBoxGraph.isChecked())
//			return 1;
//		return 0;
//	}

	void saveState(window cnt, long id_name, long isCheckedGraph,
			long isCheckedNotif) {
		sPref = getPreferences(MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putLong(SAVED_TEXT, cnt.ordinal());
		ed.putLong(SAVED_NAME, id_name);
		ed.putLong(CHECKED_GRAPH, isCheckedGraph);
		ed.putLong(CHECKED_NOTIF, isCheckedNotif);
		ed.commit();
		Log.d(LOG_TAG, "cnt = " + cnt);
	}

	long[] loadState() {
		sPref = getPreferences(MODE_PRIVATE);
		long state = sPref.getLong(SAVED_TEXT, 0);
		long name = sPref.getLong(SAVED_NAME, 0);
		long checkStateGraph = sPref.getLong(CHECKED_GRAPH, 0);
		long checkStateNotif = sPref.getLong(CHECKED_NOTIF, 0);
		long[] massive = new long[4];
		massive[0] = state;
		massive[1] = name;
		massive[2] = checkStateGraph;
		massive[3] = checkStateNotif;
		Log.d(LOG_TAG, "string = " + massive[0]);
		Log.d(LOG_TAG, "string_name = " + massive[1]);
		Log.d(LOG_TAG, "checkStateGraph = " + massive[2]);
		return massive;
	}

	// обработчик закрытия окна статистики
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mas = loadState();
		rotation = mas[2];
		notification = mas[3];
		saveState(window.profile, id_name, rotation, notification);
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
		} else if (id == DIALOG_SETTINGS) {

			// формируем столбцы сопоставления
//			String[] from = new String[] { MyDB.COLUMN_TIME,
//					MyDB.COLUMN_NOTIF_MESSAGE };
//			int[] to = new int[] { R.id.timeSettings, R.id.notifSettings };
			// создааем адаптер и настраиваем список
//			SimpleCursorAdapter dialogAdapter = new SimpleCursorAdapter(this,
//					R.layout.list_settings, null, from, to, 0);
			final ListView listSettings = (ListView) dialog.getWindow()
					.findViewById(R.id.listNotif);

			// добавляем контекстное меню к списку
			registerForContextMenu(listSettings);

			// создаем лоадер для чтения данных
			getSupportLoaderManager().initLoader(0, null, this);
			
//			listSettings.setAdapter(dialogAdapter);
			getSupportLoaderManager().getLoader(0).forceLoad();

			editNotif = (EditText) dialog.getWindow().findViewById(
					R.id.editNotif);

			btnAddNotif = (Button) dialog.getWindow().findViewById(
					R.id.btnAddNotif);

			mas = loadState();
			checkBoxGraph = (CheckBox) dialog.getWindow().findViewById(
					R.id.checkBoxRotation);
			checkBoxNotif = (CheckBox) dialog.getWindow().findViewById(
					R.id.checkBoxTimePicker);

			cal_alarm = Calendar.getInstance();
			timePicker = (TimePicker) dialog.getWindow().findViewById(
					R.id.timePicker);
			timePicker.setIs24HourView(true);
			timePicker.setCurrentHour(cal_alarm.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(cal_alarm.get(Calendar.MINUTE));

			if (mas[3] == 0) {
				checkBoxNotif.setChecked(true);
				timePicker.setVisibility(View.VISIBLE);
			} else {
				checkBoxNotif.setChecked(false);
				timePicker.setVisibility(View.INVISIBLE);
			}

			checkBoxNotif
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton checkView,
								boolean isChecked) {
							if (checkView.isChecked())
								timePicker.setVisibility(View.VISIBLE);
							else
								timePicker.setVisibility(View.INVISIBLE);
						}
					});
			if (mas[2] == 1)
				checkBoxGraph.setChecked(false);
			else
				checkBoxGraph.setChecked(true);
//			rotation = checkCheckBox(checkBoxGraph);
		}
	}

	DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			// EditTextValidator firstnameValidator = new EditTextValidator();
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE:
				if (idCurrentName != 0) {
					if ((0 == editName.getText().toString().length())
							|| (0 == editMail.getText().toString().length())
							|| (!(isValidEmail(editMail.getText().toString())))) {
//						rotation = checkCheckBox(checkBoxGraph);
//						notification = checkCheckBox(checkBoxNotif);
						saveState(window.profile, id_name, rotation,
								notification);
						break;
					} else {
						db.editRec(editName.getText().toString(), editMail
								.getText().toString(), String
								.valueOf(idCurrentName));
						getSupportLoaderManager().getLoader(0).forceLoad();
//						rotation = checkCheckBox(checkBoxGraph);
//						notification = checkCheckBox(checkBoxNotif);
						saveState(window.profile, id_name, rotation,
								notification);
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

	DialogInterface.OnClickListener myClickListenerSettings = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE:
//				rotation = checkCheckBox(checkBoxGraph);
//				notification = checkCheckBox(checkBoxNotif);
				Log.d(LOG_TAG, "notification = " + notification);
				saveState(window.profile, id_name, rotation, notification);
				time.hour = timePicker.getCurrentHour();
				time.minute = timePicker.getCurrentMinute();
//				db.addRec(editNotif.getText().toString());
//				db.addNotif(
//						String.valueOf(editNotif.getText()),
//						String.valueOf(time.hour) + ":"
//								+ String.valueOf(time.minute));
				getSupportLoaderManager().getLoader(0).forceLoad();
				setRepeatingAlarm();
				saveData();
				break;
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
		} else if (id == DIALOG_SETTINGS) {
			LinearLayout view = (LinearLayout) getLayoutInflater().inflate(
					R.layout.settings, null);
			// устанавливаем ее, как содержимое тела диалога
			adb.setView(view);

			// кнопка положительного ответа
			adb.setPositiveButton(R.string.yes, myClickListenerSettings);
			// кнопка нейтрального ответа
			adb.setNeutralButton(R.string.cancel, myClickListenerSettings);

			
			
			
//			btnAddNotif.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					db.addNotif(
//							String.valueOf(editNotif.getText()),
//							String.valueOf(time.hour) + ":"
//									+ String.valueOf(time.minute));
//					getSupportLoaderManager().getLoader(0).forceLoad();
//				}
//			});
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
		Toast.makeText(this, R.string.correct, Toast.LENGTH_SHORT).show();
	}

	protected void onDestroy() {
		db.close();
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