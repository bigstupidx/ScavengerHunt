package com.yan.sh.sh_android.engine.objects;

/**
 * Created by yan on 6/25/17.
 */

public class UnsyncedObjective {

    private int mId;
    private int mObjectiveId;
    private String mTimeStamp;
    private String mPictureUrl;
    private int mGameId;
    private int mUserId;

    public UnsyncedObjective(int id, int objectiveId, String timeStamp, String pictureUrl, int gameId, int userId){
        mId = id;
        mObjectiveId = objectiveId;
        mTimeStamp = timeStamp;
        mPictureUrl = pictureUrl;
        mGameId = gameId;
        mUserId = userId;
    }

    public String toString(){
        return "{ " + mId + " " + mObjectiveId + " " + mTimeStamp + " " + mPictureUrl + " " + mGameId + " " + mUserId + " }";
    }

    public int id(){
        return mId;
    }
}
