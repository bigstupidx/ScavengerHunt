package com.yan.sh.sh_android.engine.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yan.sh.sh_android.engine.Engine;

import timber.log.Timber;

/**
 * Created by yan on 6/29/17.
 */

public class BroadcastManager extends BroadcastReceiver {

    //Manager implementation
    private boolean started = false;
    public void startup(){
        started = true;
    }
    public void shutdown(){
        started = false;
    }
    public boolean isStarted(){
        return started;
    }

    public BroadcastManager(){
        super();
        startup();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //only network at the moment
        if(intent.getAction().matches("android.net.conn.CONNECTIVITY_CHANGE")){
            Engine.hardware().checkInternet();
        } else if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Engine.hardware().isLocationEnabled();
        }
    }
}
