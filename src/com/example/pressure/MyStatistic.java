package com.example.pressure;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class MyStatistic extends Activity implements OnClickListener {
	private TextView name;
	MyDB db;
	Button btnAdd;
	EditText etPulse, etSysPressure, etDiasPressure;

	final int DIALOG_STAT = 1;
	final String LOG_TAG = "myLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistic);

		btnAdd = (Button) findViewById(R.id.btnAddStat);
		btnAdd.setOnClickListener(this);

		db = new MyDB(this);
		db.open();

		name = (TextView) findViewById(R.id.profile_name);
		String profile_id = getIntent().getStringExtra("lvData");// принимаем id
																	// item'a из
																	// списка
		String profile_name = db.getCurrentName(Long.parseLong(profile_id));
		name.setText(profile_name);

	}

	DialogInterface.OnClickListener myClickListenerStat = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			// положительная кнопка
			case Dialog.BUTTON_POSITIVE:
				if ((etPulse.getText().toString().length() == 0)
						&& (etSysPressure.getText().toString().length() == 0)
						&& (etDiasPressure.getText().toString().length() == 0)) {
					showDialog(DIALOG_STAT);
					break;
				} else {
					db.addStat(etPulse.getText().toString(), etSysPressure
							.getText().toString(), etDiasPressure.getText()
							.toString());
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

	void saveData() {
		Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
	}

	void addData() {
		Toast.makeText(this, R.string.add, Toast.LENGTH_SHORT).show();
	}
}