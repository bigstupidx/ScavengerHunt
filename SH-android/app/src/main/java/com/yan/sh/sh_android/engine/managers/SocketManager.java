package com.yan.sh.sh_android.engine.managers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.engine.objects.Objective;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.socket.emitter.Emitter;
import timber.log.Timber;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by yan on 6/11/17.
 * Handles socket request to the server
 */

public class SocketManager extends Manager {

    final private String domain = "https://glacial-garden-48114.herokuapp.com/";

    ScheduledExecutorService serialQueue;
    private Socket socketIO;
    private Context mContext;

    public SocketManager(Context context){
        this.startup();
        serialQueue = Executors.newSingleThreadScheduledExecutor();
        mContext = context;

        try{
            socketIO = IO.socket(domain);
        } catch (URISyntaxException ex) {
            Timber.e(ex, "Error initalizing socket");
        }
    }

    //send completed objective to
    public void sendSocketMessage(final String message, final JSONObject arguments){
        if(socketIO == null || !socketIO.connected()){
            closeSocket();
            Timber.i("open socket");
            openSocket();
        }
        if(arguments == null){
            socketIO.emit(message);
        } else {
            socketIO.emit(message, arguments);
        }
       ;
    }

    public void sendNewPlayerMessage(final String userName){
        try{
            JSONObject data = new JSONObject();
            data.put("type", "player");
            data.put("name", userName);
            JSONObject json = new JSONObject();
            json.put("data", data);
            sendSocketMessage("connection", json);
        } catch (JSONException ex) {
            Timber.e("Error!");
        }
    }

    public void sendReturningPlayerMessage(){
        try {
            JSONObject data = new JSONObject();
            data.put("type", "player");
            data.put("playerId", Engine.data().getUserId());
            JSONObject json = new JSONObject();
            json.put("data", data);
            Engine.socket().sendSocketMessage("connection", json);
        } catch (JSONException ex) {
            Timber.e(ex, "JSON error");
        }

    }

    public void sendCompletedObjective(Objective objective){
        try{
            JSONObject data = new JSONObject();
            data.put("objectiveId", objective.getObjectiveId());
            data.put("time", objective.getCompletedTime());
            data.put("url", objective.getPictureUrl());
            JSONObject json = new JSONObject();
            json.put("data", data);
            sendSocketMessage("objective", json);
        } catch (JSONException ex){
            Timber.e(ex, "JSON error!");
        }
    }

    public void openSocket(){
        if(socketIO == null){
            try {
                socketIO = IO.socket(domain);
            } catch (URISyntaxException ex) {
                Timber.e(ex, "URI error");
            }
        }

        if(socketIO.connected()){
            return;
        }

        socketIO.connect();
        try {
            JSONObject data = new JSONObject();
            data.put("type", "player");
            data.put("playerId", Engine.data().getUserId());
            JSONObject json = new JSONObject();
            json.put("data", data);
            socketIO.emit("connection", json);
        } catch (JSONException ex) {
            Timber.e(ex, "JSON error");
        }

        socketIO.on(Socket.EVENT_CONNECT, new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                Timber.i("Connected");
            }
        }).on("connection", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                Timber.i("on connection");
                try {
                    JSONObject json = (JSONObject) args[0];
                    if(json != null && json.has("data") && json.getJSONObject("data").has("id")){
                        Engine.user().setUuid(json.getJSONObject("data").getString("id"));
                        Engine.user().setGameKey(Engine.game().getGameCode());
                    } else if (json != null && json.has("data") && json.getJSONObject("data").has("objectives complete")) {
                        Timber.i(json.toString());
                        Engine.objective().updateObjectives(json.getJSONObject("data").getJSONArray("objectives complete"));
                    }
                } catch (JSONException ex){
                    Timber.e(ex, "JSON exception");
                }
            }
        }).on("rank", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                Timber.i("on rank");
                try {
                    JSONObject json = (JSONObject) args[0];
                    Integer rank = json.getJSONObject("data").getInt("rank");
                    Integer players = json.getJSONObject("data").getInt("num players");
                    if(Engine.game().setRank(rank, players)){
                        onRankUpdate();
                    }
                } catch (JSONException ex){
                    Timber.e(ex, "JSON error!");
                }
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Timber.i("disconnecting!");
            }
        });
    }

    public void closeSocket(){
        if(socketIO == null || !socketIO.connected()){
            return;
        }

        socketIO.off();
        socketIO.disconnect();
        socketIO.close();
        socketIO = null;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        closeSocket();
    }

    private void onRankUpdate(){
        Intent rankUpdate = new Intent();
        rankUpdate.setAction("RANK_CHANGE");

        //send local broadcast here
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.sendBroadcast(rankUpdate);
    }
}
