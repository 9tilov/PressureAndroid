package com.example.pressure;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class Receiver extends BroadcastReceiver {

//	private static String m_message = "asas";

//	public Receiver() {
//
//	}
//
//	public Receiver(String m) {
//		super();
//		m_message = m;
//	}
	
	
	MyDB db;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		db = new MyDB(context);
		db.open();
		Bundle bundle = intent.getExtras();
		String m_message = bundle.getString("message", "dff");
		
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notif = new Notification(R.drawable.ic_pressure,
				m_message, System.currentTimeMillis());

		intent = new Intent(context, MainActivity.class);
		
		notif.setLatestEventInfo(context, "Pressure", m_message, PendingIntent
				.getActivity(context, 0, intent,
						PendingIntent.FLAG_CANCEL_CURRENT));
		notif.flags = Notification.DEFAULT_LIGHTS
				| Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, notif);
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}
}