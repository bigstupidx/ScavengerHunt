package com.yan.sh.sh_android.engine.managers;

import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by yan on 6/11/17.
 */

public class GameManager extends Manager {

    private boolean initalized = false;
    private String startTime;
    private String gameCode;
    private Integer rank;
    private Integer numPlayers;

    public GameManager(){
        this.startup();
    }

    public void storeGameData(JSONObject data, String newGameCode){
        if(data == null || newGameCode == null){
            return;
        }

        gameCode = newGameCode;
        try{
            JSONArray objectives = data.getJSONArray("data");
            Engine.objective().loadObjectives(objectives);
            Engine.data().saveGameData(gameCode);
            initalized = true;
        }catch(JSONException ex){
            Timber.e(ex, "Json error");
        }
    }

    public boolean initialized(){
        return initalized;
    }

    public String getGameCode(){
        return gameCode;
    }

    public Boolean setRank(Integer rank, Integer players){
        if(rank != null && players !=null && rank <= players){
            this.rank = rank;
            this.numPlayers = players;
            return true;
        }
        return false;
    }

    public String createRankText(){
        if(rank == null || numPlayers == null){
            return "";
        }

        return Integer.toString(rank) + " / " + Integer.toString(numPlayers);
    }

}
