package com.mobsoftmaster.bloodpressurediary;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.Patterns;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends TrackedActivity implements
		LoaderCallbacks<Cursor> {

	private static final int CM_EDIT_ID = 0, CM_DELETE_ID = 1;

	MyDB db;
	SharedPreference sharedPref;

	SimpleCursorAdapter scAdapter;

	long idCurrentName;
	EditText editName, editMail, addName;

	String[] currentProfile = new String[] { "", "" };

	final String LOG_TAG = "Pressure";
	final int DIALOG_EDIT = 1, DIALOG_ADD = 2;
	private AdView mAdView;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// открываем подключение к БД
		db = new MyDB(this);
		db.open();

		Configuration c = new Configuration(getResources().getConfiguration());

		sharedPref = new SharedPreference(this);

		setTitle(R.string.app_name);
		
		int language = sharedPref.LoadLanguage();

		switch (language) {
		case 0:
			c.locale = Locale.getDefault();
			break;
		case 1:
			c.locale = Locale.ENGLISH;
			break;
		case 2:
			Locale myLocale = new Locale("ru", "RU");
			c.locale = myLocale;
			break;
		case 3:
			c.locale = Locale.CHINESE;
			break;
		}

		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		startService(new Intent(this, Receiver.class));

		boolean notification = sharedPref.LoadNotification();
		boolean stateActivity = sharedPref.LoadState();

		if (stateActivity) {
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
			if ((notification))
				setRepeatingAlarm(am, Integer.valueOf(data_notif_fields[i]),
						list.get(i)[0], Integer.valueOf(list.get(i)[1]),
						Integer.valueOf(list.get(i)[2]), notification);
		}

		// жесть кончилась

		// формируем столбцы сопоставления
		String[] from = new String[] { MyDB.COLUMN_NAME };
		int[] to = new int[] { R.id.tvName };

		ImageButton addProfile = (ImageButton) findViewById(R.id.addProfile);
		ImageButton btnSettings = (ImageButton) findViewById(R.id.imageButtonSettings);

		addProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				idCurrentName = 0;
				currentProfile[0] = "";
				currentProfile[1] = "";
				showDialog(DIALOG_ADD);
			}
		});

		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Settings.class);
				startActivity(intent);
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
			Resources res = getResources();

			db.addRec(res.getString(R.string.guest), getUserEmail());
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
				int id_name = cur.getInt(cur.getColumnIndex("_id"));
				sharedPref.saveID(sharedPref.s_id, id_name);
				sharedPref.SavePreferences(sharedPref.s_state, false);
				startActivity(intent);
			}
		});
		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				AdRequest.DEVICE_ID_EMULATOR).build();
		mAdView.loadAd(adRequest);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	public void setRepeatingAlarm(AlarmManager am, int id, String message,
			int hour, int minute, Boolean notif) {
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

		Resources res = getResources();

		Intent intent = new Intent(this, Receiver.class);
		intent.putExtra("message", message);
		intent.putExtra("appName", res.getString(R.string.app_name));

		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, alarm,
				AlarmManager.INTERVAL_DAY, pendingIntent);
		if (!notif)
			am.cancel(pendingIntent);
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
			editMail = (EditText) dialog.getWindow().findViewById(R.id.addMail);
			String t = getUserEmail();
			if (t != "") {
				editMail.setText(t);
			}
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
						db.addRec(addName.getText().toString(), editMail
								.getText().toString());
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
			adb.setPositiveButton(R.string.save, myClickListener);
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
			adb.setPositiveButton(R.string.save, myClickListener);
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
			currentProfile = db.getCurrentName((int) acmi.id);
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
		Toast.makeText(this, R.string.profile_deleted, Toast.LENGTH_SHORT)
				.show();
	}

	void addData() {
		Toast.makeText(this, R.string.profile_added, Toast.LENGTH_SHORT).show();
	}

	void inCorrectData() {
		Toast.makeText(this, R.string.correct_name, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdView.resume();
	}

	@Override
	protected void onPause() {
		mAdView.pause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mAdView.destroy();
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

	private String getUserEmail() {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(this).getAccounts();
		String possibleEmail = "";
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				possibleEmail = account.name;
				break;
			}
		}
		return possibleEmail;
	}
}