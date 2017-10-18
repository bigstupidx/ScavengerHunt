package com.yan.sh.sh_android.engine.managers;

import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.engine.EngineGlobal;
import com.yan.sh.sh_android.engine.objects.NetworkOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by yan on 6/11/17.
 * Handles requests to the server
 */

public class NetworkManager extends Manager {

    private OkHttpClient client;
    final private String domain = "https://glacial-garden-48114.herokuapp.com/";
    private ArrayList<NetworkOperations> mNetworkOps = new ArrayList<>();
    private ScheduledExecutorService mNetworkQueue = Executors.newSingleThreadScheduledExecutor();

    private Lock runQueueLock = new ReentrantLock();
    private boolean runQueueRunning = false;

    public NetworkManager(){
        this.startup();
        client = new OkHttpClient();
    }

    public void getGameData(String gameCode, Callback callback){
        Request request = new Request.Builder()
                                .url(domain + "api/game_info/?game_id=" + gameCode)
                                .build();

        makeRequest(request, callback);
    }

    public void getRankData(String userId, String gameId, Callback callback){
        Request request = new Request.Builder()
                                .url(domain + "api/player_rank/?game_id=" + gameId + "&&player_id=" + userId)
                                .build();

        if(!Engine.hardware().hasNetworkAccess()){
            NetworkOperations op = new NetworkOperations(EngineGlobal.RANK_REQUEST, request, callback, false);
            mNetworkOps.add(op);
            return;
        }

        makeRequest(request, callback);
    }

    public void makeRequest(Request request, Callback callback){
        client.newCall(request).enqueue(callback);
    }

    //Queue to re-try failed network requests
    public void runQueue(){
        runQueueLock.lock();
        if(runQueueRunning){
            runQueueLock.unlock();
            return;
        }

        runQueueRunning = true;
        runQueueLock.unlock();

        mNetworkQueue.execute(new Runnable() {
            @Override
            public void run() {
                if(mNetworkOps == null || mNetworkOps.isEmpty()){
                    return;
                }

                List<NetworkOperations> queuedOps = new ArrayList<NetworkOperations>();
                queuedOps.addAll(mNetworkOps);
                mNetworkOps.clear();

                for(NetworkOperations op : queuedOps){
                    if(op.shouldRetry()){
                        boolean success = op.runOp();
                        if(!success){
                            mNetworkOps.add(op);
                        }
                    }
                }
            }
        });
    }
}
