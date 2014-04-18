package com.example.pressure;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private static final int CM_EDIT_ID = 0, CM_DELETE_ID = 1;

	static SharedPreferences sPref;

	MyDB db;
	SimpleCursorAdapter scAdapter;

	final String SAVED_TEXT = "saved_text";
	final String SAVED_NAME = "saved_name";

	long idCurrentName;
	EditText editName, editMail, addName;

	String[] currentProfile = new String[] { "", "" };

	long id_name;

	enum window {
		profile, data
	}

	static boolean active = false;

	AlarmManager am;

	final String LOG_TAG = "Pressure";
	final int DIALOG_EDIT = 1, DIALOG_ADD = 2;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		startService(new Intent(this, Receiver.class));

		long[] mas = new long[2];
		mas = loadState();

		if (mas[0] == 0)
			setContentView(R.layout.activity_main);
		else {
			setContentView(R.layout.activity_main);
			Intent intent = new Intent(MainActivity.this, MyStatistic.class);
			intent.putExtra("id_profile_key", String.valueOf(mas[1]));
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

		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from,
				to, 0);
		final ListView lvData = (ListView) findViewById(R.id.lvData);

		// добавляем контекстное меню к списку
		registerForContextMenu(lvData);

		// создаем лоадер для чтения данных
		getSupportLoaderManager().initLoader(0, null, this);

		lvData.setAdapter(scAdapter);

		lvData.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.this, MyStatistic.class);
				Cursor cur = (Cursor) lvData.getAdapter().getItem(position);
				id_name = cur.getLong(cur.getColumnIndex("_id"));
				intent.putExtra("id_profile_key", String.valueOf(id_name));
				startActivityForResult(intent, 1);
				saveState(window.data, id_name);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setRepeatingAlarm() {
		Intent intent = new Intent(this, Receiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				(20 * 1000), pendingIntent);
	}

	void saveState(window cnt, long id_name) {
		sPref = getPreferences(MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putLong(SAVED_TEXT, cnt.ordinal());
		ed.putLong(SAVED_NAME, id_name);
		ed.commit();
		Log.d(LOG_TAG, "cnt = " + cnt);
	}

	long[] loadState() {
		sPref = getPreferences(MODE_PRIVATE);
		long state = sPref.getLong(SAVED_TEXT, 0);
		long name = sPref.getLong(SAVED_NAME, 0);
		long[] massive = new long[2];
		massive[0] = state;
		massive[1] = name;
		Log.d(LOG_TAG, "string = " + massive[0]);
		Log.d(LOG_TAG, "string_name = " + massive[1]);
		return massive;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		saveState(window.profile, id_name);
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
						inCorrectData();
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
		}
	}

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