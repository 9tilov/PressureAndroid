package com.example.pressure;


import com.example.pressure.MainActivity.MyCursorLoader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.opengl.ETC1;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class MyStatistic extends FragmentActivity implements OnClickListener, LoaderCallbacks<Cursor> {
	private TextView name;
	MyDB db;
	Button btnAdd;
	EditText etPulse, etSysPressure, etDiasPressure;

	SimpleCursorAdapter scAdapter;
	
	Cursor cursor;

	final int DIALOG_STAT = 1;
	final String LOG_TAG = "myLogs";
	
	@Override
	protected void onStart() {
	    super.onStart();

	    /** Initializes the Loader */
	    getSupportLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic);

		btnAdd = (Button) findViewById(R.id.btnAddStat);
		btnAdd.setOnClickListener(this);

		db = new MyDB(this);
		db.open();
		
		cursor = db.getAllDataStat();
	    startManagingCursor(cursor);

		// формируем столбцы сопоставления
		String[] fromPulse = new String[] { MyDB.COLUMN_PULSE };
		int[] to = new int[] { R.id.tvText };

		name = (TextView) findViewById(R.id.profile_name);
		String profile_id = getIntent().getStringExtra("lvData");// принимаем id item'a из списка
		
		String profile_name = db.getCurrentName(Long.parseLong(profile_id));
		name.setText(profile_name);
//		getSupportLoaderManager().initLoader(0, null, this);
		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, fromPulse, to, 0);
		ListView lvStat = (ListView) findViewById(R.id.lvStat);
		
		lvStat.setAdapter(scAdapter);
		
//		getSupportLoaderManager().getLoader(0).forceLoad();
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
					db.addPulse(etPulse.getText().toString());
					db.addSysPressure(etSysPressure.getText().toString());
					db.addDiasPressure(etDiasPressure.getText().toString());
//					lvStat.setAdapter(scAdapter);
					getSupportLoaderManager().getLoader(0).forceLoad();
					addData();
				}
				break;
			// нейтральная кнопка
			case Dialog.BUTTON_NEUTRAL:
				break;
			}
		}
	};

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
			Cursor cursor = db.getAllDataStat();
			return cursor;
		}
	}
	
	void saveData() {
		Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
	}

	void addData() {
		Toast.makeText(this, R.string.add, Toast.LENGTH_SHORT).show();
	}
}