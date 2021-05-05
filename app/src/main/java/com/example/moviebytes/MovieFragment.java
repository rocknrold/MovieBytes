package com.example.moviebytes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviebytes.ShowDetails.MovieDetails;
import com.example.moviebytes.crud.MovieAdapter;
import com.example.moviebytes.models.Actor;
import com.example.moviebytes.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment implements MovieAdapter.OnMovieClickListener {

    private final String getAllMovies = "http://192.168.0.26:8000/api/film/all";
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView rvMovies;
    private MovieAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_movie, container, false);

        rvMovies = view.findViewById(R.id.rvMovies);

        getAllMovies(view.getContext());

        return view;
    }

    private void getAllMovies(Context cont) {
        RequestQueue queue = Volley.newRequestQueue(cont);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getAllMovies, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i < response.length(); i++) {
                    try {
                        JSONObject movie = (JSONObject) response.get(i);
                        movieList.add(new Movie(
                                            movie.getInt("id"),
                                            movie.getString("name"),
                                            movie.getString("story"),
                                            movie.getString("released_at"),
                                            movie.getInt("duration"),
                                            movie.getString("info"),
                                            movie.getInt("genre_id"),
                                            movie.getInt("certificate_id"),
                                            movie.getString("poster")
                                ));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter = new MovieAdapter(cont, movieList);
                rvMovies.setAdapter(adapter);
                rvMovies.setLayoutManager(new LinearLayoutManager(cont));
                adapter.setOnMovieClickListener(MovieFragment.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(cont).getString("access_token", null));
                System.out.println(headers);
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public void OnMovieItemClick(int position) {
        Intent intent = new Intent(getActivity(), MovieDetails.class);
        Movie movie = movieList.get(position);
        intent.putExtra("movie_id", movie.getMovie_id());
        startActivity(intent);
    }

    @Override
    public void OnMovieItemLongClick(int position) {
        Movie mo = movieList.get(position);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("DELETE")
                .setMessage("Do you want to delete this movie?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        String url = getString(R.string.BASE_URL)+"film/".concat(String.valueOf(mo.getMovie_id()));
                        StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
                            try {
                                JSONObject movieDelete = new JSONObject(response);
                                if(movieDelete.getInt("status") == 200){
                                    movieList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    Toast.makeText(getContext(), movieDelete.getString("success"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            error.printStackTrace();
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(getActivity()).getString("access_token", null));
                                System.out.println(headers);
                                return headers;
                            }
                        };
                        queue.add(request);
                    }
                })
                .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                .create();

        dialog.show();
    }
}