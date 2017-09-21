package com.yan.sh.sh_android.engine.managers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.engine.objects.Objective;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by yan on 6/16/17.
 */

public class ObjectiveManager extends Manager {

    private ArrayList<Objective> userObjectives;
    private Context mContext;

    public ObjectiveManager(Context context){
        this.startup();
        userObjectives = new ArrayList<>();
        mContext = context;
    }

    public void loadObjectives(JSONArray objectives){
        if(userObjectives != null){
            userObjectives.clear();
        }

        for(int i = 0; i < objectives.length(); i++){
            try{
                Objective objective = new Objective(objectives.getJSONObject(i));
                if(userObjectives != null){
                    userObjectives.add(objective);
                }
            } catch (JSONException ex) {
                Timber.e(ex, "Error!");
            }
        }
    }

    public ArrayList<Objective> getObjectives(){
        return userObjectives;
    }

    public Objective getObjectiveById(String id){
        if(userObjectives == null){
            return null;
        }

        for(Objective objective : userObjectives){
            if(objective.getObjectiveId().equals(id)){
                return objective;
            }
        }

        return null;
    }

    public Objective getObjectiveByIndex(int index){
        if(userObjectives == null || index < 0 || index >= userObjectives.size()){
            return null;
        }

        return userObjectives.get(index);
    }

    public void completingObjective(String objectiveId, String url){
        Objective completedObjective = getObjectiveById(objectiveId);

        if(completedObjective == null){
            Timber.w("Null objective?");
            return;
        }

        completedObjective.setPictureUrl(url);
        completedObjective.setAsCompleted(System.currentTimeMillis()/1000);

        for(int i = 0; i < userObjectives.size() ; i++){
            if(userObjectives.get(i).getObjectiveId().equals(objectiveId)){
                userObjectives.remove(i);
                userObjectives.add(completedObjective);
                onObjectiveUpdate();
                break;
            }
        }

        Engine.socket().sendCompletedObjective(completedObjective);
    }

    public void updateObjectives(JSONArray objectives){
        for(int i = 0; i < objectives.length(); i++){
            try{
                JSONObject objective = objectives.getJSONObject(i);
                for(int j = 0; j < userObjectives.size() ; j++){
                    if(userObjectives.get(j).getObjectiveId().equals(objective.getString("id"))){
                        Objective complete = userObjectives.get(j);
                        complete.setAsCompleted(objective.getLong("time"));
                        complete.setPictureUrl(objective.getString("url").replace("\\", ""));
                        Timber.i("new url : " + complete.getPictureUrl());

                        userObjectives.remove(j);
                        userObjectives.add(complete);
                        break;
                    }
                }
            } catch (JSONException ex){
                Timber.e(ex, "JSON error");
            }
        }

        onObjectiveUpdate();
    }

    private void onObjectiveUpdate(){
        Intent objectiveChange = new Intent();
        objectiveChange.setAction("OBJECTIVE_CHANGE");

        //send local broadcast
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.sendBroadcast(objectiveChange);
    }
}
