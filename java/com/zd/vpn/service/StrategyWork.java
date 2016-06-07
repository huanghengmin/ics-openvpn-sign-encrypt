package com.zd.vpn.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.zd.vpn.checker.PropertiesUtils;
import com.zd.vpn.receiver.BluetoothReceiver;
import com.zd.vpn.receiver.WifiReceiver;
import com.zd.vpn.util.FileUtil;
import com.zd.vpn.util.PostRequest;
import com.zd.vpn.util.StrategyUtil;

import java.util.TimerTask;

public class StrategyWork extends TimerTask {
    private String TAG = "StrategyWork";
    private Context context;

    private WifiReceiver wifiReceiver = new WifiReceiver();
    private boolean register_wifi_receiver = false;

    private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    private boolean register_bluetooth_receiver = false;

    public StrategyWork(Context context) {
        this.context = context;
    }

    public static void setWifiEnable(Context paramContext, boolean paramBoolean) {
        ((WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(paramBoolean);
    }

    public static boolean wifiIsEnable(Context paramContext) {
        return ((WifiManager) paramContext.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
    }

    public static void setBluetoothEnable(boolean paramBoolean) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (paramBoolean) {
            bluetoothAdapter.enable();
            return;
        }
        bluetoothAdapter.disable();
    }

    public static boolean bluetoothIsEnable() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    @Override
    public void run() {
        Log.i(TAG,"StrategyWork start working...... ");
        String save_path = context.getFilesDir().getAbsolutePath();
        SharedPreferences shPreferences = context.getSharedPreferences("com.zd.vpn", Context.MODE_PRIVATE);
        String ip = shPreferences.getString("vpn.ip", null);
        String strategyPort = shPreferences.getString("vpn.poliPort", null);
        if (ip != null&&ip.length()>0 && strategyPort != null&&strategyPort.length()>0) {
            String down_strategy_url = "http://" + ip + ":" + strategyPort + PropertiesUtils.DOWN_STRATEGY;
            //取出回应字串
            try {
                byte[] strategy_data = PostRequest.getData(down_strategy_url);
                if (strategy_data != null)
                    FileUtil.copy(strategy_data, save_path + PropertiesUtils.STRATEGY_PATH);

                StrategyUtil strategyUtil = new StrategyUtil(context);
                if (strategyUtil.getWifi()) {
                    if (wifiIsEnable(context)) {
                        setWifiEnable(context, false);
                    }
                    //通过代码的方式动态注册BroadcastReceiver
                    if (!register_wifi_receiver) {
                        IntentFilter wifiFilter = new IntentFilter();
                        wifiFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                        //注册receiver
                        context.registerReceiver(wifiReceiver, wifiFilter);
                        register_wifi_receiver = true;
                    }
                } else {
                    if (register_wifi_receiver) {
                        context.unregisterReceiver(wifiReceiver);
                        register_wifi_receiver = false;
                    }
                }

                if (strategyUtil.getBluetooth()) {
                    if (bluetoothIsEnable()) {
                        setBluetoothEnable(false);
                    }
                    //通过代码的方式动态注册BroadcastReceiver
                    if (!register_bluetooth_receiver) {
                        IntentFilter bluetoothFilter = new IntentFilter();
                        bluetoothFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
                        //注册receiver
                        context.registerReceiver(bluetoothReceiver, bluetoothFilter);
                        register_bluetooth_receiver = true;
                    }
                } else {
                    if (register_bluetooth_receiver) {
                        context.unregisterReceiver(bluetoothReceiver);
                        register_bluetooth_receiver = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
};