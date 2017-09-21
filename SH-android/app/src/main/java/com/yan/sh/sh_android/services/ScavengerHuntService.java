package com.yan.sh.sh_android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.yan.sh.sh_android.engine.Engine;

import timber.log.Timber;

/**
 * Created by yan on 6/11/17.
 */

public class ScavengerHuntService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("Start SH Services - Initialize Engine");
        Engine.startup(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }
}
