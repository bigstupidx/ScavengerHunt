package com.yan.sh.sh_android.engine.managers;

import com.yan.sh.sh_android.engine.Engine;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by yan on 6/11/17.
 */

public class UserManager extends Manager {
    private boolean connectedToGame;
    private boolean userInitialized;

    private String nickName;
    private String uuid;
    private String gameKey;

    public UserManager(){
        this.startup();
        connectedToGame = false;
        userInitialized = false;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public void setGameKey(String gameKey){
        this.gameKey = gameKey;

        //GAME KEY is the last thing set, so if this is successful save all the data
        Engine.data().saveUserData(uuid, nickName, gameKey);
    }

    public void setNickName(String nickName){
        this.nickName = nickName;
    }

    public void loadUserManager(){
        this.uuid = Engine.data().getUserId();
        this.gameKey = Engine.data().getUserGameKey();
        this.nickName = Engine.data().getUserName();
    }

    public String getUuid(){
        return uuid;
    }


}
