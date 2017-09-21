package com.yan.sh.sh_android.engine.managers;

import timber.log.Timber;

/**
 * Created by yan on 6/11/17.
 */

public abstract class Manager {
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
}
