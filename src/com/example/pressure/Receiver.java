package com.example.pressure;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notif = new Notification(R.drawable.ic_pressure,
				"Measure your pressure...", System.currentTimeMillis());

		intent = new Intent(context, MainActivity.class);
		notif.setLatestEventInfo(context, "Pressure",
				"Measure your pressure...", PendingIntent.getActivity(context,
						0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
		notif.flags = Notification.DEFAULT_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, notif);
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}
}