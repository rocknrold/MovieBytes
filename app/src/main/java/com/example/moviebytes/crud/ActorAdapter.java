package com.example.moviebytes.crud;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebytes.R;
import com.example.moviebytes.models.Actor;

import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorHolder> {

    private final Context cont;
    private final List<Actor> actors;
    private OnActorClickListener actorListener;

    public interface OnActorClickListener {
        void OnActorItemClick(int position);
    }

    public void setOnActorClickListener(OnActorClickListener listener) {
        actorListener = listener;
    }

    public ActorAdapter(Context cont, List<Actor> actors) {
        this.cont = cont;
        this.actors = actors;
    }

    @NonNull
    @Override
    public ActorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cont).inflate(R.layout.rv_actor_item, parent,false);
        return new ActorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorHolder holder, int position) {
        Actor ac = actors.get(position);

        
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public class ActorHolder extends RecyclerView.ViewHolder {
        TextView tvActorName,tvActorNote;
        ImageView ivActorPoster;

        public ActorHolder(@NonNull View itemView) {
            super(itemView);

            tvActorName = itemView.findViewById(R.id.tvActorName);
            tvActorNote = itemView.findViewById(R.id.tvActorNote);
            ivActorPoster = itemView.findViewById(R.id.ivActorPoster);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(actorListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            actorListener.OnActorItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
