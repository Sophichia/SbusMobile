package edu.usc.imsc.sbus.activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.*;
/**
 * Created by Mengjia on 16/1/11.
 * this activity used for notification
 */
public class BaseActivity extends ActionBarActivity{
    public NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService();
    }

    private void initService(){
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    public void clearNotifity(int notifyId) {
        mNotificationManager.cancel(notifyId);
    }

    public PendingIntent getDefaultIntent(int flag){
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,new Intent(),flag);
        return pendingIntent;
    }

}
