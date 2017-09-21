package com.yan.sh.sh_android.ui.ObjectiveScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yan.sh.sh_android.R;
import com.yan.sh.sh_android.engine.objects.Objective;
import com.yan.sh.sh_android.ui.MapsActivity;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by yan on 7/1/17.
 */

public class CompletedObjectiveAdapter extends RecyclerView.Adapter<CompletedObjectiveAdapter.ObjectiveHolder> {

    private ArrayList<Objective> objectives;
    private Context mContext;

    public class ObjectiveHolder extends RecyclerView.ViewHolder {

        public TextView description;
        public TextView name;
        public ImageView image;


        public ObjectiveHolder(View v){
            super(v);
            description = (TextView) v.findViewById(R.id.title);
            name  = (TextView) v.findViewById(R.id.txtName);
            image = (ImageView) v.findViewById(R.id.iv_objective);

        }
    }

    public CompletedObjectiveAdapter(Context context, ArrayList<Objective> objectiveList){
        objectives = new ArrayList<>();
        if(objectiveList != null){
            for(Objective objective : objectiveList){
                if(objective.getCompleted()){
                    objectives.add(objective);
                }
            }
        }

        mContext = context;
    }

    @Override
    public void onBindViewHolder(ObjectiveHolder holder, final int position) {
        final Objective objective = objectives.get(position);
        Timber.i("on bind");
        holder.name.setText(objective.getName());
        holder.description.setText(objective.getDescription());
        //TODO : https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
        //holder.image.setImageURI(Uri.parse(objective.getPictureUrl()));
    }

    @Override
    public int getItemCount() {
        return objectives.size();
    }

    @Override
    public ObjectiveHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        Timber.i("on create");
        View objectiveView = inflater.inflate(R.layout.completed_objective_card, parent, false);

        ObjectiveHolder objectiveHolder = new ObjectiveHolder(objectiveView);
        return objectiveHolder;
    }
}
