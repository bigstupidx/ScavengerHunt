package com.yan.sh.sh_android.engine.objects;

/**
 * Created by yan on 6/25/17.
 */

public class UnsyncedObjective {

    private int mId;
    private String mObjectiveId;
    private String mTimeStamp;
    private String mPictureUrl;
    private String mFileName;
    private String mGameId;
    private String mUserId;

    public UnsyncedObjective(int id, String objectiveId, String timeStamp, String pictureUrl, String fileName, String gameId, String userId){
        mId = id;
        mObjectiveId = objectiveId;
        mTimeStamp = timeStamp;
        mFileName = fileName;
        mPictureUrl = pictureUrl;
        mGameId = gameId;
        mUserId = userId;
    }

    public String toString(){
        return "{ " + mId + " " + mObjectiveId + " " + mTimeStamp + " " + mPictureUrl + " " + mGameId + " " + mUserId + " }";
    }

    public int getId() {
        return mId;
    }

    public String getObjectiveId() {
        return mObjectiveId;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getGameId() {
        return mGameId;
    }

    public String getUserId() {
        return mUserId;
    }

}
