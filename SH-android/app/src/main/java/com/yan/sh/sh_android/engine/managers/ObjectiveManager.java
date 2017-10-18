package com.yan.sh.sh_android.engine.managers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.engine.EngineGlobal;
import com.yan.sh.sh_android.engine.objects.Objective;
import com.yan.sh.sh_android.engine.objects.UnsyncedObjective;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    //Generate objectives based on a JSON response from server
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

    //Mark an objective as complete
    public void completingObjective(String objectiveId, String url){
        Objective completedObjective = getObjectiveById(objectiveId);

        if(completedObjective == null){
            Timber.w("Null objective?");
            return;
        }

        if(url != null){
            completedObjective.setPictureUrl(url);
        }
        completedObjective.setAsCompleted((long)Engine.time().now());

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

    //Update objectives with JSON response from the server
    public void updateObjectives(JSONObject objectives){
        if(objectives == null){
            return;
        }

        try{
            for(int i = 0; i < objectives.names().length(); i++){
                String key = objectives.names().getString(i);
                JSONObject objective = objectives.getJSONObject(key);
                for(Objective userObjective : userObjectives){
                    if(userObjective.getObjectiveId().equals(key)){
                        userObjective.setAsCompleted(objective.getLong("time"));
                        userObjective.setPictureUrl(objective.getString("url").replace("\\",""));
                        Timber.i("objective updated!");
                    }
                }
            }
        }catch(JSONException e) {
            Timber.e("Update Obj Err:" + e.toString());
        }

        onObjectiveUpdate();
    }

    //Send a local broadcast, indicating objectives were updated
    private void onObjectiveUpdate(){
        Intent objectiveChange = new Intent();
        objectiveChange.setAction(EngineGlobal.LOCAL_OBJECTIVE_CHANGE);

        //send local broadcast
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.sendBroadcast(objectiveChange);
    }

    //Sync unsynced objectives saved in the SQLite database
    private void processUnsyncedObjectives(){
        List<UnsyncedObjective> unsyncedObjectives = Engine.data().getUnsyncedObjectives();
        if(unsyncedObjectives == null || unsyncedObjectives.isEmpty()){
            return;
        }

        for(UnsyncedObjective obj : unsyncedObjectives){
            //ensure that we only sync objectives if the same user has returned to the same game
            if(obj.getGameId().equals(Engine.game().getGameCode()) && obj.getUserId().equals(Engine.user().getUuid())){
                Engine.cloud().uploadPicture(obj.getPictureUrl(), obj.getFileName(), obj.getObjectiveId());
            }
        }
    }
}
