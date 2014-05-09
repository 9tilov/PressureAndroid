package com.example.pressure;

import java.util.Calendar;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;

public class Settings extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	CheckBox checkBoxGraph, checkBoxNotif;
	Calendar cal_alarm;
	TimePicker timePicker, timeEditPicker;

	int idCurrentNotif;

	EditText editNotif;

	static SharedPreferences sPref;

	final String CHECKED_GRAPH = "isCheckedGraph";
	final String CHECKED_NOTIF = "isCheckedNotif";

	private static final int CM_EDIT_NOTIF = 0, CM_DELETE_NOTIF = 1;
	final int DIALOG_EDIT = 1;

	final String LOG_TAG = "Pressure";

	EditText editCurrentNotif;

	String[] currentNotif = new String[] { "", "", "" };

	MyDB db;

	int rotation = 0;
	int notification = 1;
	int stateActivity;

	Button btnAddNotif;

	String[] savedValues = new String[3];

	static class time {
		public static int hour = 0;
		public static int minute = 0;
	}

	SimpleCursorAdapter scAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		db = new MyDB(this);
		db.open();

		savedValues = LoadPreferences();

		checkBoxGraph = (CheckBox) findViewById(R.id.checkBoxRotation);
		checkBoxNotif = (CheckBox) findViewById(R.id.checkBoxTimePicker);

		btnAddNotif = (Button) findViewById(R.id.btnAddNotif);

		editNotif = (EditText) findViewById(R.id.editNotif);

		final ListView listNotif = (ListView) findViewById(R.id.listNotif);

		cal_alarm = Calendar.getInstance();

		timePicker = (TimePicker) findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(cal_alarm.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(cal_alarm.get(Calendar.MINUTE));

		rotation = Integer.valueOf(savedValues[0]);
		notification = Integer.valueOf(savedValues[1]);

		if (rotation == 0) {
			checkBoxGraph.setChecked(true);
		} else {
			checkBoxGraph.setChecked(false);
		}

		if (notification == 0) {
			checkBoxNotif.setChecked(true);
			timePicker.setVisibility(View.VISIBLE);
			btnAddNotif.setVisibility(View.VISIBLE);
			editNotif.setVisibility(View.VISIBLE);

		} else {
			checkBoxNotif.setChecked(false);
			timePicker.setVisibility(View.INVISIBLE);
			btnAddNotif.setVisibility(View.INVISIBLE);
			editNotif.setVisibility(View.INVISIBLE);
			listNotif.setVisibility(View.INVISIBLE);
		}

		checkBoxNotif
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton checkView,
							boolean isChecked) {
						if (checkView.isChecked()) {
							timePicker.setVisibility(View.VISIBLE);
							btnAddNotif.setVisibility(View.VISIBLE);
							editNotif.setVisibility(View.VISIBLE);
							listNotif.setVisibility(View.VISIBLE);
						} else {
							timePicker.setVisibility(View.INVISIBLE);
							btnAddNotif.setVisibility(View.INVISIBLE);
							editNotif.setVisibility(View.INVISIBLE);
							listNotif.setVisibility(View.INVISIBLE);
						}
					}
				});

		// формируем столбцы сопоставления
		String[] from = new String[] { MyDB.COLUMN_NOTIF_HOUR,
				MyDB.COLUMN_NOTIF_MINUTE, MyDB.COLUMN_NOTIF_MESSAGE };
		int[] to = new int[] { R.id.hourSettings, R.id.minuteSettings,
				R.id.notifSettings };

		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.layout.list_settings, null,
				from, to, 0);

		registerForContextMenu(listNotif);

		getSupportLoaderManager().initLoader(0, null, this);

		listNotif.setAdapter(scAdapter);

		Button btnSaveSettings = (Button) findViewById(R.id.btnSaveSettings);

		btnAddNotif.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				time.hour = timePicker.getCurrentHour();
				time.minute = timePicker.getCurrentMinute();
				db.addNotif(String.valueOf(editNotif.getText()),
						String.valueOf(time.hour), String.valueOf(time.minute));
				editNotif.setText("");
				getSupportLoaderManager().getLoader(0).forceLoad();
			}
		});

		btnSaveSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rotation = Integer.valueOf(checkCheckBox(checkBoxGraph));
				notification = checkCheckBox(checkBoxNotif);
				Intent intent = new Intent(Settings.this, MainActivity.class);
				SavePreferences("rotation", String.valueOf(rotation));
				SavePreferences("notification", String.valueOf(notification));
				stateActivity = 0;
				SavePreferences("state", String.valueOf(stateActivity));
				startActivityForResult(intent, 1);
			}
		});

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
		String[] data = new String[2];
		data[0] = sharedPreferences.getString("rotation", "0");
		data[1] = sharedPreferences.getString("notification", "1");
		return data;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_EDIT_NOTIF, 0, R.string.edit_notif);
		menu.add(0, CM_DELETE_NOTIF, 0, R.string.delete_notif);
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_EDIT) {
			editCurrentNotif = (EditText) dialog.getWindow().findViewById(
					R.id.editCurrentNotif);
			editCurrentNotif.setText(currentNotif[0]);

			timeEditPicker = (TimePicker) dialog.getWindow().findViewById(
					R.id.timeEditPicker);
			timeEditPicker.setIs24HourView(true);

			timeEditPicker.setCurrentHour(Integer.valueOf(currentNotif[1]));
			timeEditPicker.setCurrentMinute(Integer.valueOf(currentNotif[2]));
		}
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		if (id == DIALOG_EDIT) {
			LinearLayout view = (LinearLayout) getLayoutInflater().inflate(
					R.layout.dialog_settings, null);
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

	DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			// EditTextValidator firstnameValidator = new EditTextValidator();
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE:
				db.editNotif(String.valueOf(editCurrentNotif.getText()),
						String.valueOf(timeEditPicker.getCurrentHour()),
						String.valueOf(timeEditPicker.getCurrentMinute()),
						String.valueOf(idCurrentNotif));
				getSupportLoaderManager().getLoader(0).forceLoad();
				break;
			// нейтральная кнопка
			case Dialog.BUTTON_NEUTRAL:
				break;
			}
		}
	};

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_NOTIF) {
			// извлекаем id записи и удаляем соответствующую запись в БД
			db.delRecNotif(acmi.id);
			// получаем новый курсор с данными
			getSupportLoaderManager().getLoader(0).forceLoad();
			return true;
		} else if (item.getItemId() == CM_EDIT_NOTIF) {
			idCurrentNotif = (int) acmi.id;
			currentNotif = db.getCurrentNotif(acmi.id);
			showDialog(DIALOG_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public int checkCheckBox(View v) {
		CheckBox checkBoxGraph = (CheckBox) v;
		if (!checkBoxGraph.isChecked())
			return 1;
		return 0;
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
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	static class MyCursorLoader extends CursorLoader {

		MyDB db;

		public MyCursorLoader(Context context, MyDB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor = db.getAllDataNotif();
			return cursor;
		}
	}
}
