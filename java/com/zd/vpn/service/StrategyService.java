package com.zd.vpn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

public class StrategyService extends Service {
    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.schedule(new StrategyWork(this), 0, 1000 * 60 * 2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}