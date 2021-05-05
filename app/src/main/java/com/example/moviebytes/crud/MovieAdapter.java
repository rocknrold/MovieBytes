package com.example.moviebytes.crud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviebytes.R;
import com.example.moviebytes.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private final Context cont;
    private final List<Movie> movies;
    private OnMovieClickListener movieListener;

    public interface OnMovieClickListener {
        void OnMovieItemClick(int position);
        void OnMovieItemLongClick(int position);
    }

    public void setOnMovieClickListener(OnMovieClickListener listener) {
        movieListener = listener;
    }

    public MovieAdapter(Context cont, List<Movie> movies) {
        this.cont = cont;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cont).inflate(R.layout.rv_movie_item, parent,false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Movie mv = movies.get(position);
        System.out.println();

        if(mv.getPoster().equals("null")){
            Picasso.get().load(R.drawable.no_cover).into(holder.ivPoster);
        } else {
            if(mv.getPoster().contains("storage/images/")){
                Picasso.get().load(holder.photo_path.concat(mv.getPoster())).into(holder.ivPoster);
            } else {
                Picasso.get().load(mv.getPoster()).into(holder.ivPoster);
            }
        }

        holder.tvTitle.setText(mv.getName());
        holder.tvInfo.setText(mv.getInfo());
        
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvInfo;
        String photo_path;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivMoviePoster);
            tvTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvInfo = itemView.findViewById(R.id.tvMovieInfo);
            photo_path = itemView.getContext().getString(R.string.PUBLIC_URL);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(movieListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            movieListener.OnMovieItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(movieListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            movieListener.OnMovieItemLongClick(position);
                        }
                    }
                    return true;
                }
            });
        }
    }
}
