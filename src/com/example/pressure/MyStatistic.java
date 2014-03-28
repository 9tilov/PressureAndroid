package com.example.pressure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

public class MyStatistic extends FragmentActivity implements OnClickListener,
		LoaderCallbacks<Cursor>, NumberPicker.OnValueChangeListener {

	private static final int CM_DELETE_ID = 0, CM_EDIT_ID = 1;
	long idCurrentName = 0;
	String formattedDate, formattedTime, currentPulse, currentSys, currentDias;

	MyDB db;
	Button btnAdd, btnSave, btnLoad, btnEmail, btnGraph;
	EditText etPulse, etSysPressure, etDiasPressure;
	static String profile_id;
	SimpleCursorAdapter scAdapter;

	TextView tvPulse;

	String[] profile_name;

	NumberPicker npPulse, npSysPressure, npDiasPressure;

	ListView listStat, pulse, sysPressure, diasPressure;

	final String DIR_SD = "Pressure";
	final String FILENAME_SD = "pressure_stat";

	final String FILENAME = "Pressure_stat";

	Cursor cursor;
	String[] currentStat = new String[] { "", "", "" };
	final int DIALOG_STAT = 1;
	final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic);

		btnAdd = (Button) findViewById(R.id.btnAddStat);
		btnGraph = (Button) findViewById(R.id.btnGraph);
		TextView name, e_mail;
		btnAdd.setOnClickListener(this);
		btnGraph.setOnClickListener(this);

		tvPulse = (TextView) findViewById(R.id.tvTextPulse);

		db = new MyDB(this);
		db.open();

		// формируем столбцы сопоставления
		String[] from = new String[] { MyDB.COLUMN_PULSE,
				MyDB.COLUMN_SYS_PRESSURE, MyDB.COLUMN_DIAS_PRESSURE,
				MyDB.COLUMN_DATE, MyDB.COLUMN_TIME };
		int[] to = new int[] { R.id.tvTextPulse, R.id.tvTextSys,
				R.id.tvTextDias, R.id.tvTextDate, R.id.tvTextTime };

		name = (TextView) findViewById(R.id.profile_name);
		e_mail = (TextView) findViewById(R.id.profile_e_mail);
		profile_id = getIntent().getStringExtra("id_profile_key");

		profile_name = db.getCurrentName(Long.parseLong(profile_id));
		name.setText(profile_name[0]);
		e_mail.setText(profile_name[1]);

		npPulse = (NumberPicker) findViewById(R.id.npPulse);
		npSysPressure = (NumberPicker) findViewById(R.id.npSysPressure);
		npDiasPressure = (NumberPicker) findViewById(R.id.npDiasPressure);

		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.layout.list, null, from,
				to, 0);
		listStat = (ListView) findViewById(R.id.listStat);
		registerForContextMenu(listStat);

		listStat.setAdapter(scAdapter);

		// // создаем лоадер для чтения данных
		getSupportLoaderManager().initLoader(0, null, this);
		// //Получаем текущее время
		Calendar c = Calendar.getInstance();
		// SimpleDateFormat df = new SimpleDateFormat("ddMM");
		SimpleDateFormat date = new SimpleDateFormat("dd/MM");
		SimpleDateFormat time = new SimpleDateFormat("HH:mm");
		formattedDate = date.format(c.getTime());
		formattedTime = time.format(c.getTime());
	}

	public void show() {

		final Dialog dialog = new Dialog(MyStatistic.this);
		dialog.setContentView(R.layout.dialog_stat);
		dialog.setTitle("Statictics");
		final NumberPicker npPulse = (NumberPicker) dialog
				.findViewById(R.id.npPulse);
		npPulse.setMaxValue(200);
		npPulse.setMinValue(20);
		npPulse.setWrapSelectorWheel(false);
		npPulse.setOnValueChangedListener(this);

		final NumberPicker npSysPressure = (NumberPicker) dialog
				.findViewById(R.id.npSysPressure);
		npSysPressure.setMaxValue(300);
		npSysPressure.setMinValue(60);
		npSysPressure.setWrapSelectorWheel(false);
		npSysPressure.setOnValueChangedListener(this);

		final NumberPicker npDiasPressure = (NumberPicker) dialog
				.findViewById(R.id.npDiasPressure);
		npDiasPressure.setMaxValue(250);
		npDiasPressure.setMinValue(40);
		npDiasPressure.setWrapSelectorWheel(false);
		npDiasPressure.setOnValueChangedListener(this);

		if (idCurrentName != 0) {
			npPulse.setValue(Integer.valueOf(currentStat[0]));
			npSysPressure.setValue(Integer.valueOf(currentStat[1]));
			npDiasPressure.setValue(Integer.valueOf(currentStat[2]));
		} else {
			npPulse.setValue(65);
			npSysPressure.setValue(120);
			npDiasPressure.setValue(80);
		}

		final Button btnSaveStat = (Button) dialog
				.findViewById(R.id.btnSaveState);

		btnSaveStat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (idCurrentName == 0) {
					db.addStat(String.valueOf(npPulse.getValue()),
							String.valueOf(npSysPressure.getValue()),
							String.valueOf(npDiasPressure.getValue()),
							profile_id, formattedDate, formattedTime);
					getSupportLoaderManager().getLoader(0).forceLoad();
					addData();
					dialog.dismiss();
				} else {
					currentStat[0] = String.valueOf(npPulse.getValue());
					currentStat[1] = String.valueOf(npSysPressure.getValue());
					currentStat[2] = String.valueOf(npDiasPressure.getValue());
					db.editStat(currentStat, String.valueOf(idCurrentName));
					getSupportLoaderManager().getLoader(0).forceLoad();
					saveData();
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
	}

	//
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_ID) {
			db.delRecStat(acmi.id);
			// получаем новый курсор с данными
			getSupportLoaderManager().getLoader(0).forceLoad();
			deleteData();
			return true;
		} else if (item.getItemId() == CM_EDIT_ID) {
			currentStat = db.getCurrentStat(acmi.id);
			idCurrentName = acmi.id;
			Log.d(LOG_TAG, "row inserted, id= " + idCurrentName);
			Log.d(LOG_TAG, "row inserted, pulse = " + currentStat[0]);
			Log.d(LOG_TAG, "row inserted, sys= " + currentStat[1]);
			Log.d(LOG_TAG, "row inserted, dias= " + currentStat[2]);
			show();

			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Log.d(LOG_TAG, "row inserted, id= " + idCurrentName);
		switch (v.getId()) {
		case R.id.btnAddStat:
			show();
			break;
		case R.id.btnGraph:
			Intent intent = new Intent(MyStatistic.this, Graph.class);
			
//			id_stat = db.getCurrentStat(Long.valueOf(profile_id));
			intent.putExtra("id_stat_key", profile_id);
			intent.putExtra("id_stat_count", String.valueOf(listStat.getCount()));
		    startActivity(intent);
			break;
		}
	}

	public void showGraph() {
		final Dialog dialog = new Dialog(MyStatistic.this);
		dialog.setContentView(R.layout.graph);
		dialog.setTitle("Graph");

		int array[] = new int[] { 1, 2, 3, 4, 5, 6 };
		int array2[] = new int[] { 10, 16, 2, 12, 20, 28 };
		// init example series data
		int num = 6;
		GraphViewData[] data = new GraphViewData[num];
		for (int i = 0; i < num; i++) {

			data[i] = new GraphViewData(array[i], array2[i]);
		}

		GraphView graphView = new LineGraphView(this // context
				, "GraphViewDemo" // heading
		);
		graphView.addSeries(new GraphViewSeries(data)); // data

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);

		dialog.show();
	}

	public void showSave() {
		final Dialog dialog = new Dialog(MyStatistic.this);
		dialog.setContentView(R.layout.dialog_save);
		dialog.setTitle("Save your data");

		final Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

		final Button btnEmail = (Button) dialog.findViewById(R.id.btnEmail);

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				writeFileSD(profile_name[0]);
				dialog.dismiss();
			}
		});

		btnEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// writeFileSD(profile_name[0]);
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);

				emailIntent.setType("plain/text");
				// Кому
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { profile_name[1].toString() });
				// Зачем
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Pressure diary");
				// О чём
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						"Hello, " + profile_name[0]
								+ ", this is your statistic:");
				emailIntent.putExtra(
						android.content.Intent.EXTRA_STREAM,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory()
								+ "/" + DIR_SD + "/" + FILENAME_SD + "_for_"
								+ profile_name[0] + ".txt"));
				Log.d(LOG_TAG,
						"genm: " + Environment.getExternalStorageDirectory()
								+ "/" + DIR_SD + "/" + FILENAME_SD + "_for_"
								+ profile_name[0] + ".txt");

				MyStatistic.this.startActivity(Intent.createChooser(
						emailIntent, "Отправка письма..."));
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			showSave();
			return true;
		}

		return super.onKeyDown(keycode, e);
	}

	//
	void writeFileSD(String path) {
		// проверяем доступность SD
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.d(LOG_TAG,
					"SD-card is no available: "
							+ Environment.getExternalStorageState());
			return;
		}
		// получаем путь к SD
		File sdPath = Environment.getExternalStorageDirectory();
		// добавляем свой каталог к пути
		sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
		// создаем каталог
		sdPath.mkdirs();
		// формируем объект File, который содержит путь к файлу
		File sdFile = new File(sdPath, FILENAME_SD + "_for_" + path + ".txt");

		String[] name = new String[] { "", "", "", "", "", "" };

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
			for (int i = 0; i < listStat.getCount(); i++) {
				Cursor cur = (Cursor) listStat.getAdapter().getItem(i);
				long temp = cur.getLong(cur.getColumnIndex("_id"));
				name = db.getCurrentStat(temp);
				bw.write(name[4] + "   " + name[1] + "/" + name[2] + "    "
						+ name[0] + "   " + name[5] + "\n");
			}
			bw.close();
			saveOnSD();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	void correctData() {
		Toast.makeText(this, R.string.correct, Toast.LENGTH_SHORT).show();
	}

	void saveOnSD() {
		Toast.makeText(this, R.string.save_on_SD, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub

	}
}