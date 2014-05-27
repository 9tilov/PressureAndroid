package com.mobsoftmaster.bloodpressurediary;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class Settings extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	CheckBox checkBoxGraph, checkBoxNotif;

	TimePicker timePicker, timeEditPicker;

	int idCurrentNotif;

	EditText editNotif;

	private static final int CM_EDIT_NOTIF = 0, CM_DELETE_NOTIF = 1,
			CM_DELETE_ALL = 2;

	final int DIALOG_EDIT = 1;

	final String LOG_TAG = "Pressure";

	EditText editCurrentNotif;

	String[] currentNotif = new String[] { "", "", "" };

	MyDB db;

	SharedPreference sharedPref;

	boolean rotation, notification;

	ListView listNotif;

	Button btnAddNotif;

	int count_element_notif;

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
		sharedPref = new SharedPreference(this);

		checkBoxGraph = (CheckBox) findViewById(R.id.checkBoxRotation);
		checkBoxNotif = (CheckBox) findViewById(R.id.checkBoxTimePicker);

		btnAddNotif = (Button) findViewById(R.id.btnAddNotif);
		ImageButton infoButton = (ImageButton) findViewById(R.id.imageButtonInfo);

		editNotif = (EditText) findViewById(R.id.editNotif);

		listNotif = (ListView) findViewById(R.id.listNotif);

		Calendar cal_alarm = Calendar.getInstance();

		timePicker = (TimePicker) findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(cal_alarm.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(cal_alarm.get(Calendar.MINUTE));

		rotation = sharedPref.LoadRotation();
		notification = sharedPref.LoadNotification();

		if (rotation) {
			checkBoxGraph.setChecked(true);
		} else {
			checkBoxGraph.setChecked(false);
		}

		if (notification) {
			checkBoxNotif.setChecked(true);

			timePicker.setEnabled(true);
			btnAddNotif.setEnabled(true);
			editNotif.setEnabled(true);
			listNotif.setEnabled(true);
		} else {
			checkBoxNotif.setChecked(false);

			timePicker.setEnabled(false);
			btnAddNotif.setEnabled(false);
			editNotif.setEnabled(false);
			listNotif.setEnabled(false);
		}

		checkBoxNotif
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton checkView,
							boolean isChecked) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						if (checkView.isChecked()) {
							imm.showSoftInput(editNotif,
									InputMethodManager.SHOW_IMPLICIT);
							timePicker.setEnabled(true);
							btnAddNotif.setEnabled(true);
							editNotif.setEnabled(true);
							listNotif.setEnabled(true);
						} else {
							imm.hideSoftInputFromWindow(
									editNotif.getWindowToken(), 0);
							timePicker.setEnabled(false);
							btnAddNotif.setEnabled(false);
							editNotif.setEnabled(false);
							listNotif.setEnabled(false);
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

		count_element_notif = db.getCountElementsSettings();

		Button btnSaveSettings = (Button) findViewById(R.id.btnSaveSettings);

		btnAddNotif.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				timePicker.clearFocus();
				time.hour = timePicker.getCurrentHour();
				time.minute = timePicker.getCurrentMinute();
				String hour;
				String minute;
				if (time.hour < 10)
					hour = "0" + String.valueOf(time.hour);
				else
					hour = String.valueOf(time.hour);
				if (time.minute < 10)
					minute = "0" + String.valueOf(time.minute);
				else
					minute = String.valueOf(time.minute);

				if (0 != editNotif.getText().toString().length()) {
					db.addNotif(editNotif.getText().toString(), hour, minute);
					editNotif.setText("");
					getSupportLoaderManager().getLoader(0).forceLoad();
					count_element_notif = db.getCountElementsSettings();
					scrollMyListViewToBottom();
				} else {
					inCorrectData();
				}
			}
		});

		btnSaveSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rotation = checkCheckBox(checkBoxGraph);
				notification = checkCheckBox(checkBoxNotif);
				if ((db.getCountElementsSettings() == 0)
						&& (checkCheckBox(checkBoxNotif)))
					inCorrectData();
				else {
					Intent intent = new Intent(Settings.this,
							MainActivity.class);
					sharedPref.SavePreferences("rotation", rotation);
					sharedPref.SavePreferences("notification", notification);
					sharedPref.SavePreferences("state", true);
					startActivity(intent);
				}
			}
		});
		scrollMyListViewToBottom();

		infoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.this, ProgrammInfo.class);
				startActivity(intent);
			}
		});
	}

	private void scrollMyListViewToBottom() {
		listNotif.post(new Runnable() {
			@Override
			public void run() {
				listNotif.setAdapter(scAdapter);
				// Select the last row so it will scroll into view...
				listNotif.setSelection(count_element_notif);
			}
		});
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_EDIT_NOTIF, 0, R.string.edit_notif);
		menu.add(0, CM_DELETE_NOTIF, 0, R.string.delete_notif);
		menu.add(0, CM_DELETE_ALL, 0, R.string.delete_notif_all);
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
				String hour;
				String minute;
				if (timeEditPicker.getCurrentHour() < 10)
					hour = "0"
							+ String.valueOf(timeEditPicker.getCurrentHour());
				else
					hour = String.valueOf(timeEditPicker.getCurrentHour());
				if (timeEditPicker.getCurrentMinute() < 10)
					minute = "0"
							+ String.valueOf(timeEditPicker.getCurrentMinute());
				else
					minute = String.valueOf(timeEditPicker.getCurrentMinute());

				if (0 != editCurrentNotif.getText().toString().length()) {
					db.editNotif(String.valueOf(editCurrentNotif.getText()),
							hour, minute, String.valueOf(idCurrentNotif));
					getSupportLoaderManager().getLoader(0).forceLoad();
					scrollMyListViewToBottom();
				}
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
			scrollMyListViewToBottom();
			return true;
		} else if (item.getItemId() == CM_EDIT_NOTIF) {
			idCurrentNotif = (int) acmi.id;
			currentNotif = db.getCurrentNotif(acmi.id);
			showDialog(DIALOG_EDIT);
			scrollMyListViewToBottom();
			return true;
		} else if (item.getItemId() == CM_DELETE_ALL) {
			showChoice();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void showChoice() {
		final Dialog dialog = new Dialog(Settings.this);
		dialog.setContentView(R.layout.dialog_choice);
		dialog.setTitle("Are you sure?");

		Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
		Button btnNo = (Button) dialog.findViewById(R.id.btnNo);

		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				db.delRecAllNotif();
				getSupportLoaderManager().getLoader(0).forceLoad();
				deleteData();
				dialog.dismiss();
			}
		});

		btnNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean checkCheckBox(View v) {
		CheckBox checkBoxGraph = (CheckBox) v;
		if (!checkBoxGraph.isChecked())
			return false;
		return true;
	}

	protected void onDestroy() {
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

	void inCorrectData() {
		Toast.makeText(this, R.string.correct_notif, Toast.LENGTH_SHORT).show();
	}

	void deleteData() {
		Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
	}
}
