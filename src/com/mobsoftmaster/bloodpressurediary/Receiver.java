package com.mobsoftmaster.bloodpressurediary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class Receiver extends BroadcastReceiver {

	final String LOG_TAG = "Pressure";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String m_message = bundle.getString("message");
		String app_name = bundle.getString("appName");

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notif = new Notification(R.drawable.ic_logo_app,
				m_message, System.currentTimeMillis());

		intent = new Intent(context, Settings.class);

		notif.setLatestEventInfo(context, app_name, m_message, PendingIntent
				.getActivity(context, 0, intent,
						PendingIntent.FLAG_CANCEL_CURRENT));
		notif.flags = Notification.DEFAULT_LIGHTS
				| Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE;
		nm.notify(1, notif);
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}
}