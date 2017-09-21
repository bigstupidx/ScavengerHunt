package com.yan.sh.sh_android.engine.managers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.yan.sh.sh_android.engine.objects.UnsyncedObjective;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by yan on 6/11/17.
 * Handles data storage and persistance
 */

public class DataManager extends Manager{

    //Store current game data incase user becomes offline and data not synced
    public static final class GameData implements BaseColumns{
        public static final String TABLE_NAME = "gameData";
        public static final String COLUMN_OBJECTIVE_ID = "objectiveId";
        public static final String COLUMN_OBJECTIVE_COMPLETED_TIMESTAMP = "completedTime";
        public static final String COLUMN_OBJECTIVE_PICTURE_URL = "objectiveUrl";
        public static final String COLUMN_GAME_ID = "gameId";
        public static final String COLUMN_USER_ID = "userId";
        public static final String COLUMN_SYNCED = "dataSynced";
    }

    public static class GameDataDBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "gamedata.db";
        private static final int DATABASE_VERSION = 1;

        public GameDataDBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Timber.i("Creating DataManager Table");
            final String SQL_CREATE_GAMEDATA_TABLE = "CREATE TABLE " + GameData.TABLE_NAME + " (" +
                    GameData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    GameData.COLUMN_OBJECTIVE_ID + " INTEGER NOT NULL, " +
                    GameData.COLUMN_OBJECTIVE_COMPLETED_TIMESTAMP + " TEXT NOT NULL, " +
                    GameData.COLUMN_OBJECTIVE_PICTURE_URL + " TEXT, " +
                    GameData.COLUMN_GAME_ID + " INTEGER NOT NULL, " +
                    GameData.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                    GameData.COLUMN_SYNCED + " BOOLEAN NOT NULL " +
                    "); ";

            db.execSQL(SQL_CREATE_GAMEDATA_TABLE);
        }

        public void dropTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + GameData.TABLE_NAME);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTable(db);
            onCreate(db);
        }
    }

    private Context mContext;
    private SQLiteDatabase mDbWrite;
    private SQLiteDatabase mDbRead;

    public DataManager(Context context){
        mContext = context;
        GameDataDBHelper gameDataDBHelper = new GameDataDBHelper(mContext);
        mDbWrite = gameDataDBHelper.getWritableDatabase();
        mDbRead = gameDataDBHelper.getReadableDatabase();
        this.startup();
    }

    public long recordObjective(int objectiveId, String objectiveTimeStamp, String pictureUrl, int gameId, int userId){
        ContentValues values = new ContentValues();

        values.put(GameData.COLUMN_OBJECTIVE_ID, objectiveId);
        values.put(GameData.COLUMN_OBJECTIVE_COMPLETED_TIMESTAMP, objectiveTimeStamp);
        values.put(GameData.COLUMN_OBJECTIVE_PICTURE_URL, pictureUrl);
        values.put(GameData.COLUMN_GAME_ID, gameId);
        values.put(GameData.COLUMN_USER_ID, userId);
        values.put(GameData.COLUMN_SYNCED, false);

        long newRowId = mDbWrite.insert(GameData.TABLE_NAME, null, values);
        Timber.i(Long.toString(newRowId));
        return newRowId;
    }

    public List<UnsyncedObjective> getUnsyncedObjectives() {
        String[] projection = {
                GameData._ID,
                GameData.COLUMN_OBJECTIVE_ID,
                GameData.COLUMN_OBJECTIVE_COMPLETED_TIMESTAMP,
                GameData.COLUMN_OBJECTIVE_PICTURE_URL,
                GameData.COLUMN_GAME_ID,
                GameData.COLUMN_USER_ID
        };

        String selection = GameData.COLUMN_SYNCED + " = ?";
        String[] selectionArgs = {"false"};

        String sortOrder = GameData.COLUMN_OBJECTIVE_COMPLETED_TIMESTAMP + " ASC";

        Cursor cursor = mDbRead.query(
                GameData.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        List objectives = new ArrayList<UnsyncedObjective>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(GameData._ID));
            int objectiveId = cursor.getInt(cursor.getColumnIndex(GameData.COLUMN_OBJECTIVE_ID));
            String timeStamp = cursor.getString(cursor.getColumnIndex(GameData.COLUMN_OBJECTIVE_COMPLETED_TIMESTAMP));
            String pictureUrl = cursor.getString(cursor.getColumnIndex(GameData.COLUMN_OBJECTIVE_PICTURE_URL));
            int gameId = cursor.getInt(cursor.getColumnIndex(GameData.COLUMN_GAME_ID));
            int userId = cursor.getInt(cursor.getColumnIndex(GameData.COLUMN_USER_ID));

            UnsyncedObjective newObj = new UnsyncedObjective(id, objectiveId, timeStamp, pictureUrl, gameId, userId);
            objectives.add(newObj);
        }
        return objectives;
    }

    public void deleteSynced(int columnId){
        String selection = GameData._ID + " = ?";
        String[] selectionArg = {Integer.toString(columnId)};
        mDbWrite.delete(GameData.TABLE_NAME,selection,selectionArg);
    }

    //shared preference calls
    public void saveUserData(String id, String name, String gameKey){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.putString("id", id);
        editor.putString("name", name);
        editor.putString("gameKey", gameKey);
        editor.commit();
    }

    public void saveGameData(String id){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("game", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.putString("id", id);
        editor.commit();
    }

    public String getUserId(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sharedPreferences.getString("id", "");
    }

    public String getUserName(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    public String getUserGameKey(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sharedPreferences.getString("gameKey", "");
    }

    public String getGameData(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("game", Context.MODE_PRIVATE);
        return sharedPreferences.getString("id", "");
    }
}
