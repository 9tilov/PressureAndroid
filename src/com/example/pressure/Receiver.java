package com.example.pressure;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver {

	NotificationManager nm;

	 @Override
	 public void onReceive(Context context, Intent intent) {
	  nm = (NotificationManager) context
	    .getSystemService(Context.NOTIFICATION_SERVICE);
	  CharSequence from = "Nithin";
	  CharSequence message = "Crazy About Android...";
	  PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
	    new Intent(), 0);
	  Notification notif = new Notification(R.drawable.ic_pressure,
	    "Crazy About Android...", System.currentTimeMillis());
	  notif.setLatestEventInfo(context, from, message, contentIntent);
	  notif.flags |= Notification.FLAG_AUTO_CANCEL;
	  nm.notify(1, notif);
	  
	 }
	}