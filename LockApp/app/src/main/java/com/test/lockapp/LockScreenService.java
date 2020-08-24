package com.test.lockapp;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service {

    private LockScreenReceiver mlockScreenReceiver;
    private IntentFilter mIntentFilter = new IntentFilter();
    private boolean isNotiShow = false;

    public LockScreenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //动态注册
        mIntentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mIntentFilter.addAction(Intent.ACTION_TIME_TICK);

        mIntentFilter.setPriority(IntentFilter.MAX_);

        return super.onStartCommand(intent, flags, startId);
    }
}
