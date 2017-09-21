package com.yan.sh.sh_android.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yan.sh.sh_android.R;
import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.services.ScavengerHuntService;
import com.yan.sh.sh_android.util.TimberTree;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private EditText gameCode;
    private Button joinGame;
    private Button returnToLastGame;
    private ImageView wifi;
    private ImageView location;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("LoginActivity", "onCreate");

        Timber.plant(new TimberTree());

        Intent serviceIntent = new Intent(this, ScavengerHuntService.class);
        startService(serviceIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(onNetworkChange, new IntentFilter("NETWORK_CHANGE"));
        LocalBroadcastManager.getInstance(this).registerReceiver(onLocationChange, new IntentFilter("LOCATION_CHANGE"));

        gameCode = (EditText) findViewById(R.id.et_game_code);
        joinGame = (Button) findViewById(R.id.bt_join_game);
        returnToLastGame = (Button) findViewById(R.id.bt_join_last_game);
        wifi = (ImageView) findViewById(R.id.iv_wifi);
        location = (ImageView) findViewById(R.id.iv_location);
        error = (TextView) findViewById(R.id.tv_error_msg);

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onJoinGameClick();
            }
        });

        returnToLastGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onJoinLastGameClick();
            }
        });

        checkEngineInitialization();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            refreshLoginUI();
        } catch (Exception ex) {
            Timber.e("Engine not initialized");
        }

    }

    BroadcastReceiver onNetworkChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshLoginUI();
        }
    };

    BroadcastReceiver onLocationChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshLoginUI();
        }
    };

    private void checkEngineInitialization(){
        Log.i("LoginActivity", "check init");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(Engine.managersInitialized()){
                    refreshLoginUI();
                } else {
                    checkEngineInitialization();
                }
            }
        });
    }

    private void refreshLoginUI(){
        if(!Engine.hardware().hasNetworkAccess()) {
            wifi.setVisibility(View.INVISIBLE);
        } else {
            wifi.setVisibility(View.VISIBLE);
        }

        if(!Engine.hardware().isLocationEnabled()) {
            location.setVisibility(View.INVISIBLE);
        } else {
            location.setVisibility(View.VISIBLE);
        }

        if(!Engine.hardware().isLocationEnabled() || !Engine.hardware().hasNetworkAccess()){
            joinGame.setEnabled(false);
            error.setVisibility(View.VISIBLE);
            returnToLastGame.setEnabled(false);
        } else {
            joinGame.setEnabled(true);
            error.setVisibility(View.GONE);
            if(!Engine.data().getGameData().equals("")) {
                returnToLastGame.setEnabled(true);
            }
        }
    }

    public void onJoinGameClick(){
        onJoinGame(gameCode.getText().toString());
    }

    public void onJoinLastGameClick(){
        onJoinGame(Engine.data().getGameData());
    }

    public void onJoinGame(final String gameCode){
        Engine.network().getGameData(gameCode, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Timber.e("Error getting game data!");
                onInitializationError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try{
                    //do some validation, make sure a valid game data gets sent
                    JSONObject gameData = new JSONObject(response.body().string());

                    if(gameData.has("status") && gameData.getInt("status") == 200 && gameData.has("data")){
                        Engine.game().storeGameData(gameData, gameCode);
                        Engine.socket().openSocket();
                        //if returning user
                        if(Engine.data().getUserGameKey().equals(gameCode)){
                            drawChangeUserDialog();
                        } else {
                            drawAlertDialog();
                        }

                    } else {
                        onInitializationError();
                    }
                } catch (JSONException ex){
                    Timber.e(ex, "Json error!");
                    onInitializationError();
                }
            }
        });
    }

    private void onInitializationError(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Timber.i("on error");
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Game does not exist!");
                builder.create().show();
            }
        });

    }

    private void onInitializationSuccess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void drawChangeUserDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Returning as " + Engine.data().getUserName() + "?");
                builder.setMessage("Press no to join as new user.");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Engine.socket().sendReturningPlayerMessage();
                        Engine.user().loadUserManager();
                        onInitializationSuccess();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawAlertDialog();
                    }
                });
                builder.show();
            }
        });
    }

    private void drawAlertDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Create User Profile");

                final EditText input = new EditText(LoginActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setGravity(Gravity.CENTER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userName = input.getText().toString();
                        Engine.user().setNickName(userName);
                        Engine.socket().sendNewPlayerMessage(userName);
                        onInitializationSuccess();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onLocationChange);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNetworkChange);
        super.onDestroy();
    }
}
