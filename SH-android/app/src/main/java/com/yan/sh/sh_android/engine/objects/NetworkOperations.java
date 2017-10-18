package com.yan.sh.sh_android.engine.objects;

import com.yan.sh.sh_android.engine.Engine;

import okhttp3.Callback;
import okhttp3.Request;

/**
 * Created by yan on 10/13/17.
 */

public class NetworkOperations {

    private String mType;
    private Request mRequest;
    private Callback mCallback;
    private boolean mImportant;

    private static final double MILLIS_TO_LIVE = 5000;
    private double mInitTime;


    public NetworkOperations(final String type, final Request request, final Callback callback, final boolean important){
        mType = type;
        mRequest = request;
        mCallback = callback;
        mImportant = important;
        mInitTime = System.currentTimeMillis();
    }

    public boolean runOp(){
        if(!Engine.hardware().hasNetworkAccess()){
            return false;
        }

        Engine.network().makeRequest(mRequest, mCallback);
        return true;
    }

    public boolean isImportant(){
        return mImportant;
    }

    public boolean shouldRetry(){
        return System.currentTimeMillis() - mInitTime > MILLIS_TO_LIVE;
    }
}
