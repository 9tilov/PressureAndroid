package com.mobsoftmaster.bloodpressurediary2;

import com.mobsoftmaster.bloodpressurediary2.R;

import java.util.Calendar;
import java.util.Locale;

import com.mobsoftmaster.bloodpressurediary2.EditNameDialog.EditNameDialogListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor>, EditNameDialogListener {

	private static final int CM_EDIT_ID = 0, CM_DELETE_ID = 1;
	private static final int REQUEST_CODE_EMAIL_AUTO = 2;
	private static final int REQUEST_CODE_SCREEN_ADD_NEW_USER = 3;
	private static final int REQUEST_CODE_SCREEN_STATISTIC = 4;

	MyDB db;

	SimpleCursorAdapter scAdapter;

	long idCurrentName;
	EditText editName, editMail, addName;
	String possibleEmail = "";
	String[] currentProfile = new String[] { "", "" };
	String accountName = "";

	TextView tvProfileTitle;

	boolean tutorial;

	final String LOG_TAG = "myLogs";
	private AdView mAdView;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Get a Tracker (should auto-report)
		// Intent intent1 = new Intent(MainActivity.this, ProgrammInfo.class);
		// startActivity(intent1);
		((Locales) getApplication())
				.getTracker(Locales.TrackerName.APP_TRACKER);

		// открываем подключение к БД
		db = new MyDB(this);
		db.open();

		Configuration c = new Configuration(getResources().getConfiguration());

		setTitle(R.string.app_name);
		Typeface font = Typeface.createFromAsset(getAssets(), "Dashley.ttf");
		tvProfileTitle = (TextView) findViewById(R.id.tvProfileTitle);
		tvProfileTitle.setTypeface(font);

		int language = SharedPreference.LoadLanguage(this);
		Log.d(LOG_TAG, "lang = " + language);
		switch (language) {

		case 0:
			c.locale = Locale.getDefault();
			break;
		case 1:
			Locale myLocale_en = new Locale("en", "US");
			c.locale = myLocale_en;
			break;
		case 2:
			Locale myLocale_ru = new Locale("ru", "RU");
			c.locale = myLocale_ru;
			break;
		case 3:
			Locale myLocale_cn = new Locale("zh", "CN");
			c.locale = myLocale_cn;
			break;
		}

		Log.d(LOG_TAG, "lang1 = " + R.string.profile_list);
		getResources().updateConfiguration(c,
				getResources().getDisplayMetrics());

		boolean stateActivity = SharedPreference.LoadState(this);

		tutorial = SharedPreference.LoadTutorial(this);

		if (tutorial) {
			Intent intent_tutor = new Intent(MainActivity.this, Tutorial.class);
			startActivityForResult(intent_tutor, REQUEST_CODE_EMAIL_AUTO);
		}

		if (!stateActivity) {
			Intent intent_stat = new Intent(MainActivity.this,
					MyStatistic.class);
			startActivityForResult(intent_stat, REQUEST_CODE_SCREEN_STATISTIC);
		}

		// формируем столбцы сопоставления
		String[] from = new String[] { MyDB.COLUMN_NAME, MyDB.COLUMN_EMAIL };
		int[] to = new int[] { R.id.tvName, R.id.tvMail };
		ImageView addProfile = (ImageView) findViewById(R.id.addProfile);
		ImageView btnSettings = (ImageView) findViewById(R.id.imageButtonSettings);

		addProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showEditNameDialog(0, true, true, "", "");
			}
		});

		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Settings.class);
				startActivity(intent);
				overridePendingTransition(R.anim.open_window_start,
						R.anim.open_window_end);
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

		if (db.emptyDataBase())
			getUserEmailAuto(0);
		else
			lvData.setAdapter(scAdapter);

		lvData.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.this, MyStatistic.class);
				Cursor cur = (Cursor) lvData.getAdapter().getItem(position);
				int id_name = cur.getInt(cur.getColumnIndex("_id"));
				SharedPreference.saveID(getBaseContext(),
						SharedPreference.s_id, id_name);
				SharedPreference.SavePreferences(getBaseContext(),
						SharedPreference.s_state, false);
				startActivity(intent);
				overridePendingTransition(R.anim.open_window_start,
						R.anim.open_window_end);
			}
		});
		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				AdRequest.DEVICE_ID_EMULATOR).build();
		mAdView.loadAd(adRequest);
	}

	@Override
	public void onFinishEditDialog(int id, boolean isProfileAdition,
			boolean isGetEmailFromAccount, String inputText_name,
			String inputText_email) {
		if ((0 == inputText_name.length())) {
			inCorrectName();
			showEditNameDialog(id, true, true, "", "");
		} else if (!isValidEmail(inputText_email))
			inCorrectEmail();
		else {
			accountName = inputText_name;
			if (isProfileAdition) {
				if (isGetEmailFromAccount)
					getUserEmailAuto(id);
				else
					db.addRec(accountName, inputText_email);

			} else
				db.editRec(accountName, inputText_email, String.valueOf(id));

			final ListView lvData = (ListView) findViewById(R.id.lvData);
			lvData.setAdapter(scAdapter);
			getSupportLoaderManager().getLoader(0).forceLoad();
		}
		startActivityForResult(getIntent(), REQUEST_CODE_SCREEN_ADD_NEW_USER);
	}

	void showEditNameDialog(int id, boolean isProfileAdition,
			boolean isGetEmailFromAccount, String name, String email) {
		final FragmentManager fm = getSupportFragmentManager();
		EditNameDialog dFragment = EditNameDialog.newInstance(id,
				isProfileAdition, isGetEmailFromAccount, name, email);
		dFragment.show(fm, "Dialog Fragment");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(MainActivity.this, Settings.class);
			startActivity(intent);
			overridePendingTransition(R.anim.open_window_start,
					R.anim.open_window_end);
			return true;
		}
		return super.onOptionsItemSelected(item);
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

		Intent intent = new Intent(this, AlarmManagerBroadcastReceiver.class);
		intent.putExtra("message", message);
		intent.putExtra("appName", res.getString(R.string.app_name));

		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, alarm, 5000, pendingIntent);
		if (!notif)
			am.cancel(pendingIntent);
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
		menu.add(0, CM_EDIT_ID, 0, R.string.edit_profile);
		menu.add(0, CM_DELETE_ID, 0, R.string.delete_profile);
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

			showEditNameDialog((int) acmi.id, false, true, currentProfile[0],
					currentProfile[1]);

			return true;
		}
		return super.onContextItemSelected(item);
	}

	boolean isValidEmail(String target) {
		if (target.length() == 0) {
			return true;
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

	void inCorrectName() {
		Toast.makeText(this, R.string.incorrect_name, Toast.LENGTH_SHORT)
				.show();
	}

	void inCorrectEmail() {
		Toast.makeText(this, R.string.incorrect_email, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle(R.string.app_name);
		tvProfileTitle.setText(R.string.profile_list);
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
	protected void onStart() {
		super.onStart();
		// Get an Analytics tracker to report app starts & uncaught exceptions
		// etc.
		GoogleAnalytics.getInstance(this).reportActivityStart(this);

	}

	@Override
	protected void onStop() {

		// Stop the analytics tracking
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		super.onStop();
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

	private void getUserEmailAuto(int id) {
		try {
			Intent intent = AccountPicker.newChooseAccountIntent(null, null,
					new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false,
					null, null, null, null);
			startActivityForResult(intent, REQUEST_CODE_EMAIL_AUTO);
		} catch (ActivityNotFoundException e) {
			// TODO
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "back");
		if (requestCode == REQUEST_CODE_EMAIL_AUTO && resultCode == RESULT_OK) {
			String accountEmail = data
					.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Resources res = getResources();
			if (db.emptyDataBase())
				db.addRec(res.getString(R.string.guest), accountEmail);
			else
				db.addRec(accountName, accountEmail);
			final ListView lvData = (ListView) findViewById(R.id.lvData);
			lvData.setAdapter(scAdapter);
			getSupportLoaderManager().getLoader(0).forceLoad();

		}
	}
}