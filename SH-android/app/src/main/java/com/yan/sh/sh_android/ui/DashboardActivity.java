package com.yan.sh.sh_android.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.yan.sh.sh_android.R;
import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.engine.objects.Objective;
import com.yan.sh.sh_android.ui.ObjectiveScrollView.ObjectiveAdapter;

import com.yan.sh.sh_android.util.Utilities;

import timber.log.Timber;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ObjectiveAdapter objectiveAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = (RecyclerView) findViewById(R.id.rv_objective);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        objectiveAdapter = new ObjectiveAdapter(this, Engine.objective().getObjectives());
        recyclerView.setAdapter(objectiveAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(onObjectivesChange, new IntentFilter("OBJECTIVE_CHANGE"));

        calculateUI();
    }

    BroadcastReceiver onObjectivesChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.i("should update");
            objectiveAdapter.notifyDataSetChanged();
            calculateUI();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_map:
                Intent intentMap = new Intent(this, MapsActivity.class);
                startActivity(intentMap);
                break;
            case R.id.item_stats:
                Intent intentStats = new Intent(this, StatsActivity.class);
                startActivity(intentStats);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    private void calculateUI(){
        //TODO: add time calculation
        if(Engine.objective().getObjectives() == null){
            return;
        }
        //uncompleted objectives calculation
        int uncompletedCount = 0;
        for(Objective objective : Engine.objective().getObjectives()){
            if(!objective.getCompleted()){
                uncompletedCount++;
            }
        }

        TextView objectiveCount = (TextView) findViewById(R.id.tv_objectives_remaining);
        objectiveCount.setText("Objectives Remaining: " + uncompletedCount);
    }

    @Override
    protected void onPause() {
        //Engine.socket().closeSocket();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            return;
        }
        Timber.i("request Code " + requestCode);

        Objective captured = Engine.objective().getObjectiveByIndex(requestCode);
        if(captured == null || !data.hasExtra("data")){
            Timber.w("Activity result captured is not an objective or no image???");
            return;
        }

        Bitmap image = (Bitmap) data.getExtras().get("data");
        String fileName = Engine.user().getUuid()+"_"+captured.getObjectiveId();
        String url = Utilities.saveBitmap(image, this, fileName+".jpg");
        Engine.cloud().uploadPicture(url, fileName, captured.getObjectiveId());
    }

    @Override
    public void onBackPressed() {
        Timber.i("on back pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        builder.setMessage("This exits your game! Are you sure you want to proceed?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DashboardActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Back", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onObjectivesChange);
        super.onDestroy();
    }
}
