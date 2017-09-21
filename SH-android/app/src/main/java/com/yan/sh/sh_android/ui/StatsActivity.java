package com.yan.sh.sh_android.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.yan.sh.sh_android.R;
import com.yan.sh.sh_android.engine.Engine;
import com.yan.sh.sh_android.ui.ObjectiveScrollView.CompletedObjectiveAdapter;

import timber.log.Timber;

public class StatsActivity extends AppCompatActivity {

    private TextView rank;
    private RecyclerView recyclerView;
    private CompletedObjectiveAdapter objectiveAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        rank = (TextView) findViewById(R.id.tv_ranking);
        //TODO: getActionBar().setTitle("My Stats");

        LocalBroadcastManager.getInstance(this).registerReceiver(onRankUpdate, new IntentFilter("RANK_CHANGE"));

        //TODO: cache rank

        recyclerView = (RecyclerView) findViewById(R.id.rv_objective_completed);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        objectiveAdapter = new CompletedObjectiveAdapter(this, Engine.objective().getObjectives());
        recyclerView.setAdapter(objectiveAdapter);
    }

    @Override
    protected void onResume() {
        getRank();
        super.onResume();
    }

    private void getRank(){
        Engine.socket().openSocket();
        Engine.socket().sendSocketMessage("rank", null);
    }

    BroadcastReceiver onRankUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            rank.setText(Engine.game().createRankText());
        }
    };

    @Override
    protected void onPause() {
        //Engine.socket().closeSocket();
        super.onPause();
    }


}
