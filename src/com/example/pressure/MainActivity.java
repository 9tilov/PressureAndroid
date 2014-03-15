package com.example.pressure;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private static final int CM_EDIT_ID = 0;
	private static final int CM_DELETE_ID = 1;
	private static final int CM_ADD_ID = 2;

	static SharedPreferences sPref;

	MyDB db;
	SimpleCursorAdapter scAdapter;

	final String SAVED_TEXT = "saved_text";
	final String SAVED_NAME = "saved_name";

	long idCurrentName;
	EditText editName, editMail;

	String[] currentProfile = new String[] { "", "" };

	long id_name;

	enum window {
		profile, data
	}

	final String LOG_TAG = "Pressure";
	final int DIALOG = 1;

	final String LOG_TAG_NAME = "myLogsName";

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		// открываем подключение к БД
		db = new MyDB(this);
		db.open();

		// формируем столбцы сопоставления
		String[] from = new String[] { MyDB.COLUMN_NAME };
		int[] to = new int[] { R.id.tvName };

		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from,
				to, 0);
		final ListView lvData = (ListView) findViewById(R.id.lvData);

		// добавляем контекстное меню к списку
		registerForContextMenu(lvData);

		// создаем лоадер для чтения данных
		getSupportLoaderManager().initLoader(0, null, this);

		if (db.emptyDataBase() == false) {
			db.addRec("Profile1", "mail@mail.com");
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
				startActivityForResult(intent, 1);
				saveState(window.data, id_name);
			}
		});
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
							|| (!(isValidEmail(editMail.getText().toString()))))
						inCorrectData();
					else {
						db.editRec(editName.getText().toString(), editMail
								.getText().toString(), String
								.valueOf(idCurrentName));
						getSupportLoaderManager().getLoader(0).forceLoad();
						saveData();
					}
				} else {
					if ((0 == editName.getText().toString().length())
							|| (0 == editMail.getText().toString().length())
							|| (!(isValidEmail(editMail.getText().toString()))))
						inCorrectData();
					else {
						db.addRec(editName.getText().toString(), editMail
								.getText().toString());
						// Log.d(LOG_TAG, "string_mail = " + name_length);
						getSupportLoaderManager().getLoader(0).forceLoad();
						addData();
					}
				}
				break;
			// нейтральная кнопка
			case Dialog.BUTTON_NEUTRAL:
				break;
			}
		}
	};

	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if (id == DIALOG) {
			editName = (EditText) dialog.getWindow()
					.findViewById(R.id.editName);
			editMail = (EditText) dialog.getWindow()
					.findViewById(R.id.editMail);
			editName.setText(currentProfile[0]);
			editMail.setText(currentProfile[1]);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		if (id == DIALOG) {

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
		}
		return super.onCreateDialog(id);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_ADD_ID, 0, R.string.add_record);
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
			showDialog(DIALOG);
			return true;
		} else if (item.getItemId() == CM_ADD_ID) {
			idCurrentName = 0;
			currentProfile[0] = "";
			currentProfile[1] = "";
			showDialog(DIALOG);
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
		super.onDestroy();
		// закрываем подключение при выходе
		db.close();
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