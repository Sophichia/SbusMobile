package edu.usc.imsc.sbus.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import edu.usc.imsc.sbus.R;

/**
 * Created by Mengjia on 16/1/11.
 */
public class AlertReceiver extends BroadcastReceiver{
    public NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    int notifyId = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("msg");
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews views_custom = new RemoteViews(context.getPackageName(), R.layout.view_custom);
        views_custom.setImageViewResource(R.id.custom_icon,R.drawable.ic_bus1);
        views_custom.setTextViewText(R.id.tv_custom_title, "SBus");
        views_custom.setTextViewText(R.id.tv_custom_content, msg);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContent(views_custom)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setTicker("Ticker")
                .setOngoing(false);
        Notification notify = mBuilder.build();
        notify.contentView = views_custom;
        mNotificationManager.notify(notifyId,notify);
        Log.d("notification","Have send the notification");
    }
}
