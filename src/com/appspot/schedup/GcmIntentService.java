package com.appspot.schedup;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	int notificationIdCounter = 0;
	
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	String title = extras.getString("title");
            	String body = extras.getString("body");
            	notifyUser(title == null ? "Event" : title, body == null ? "" : body);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected int notifyUser(String title, String body) {
    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.ic_launcher)
    	        .setContentTitle(title)
    	        .setContentText(body)
    	        .setAutoCancel(true);
    	// Creates an explicit intent for an Activity in your app
    	Intent resultIntent = new Intent(this, MainActivity.class);
    	PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
    	
    	mBuilder.setContentIntent(pendingIntent);
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	// mId allows you to update the notification later on.
    	int mId = notificationIdCounter;
    	notificationIdCounter++;
    	mNotificationManager.notify(mId, mBuilder.build());
    	return mId;
    }
}

