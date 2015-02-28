package com.mobsoftmaster.bloodpressurediary2;

import com.mobsoftmaster.bloodpressurediary2.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// You can do the processing here update the widget/remote views.
		Bundle extras = intent.getExtras();
		String appName = extras.getString("appName");
		String message = extras.getString("message");
		SharedPreference.SavePreferences(context, SharedPreference.s_state, false);
		Intent myIntent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(appName)
				.setContentText(message)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification n = mBuilder.getNotification();
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(1, n);

	}
}
