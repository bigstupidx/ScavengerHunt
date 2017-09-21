package com.yan.sh.sh_android.engine.objects;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by yan on 6/20/17.
 */

public class Objective {

    private String objectiveId;
    private String name;
    private String description;

    private Double lat;
    private Double lon;

    private Boolean completed = false;
    private Long completedTime;
    private String pictureUrl;

    public Objective(JSONObject object){
        if (object == null){
            return;
        }

        try{
            objectiveId = object.getString("id");
            name = object.getString("name");
            description = object.getString("description");

            lat = object.getJSONObject("location").getDouble("lat");
            lon = object.getJSONObject("location").getDouble("lng");

            Timber.i(name);
        }catch (JSONException err){
            Timber.e(err, "JSON error!");
        }
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getObjectiveId(){
        return objectiveId;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }

    public Boolean getCompleted(){
        return completed;
    }

    public void setPictureUrl(String pictureUrl){
        this.pictureUrl = pictureUrl;
    }

    public void setAsCompleted(Long completionTime){
        this.completed = true;
        this.completedTime = completionTime;
    }

    public Long getCompletedTime(){
        return completedTime;
    }

    public String getPictureUrl(){
        return pictureUrl;
    }
}
