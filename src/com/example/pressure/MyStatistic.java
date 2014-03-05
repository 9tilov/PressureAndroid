package com.example.pressure;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class MyStatistic extends FragmentActivity implements OnClickListener,
		LoaderCallbacks<Cursor> {

	private static final int CM_DELETE_ID = 0;
	long idCurrentName;
	String currentName;
	private TextView name;
	MyDB db;
	Button btnAdd;
	EditText etPulse, etSysPressure, etDiasPressure;
	static String profile_id;
	SimpleCursorAdapter scAdapter;

	Cursor cursor;

	final int DIALOG_STAT = 1;
	final String LOG_TAG = "myLogs";

	// @Override
	// protected void onStart() {
	// super.onStart();
	//
	// /** Initializes the Loader */
	// getSupportLoaderManager().initLoader(0, null, this);
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic);

		btnAdd = (Button) findViewById(R.id.btnAddStat);
		btnAdd.setOnClickListener(this);

		db = new MyDB(this);
		db.open();

		// формируем столбцы сопоставления
		String[] from = new String[] { MyDB.COLUMN_PULSE,
				MyDB.COLUMN_SYS_PRESSURE, MyDB.COLUMN_DIAS_PRESSURE };
		int[] to = new int[] { R.id.tvTextPulse, R.id.tvTextSys,
				R.id.tvTextDias };

		name = (TextView) findViewById(R.id.profile_name);
		profile_id = getIntent().getStringExtra("lvData");

		String profile_name = db.getCurrentName(Long.parseLong(profile_id));
		name.setText(profile_name);

		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.layout.list, null, from,
				to, 0);
		ListView listStat = (ListView) findViewById(R.id.listStat);
		registerForContextMenu(listStat);

		listStat.setAdapter(scAdapter);

		// создаем лоадер для чтения данных
		getSupportLoaderManager().initLoader(0, null, this);
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if (id == DIALOG_STAT) {
			etPulse = (EditText) dialog.getWindow().findViewById(R.id.etPulse);
			etSysPressure = (EditText) dialog.getWindow().findViewById(
					R.id.etSysPressure);
			etDiasPressure = (EditText) dialog.getWindow().findViewById(
					R.id.etDiasPressure);
		}
	}

	DialogInterface.OnClickListener myClickListenerStat = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE:
				if ((etPulse.getText().toString().length() == 0)
						|| (etSysPressure.getText().toString().length() == 0)
						|| (etDiasPressure.getText().toString().length() == 0)) {
					break;
				} else {
					db.addStat(etPulse.getText().toString(), etSysPressure
							.getText().toString(), etDiasPressure.getText()
							.toString(), profile_id);
					getSupportLoaderManager().getLoader(0).forceLoad();
					etPulse.setText("");
					etSysPressure.setText("");
					etDiasPressure.setText("");
					addData();
				}
				break;
			// нейтральная кнопка
			case Dialog.BUTTON_NEUTRAL:
				break;
			}
		}
	};

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		db.delRecStat(acmi.id);
		// получаем новый курсор с данными
		getSupportLoaderManager().getLoader(0).forceLoad();
		deleteData();
		return super.onContextItemSelected(item);
	}

	// protected void onPrepareDialog(int id, Dialog dialog) {
	// super.onPrepareDialog(id, dialog);
	// if (id == DIALOG_STAT) {
	// //showDialog(DIALOG_STAT);
	// }
	// }

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		if (id == DIALOG_STAT) {

			LinearLayout view = (LinearLayout) getLayoutInflater().inflate(
					R.layout.dialog_stat, null);
			// устанавливаем ее, как содержимое тела диалога
			adb.setView(view);

			// кнопка положительного ответа
			adb.setPositiveButton(R.string.yes, myClickListenerStat);
			// кнопка нейтрального ответа
			adb.setNeutralButton(R.string.cancel, myClickListenerStat);

			Dialog dialog = adb.create();
			return dialog;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(View v) {
		showDialog(DIALOG_STAT);
	}

	protected void onDestroy() {
		db.close();
		super.onDestroy();
		// закрываем подключение при выходе
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
			Cursor cursor = db.getAllDataStat(profile_id);
			return cursor;
		}
	}

	void saveData() {
		Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
	}

	void addData() {
		Toast.makeText(this, R.string.add, Toast.LENGTH_SHORT).show();
	}

	void deleteData() {
		Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
	}
}