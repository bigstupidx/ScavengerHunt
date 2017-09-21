package com.yan.sh.sh_android.ui.ObjectiveScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yan.sh.sh_android.R;
import com.yan.sh.sh_android.engine.objects.Objective;
import com.yan.sh.sh_android.ui.MapsActivity;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by yan on 7/1/17.
 */

public class ObjectiveAdapter extends RecyclerView.Adapter<ObjectiveAdapter.ObjectiveHolder> {

    private ArrayList<Objective> objectives;
    private Context mContext;

    public class ObjectiveHolder extends RecyclerView.ViewHolder {

        public TextView description;
        public TextView name;
        public ImageButton map;
        public ImageButton camera;

        public ObjectiveHolder(View v){
            super(v);
            description = (TextView) v.findViewById(R.id.title);
            name  = (TextView) v.findViewById(R.id.txtName);
            map = (ImageButton) v.findViewById(R.id.ib_map);
            camera = (ImageButton) v.findViewById(R.id.ib_camera);
        }
    }

    public ObjectiveAdapter(Context context, ArrayList<Objective> objectiveList){
        if(objectiveList != null){
            objectives = objectiveList;
        }

        mContext = context;
    }

    @Override
    public void onBindViewHolder(ObjectiveHolder holder, final int position) {
        final Objective objective = objectives.get(position);
        Timber.i("on bind");
        holder.name.setText(objective.getName());
        holder.description.setText(objective.getDescription());
        if(!objective.getCompleted()) {
            holder.map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MapsActivity.class);
                    intent.putExtra("id", objective.getObjectiveId());
                    mContext.startActivity(intent);
                }
            });
            holder.camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("id", objective.getObjectiveId());
                    ((Activity) mContext).startActivityForResult(intent, position);
                }
            });
        } else {
            holder.camera.setVisibility(View.GONE);
            holder.map.setImageResource(R.drawable.ic_check_black_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return objectives.size();
    }

    @Override
    public ObjectiveHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        Timber.i("on create");
        View objectiveView = inflater.inflate(R.layout.objective_card, parent, false);

        ObjectiveHolder objectiveHolder = new ObjectiveHolder(objectiveView);
        return objectiveHolder;
    }
}
