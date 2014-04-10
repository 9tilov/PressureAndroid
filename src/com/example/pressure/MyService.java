package com.example.pressure;

import java.util.concurrent.TimeUnit;

import com.pushbots.push.Pushbots;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class MyService extends Service {
	NotificationManager nm;
	AlarmManager am;
	PendingIntent pIntent;
	

	@Override
	public void onCreate() {
		super.onCreate();
		Pushbots.init(this, "485155082084", "53463ebf1d0ab1d5048b456f");
		Pushbots.getInstance().setMsgReceiver(Receiver.class);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
//		am.setRepeating(AlarmManager.ELAPSED_REALTIME,
//		        SystemClock.elapsedRealtime() + 3000, 5000, pIntent);
	}

//	public int onStartCommand(Intent intent, int flags, int startId) {
////		try {
////			TimeUnit.SECONDS.sleep(4);
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////		}
//		am.setRepeating(AlarmManager.ELAPSED_REALTIME,
//		        SystemClock.elapsedRealtime() + 3000, 1000, pIntent);
////		sendNotif();
//		return super.onStartCommand(intent, flags, startId);
//	}

	void sendNotif() {
		// 1-я часть
		Notification notif = new Notification(R.drawable.ic_pressure,
				"Pressure", System.currentTimeMillis());

		// 3-я часть
		Intent intent = new Intent(this, MainActivity.class);
		pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// 2-я часть
		notif.setLatestEventInfo(this, "Pressure",
				"Please, measure your pressure", pIntent);

		// ставим флаг, чтобы уведомление пропало после нажатия
		notif.flags |= Notification.FLAG_AUTO_CANCEL;

		// отправляем
		nm.notify(1, notif);
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}
}
