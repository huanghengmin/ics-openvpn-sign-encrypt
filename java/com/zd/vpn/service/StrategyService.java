package com.zd.vpn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

public class StrategyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Timer timer = new Timer();
        timer.schedule(new StrategyWork(getApplicationContext()),0, 1000*60*2);
        
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
    }
}