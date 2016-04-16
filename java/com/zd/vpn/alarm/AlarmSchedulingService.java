package com.zd.vpn.alarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.zd.vpn.R;
import com.zd.vpn.ui.MoreActivity;

public class AlarmSchedulingService extends IntentService {

    public AlarmSchedulingService() {
        super("SchedulingService");
    }

    public static final String TAG = "Scheduling Service";

    public static final int NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;

    private String readIp(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        String ip = sPreferences.getString("vpn.ip",null);
        return  ip;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ip = readIp(getApplicationContext());
        if (ip != null) {
            String result = PingUtils.ping(ip);
            if (result == null) {
               sendNotification("网关服务器请求失败！");
            }
            PingUtils.ping(ip);
        }
        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MoreActivity.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(getString(R.string.ping_result))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
