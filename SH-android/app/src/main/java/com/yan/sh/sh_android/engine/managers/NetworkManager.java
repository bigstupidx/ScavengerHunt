package com.yan.sh.sh_android.engine.managers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    public NetworkManager(){
        this.startup();
        client = new OkHttpClient();
    }

    public void getGameData(String gameCode, Callback callback){
        Request request = new Request.Builder()
                                .url(domain + "api/game_info/?game_id=" + gameCode)
                                .build();

        client.newCall(request).enqueue(callback);
    }

    public void getRankData(String userId, String gameId, Callback callback){
        Request requst = new Request.Builder()
                                .url(domain + "api/player_rank/?game_id=" + gameId + "&&player_id=" + userId)
                                .build();

        client.newCall(requst).enqueue(callback);
    }
}
