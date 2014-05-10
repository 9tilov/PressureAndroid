package com.example.pressure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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

public class MyStatistic extends FragmentActivity implements OnClickListener,
		LoaderCallbacks<Cursor>, NumberPicker.OnValueChangeListener {

	private static final int CM_DELETE_ID = 0, CM_EDIT_ID = 1,
			CM_DELETE_ALL_ID = 2;
	public static final int SAVE_DATA_FLAG = 0, EDIT_DATA_FLAG = 1;

	int flag, idCurrentName;

	String formattedDate, formattedTime;

	MyDB db;
	static int profile_id;
	SimpleCursorAdapter scAdapter;

	String[] profile_name;

	String[] savedValues = new String[2];

	ListView listStat;

	final String DIR_SD = "Pressure";
	final String FILENAME_SD = "pressure_stat";

	String[] currentStat = new String[] { "", "", "" };
	final String LOG_TAG = "myLogs";

	String s;

	ImageView btnAddStat;

	TextView tv;

	boolean rotation;

	int number_of_elements;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic);
		db = new MyDB(this);
		db.open();

		profile_id = db.LoadID();
		rotation = db.LoadRotation();

		btnAddStat = (ImageView) findViewById(R.id.btnAddStat);
		btnAddStat.setOnClickListener(this);
		Animation animRotateIn_icon = AnimationUtils.loadAnimation(this,
				R.anim.rotate);
		btnAddStat.startAnimation(animRotateIn_icon);

		number_of_elements = db.getCountElementsStat();

		if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				&& (rotation) && (number_of_elements >= 7)) {
			Intent intent = new Intent(MyStatistic.this, Graph.class);
			startActivity(intent);
		}

		TextView btnProfile = (TextView) findViewById(R.id.btnProfile);
		btnProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				db.SavePreferences("state", true);
				finish();
			}
		});
		// формируем столбцы сопоставления
		String[] from = new String[] { MyDB.COLUMN_PULSE,
				MyDB.COLUMN_SYS_PRESSURE, MyDB.COLUMN_DIAS_PRESSURE,
				MyDB.COLUMN_DATE, MyDB.COLUMN_TIME };
		int[] to = new int[] { R.id.tvTextPulse, R.id.tvTextSys,
				R.id.tvTextDias, R.id.tvTextDate, R.id.tvTextTime };

		profile_name = db.getCurrentName(profile_id);

		btnProfile.setText(profile_name[0]);

		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(this, R.layout.list, null, from,
				to, 0);
		listStat = (ListView) findViewById(R.id.listStat);
		registerForContextMenu(listStat);

		scAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				String s;
				s = String.valueOf(cursor.getInt(columnIndex));
				TextView tv = (TextView) view;
				switch (view.getId()) {
				case R.id.tvTextPulse:
					setColor(48, 49, 59, 60, 80, 81, 100, 101, tv, s);
					break;
				case R.id.tvTextSys:
					setColor(99, 100, 109, 110, 130, 131, 139, 140, tv, s);
					break;
				case R.id.tvTextDias:
					setColor(59, 60, 69, 70, 85, 86, 90, 91, tv, s);
					break;
				}
				return false;
			}

		});

		listStat.setAdapter(scAdapter);
		scrollMyListViewToBottom();

		// // создаем лоадер для чтения данных
		getSupportLoaderManager().initLoader(0, null, this);
		// //Получаем текущее время
		Calendar c = Calendar.getInstance();
		SimpleDateFormat date = new SimpleDateFormat("dd.MM");
		SimpleDateFormat time = new SimpleDateFormat("HH:mm");
		formattedDate = date.format(c.getTime());
		formattedTime = time.format(c.getTime());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAddStat:
			flag = SAVE_DATA_FLAG;
			show();
			break;
		}
	}

	public void setColor(int redLower, int fromYellowLower, int toYellowLower,
			int fromGreen, int toGreen, int fromYellowUp, int toYellowUp,
			int redUp, TextView tv, String s) {
		int index = Integer.valueOf(s);
		if ((index >= fromGreen) && (index <= toGreen)) {
			tv.setTextColor(Color.GREEN);
			tv.setText(s);
		} else if (((index >= fromYellowLower) && (index <= toYellowLower))
				|| ((index >= fromYellowUp) && (index <= toYellowUp))) {
			tv.setTextColor(Color.YELLOW);
			tv.setText(s);
		} else if ((index >= redUp) || (index <= redLower)) {
			tv.setTextColor(Color.RED);
			tv.setText(s);
		}
	}

	private void scrollMyListViewToBottom() {
		listStat.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				listStat.setSelection(number_of_elements);
			}
		});
	}

	public void show() {

		final Dialog dialog = new Dialog(MyStatistic.this);
		dialog.setContentView(R.layout.dialog_stat);
		dialog.setTitle("Statictics");

		final NumberPicker npPulse = initNumberPicker(20, 200, R.id.npPulse,
				dialog);
		final NumberPicker npSysPressure = initNumberPicker(60, 300,
				R.id.npSysPressure, dialog);
		final NumberPicker npDiasPressure = initNumberPicker(40, 250,
				R.id.npDiasPressure, dialog);

		if (flag == EDIT_DATA_FLAG) {
			npPulse.setValue(Integer.valueOf(currentStat[0]));
			npSysPressure.setValue(Integer.valueOf(currentStat[1]));
			npDiasPressure.setValue(Integer.valueOf(currentStat[2]));
		} else if (flag == SAVE_DATA_FLAG) {
			npPulse.setValue(70);
			npSysPressure.setValue(120);
			npDiasPressure.setValue(80);
		}

		Button btnSaveStat = (Button) dialog.getWindow().findViewById(
				R.id.btnSaveState);

		btnSaveStat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag == SAVE_DATA_FLAG) {
					db.addStat(String.valueOf(npPulse.getValue()),
							String.valueOf(npSysPressure.getValue()),
							String.valueOf(npDiasPressure.getValue()),
							profile_id, formattedDate, formattedTime);
					getSupportLoaderManager().getLoader(0).forceLoad();
					dialog.dismiss();
				} else if (flag == EDIT_DATA_FLAG) {
					currentStat[0] = String.valueOf(npPulse.getValue());
					currentStat[1] = String.valueOf(npSysPressure.getValue());
					currentStat[2] = String.valueOf(npDiasPressure.getValue());
					db.editStat(currentStat, String.valueOf(idCurrentName));
					getSupportLoaderManager().getLoader(0).forceLoad();
					saveData();
					dialog.dismiss();
				}
				addData();
				scrollMyListViewToBottom();
			}
		});
		dialog.show();
	}

	public NumberPicker initNumberPicker(int min, int max, int id, Dialog dialog) {
		final NumberPicker np = (NumberPicker) dialog.findViewById(id);
		np.setMaxValue(max);
		np.setMinValue(min);
		np.setWrapSelectorWheel(false);
		np.setOnValueChangedListener(this);
		return np;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_EDIT_ID, 0, R.string.edit_record);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
		menu.add(0, CM_DELETE_ALL_ID, 0, R.string.delete_all_records);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CM_DELETE_ID) {
			db.delRecStat(acmi.id);
			// получаем новый курсор с данными
			getSupportLoaderManager().getLoader(0).forceLoad();
			deleteData();
			scrollMyListViewToBottom();
			return true;
		} else if (item.getItemId() == CM_EDIT_ID) {
			currentStat = db.getCurrentStat(acmi.id);
			idCurrentName = (int) acmi.id;
			flag = EDIT_DATA_FLAG;
			show();
			scrollMyListViewToBottom();
			return true;
		} else if (item.getItemId() == CM_DELETE_ALL_ID) {
			showChoice();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void showSave() {
		final Dialog dialog = new Dialog(MyStatistic.this);
		dialog.setContentView(R.layout.dialog_save);
		dialog.setTitle("Settings");

		Button btnSave = (Button) dialog.findViewById(R.id.btnSave);

		Button btnEmail = (Button) dialog.findViewById(R.id.btnEmail);

		Button btnGraph = (Button) dialog.findViewById(R.id.btnGraph);

		if (!rotation)
			btnGraph.setEnabled(true);
		else
			btnGraph.setEnabled(false);

		btnGraph.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listStat.getCount() < 7) {
					graphShow();
					dialog.dismiss();
				} else {
					Intent intent = new Intent(MyStatistic.this, Graph.class);
					startActivityForResult(intent, 1);
					dialog.dismiss();
				}
			}
		});

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
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				writeFileSD(profile_name[0]);
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

	public void showChoice() {
		final Dialog dialog = new Dialog(MyStatistic.this);
		dialog.setContentView(R.layout.dialog_choice);
		dialog.setTitle("Are you sure?");

		Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
		Button btnNo = (Button) dialog.findViewById(R.id.btnNo);

		btnYes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				db.delRecAllStat(Long.valueOf(profile_id));
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
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			showSave();
			break;
		}
		return super.onKeyDown(keycode, e);
	}

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
		Log.d(LOG_TAG, "closeDB");
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

	void saveOnSD() {
		Toast.makeText(this, R.string.save_on_SD, Toast.LENGTH_SHORT).show();
	}

	void graphShow() {
		Toast.makeText(this, R.string.graphShow, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub
	}

	// Запрещаем использование кнопки "Назад"
	@Override
	public void onBackPressed() {
	}

}