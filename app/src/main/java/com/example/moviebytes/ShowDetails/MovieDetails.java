package com.example.moviebytes.ShowDetails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviebytes.LoginPreference;
import com.example.moviebytes.R;
import com.example.moviebytes.crud.MovieActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MovieDetails extends AppCompatActivity {

    private final String filmUrl = "http://192.168.0.26:8000/api/film/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        int movieId = intent.getIntExtra("movie_id",0);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                filmUrl.concat(String.valueOf(movieId)),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ImageView ivPoster = findViewById(R.id.ivDmoviePoster);
                        TextView tvTitle = findViewById(R.id.tvDtitle);
                        TextView tvInfo = findViewById(R.id.tvDinfo);
                        TextView tvStory = findViewById(R.id.tvDstory);
                        TextView tvActors = findViewById(R.id.tvDactors);
                        try {
                            tvTitle.setText(response.getString("name"));
                            tvInfo.setText(response.getString("info"));
                            tvStory.setText(response.getString("story"));

                            JSONArray actors = response.getJSONArray("actors");
                            StringBuilder actorList = new StringBuilder();
                            actorList.append("Actors: \n");
                            for(int i =0; i < actors.length(); i++){
                                JSONObject actor = (JSONObject) actors.get(i);
                                actorList.append(actor.getString("name") + "\n");
                            }
                            tvActors.setText(actorList);

                            String photo_url = getString(R.string.PUBLIC_URL);
                            if(response.getString("poster").contains("storage/images/")){
                                Picasso.get().load(photo_url.concat(response.getString("poster"))).fit().centerInside().into(ivPoster);
                            }else {
                                Picasso.get().load(response.getString("poster")).fit().centerInside().into(ivPoster);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(MovieDetails.this).getString("access_token", null));
                System.out.println(headers);
                return headers;
            }
        };

        queue.add(request);

        Button btnEdit = findViewById(R.id.btnEmovie);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetails.this, MovieActivity.class);
                intent.putExtra("MOVIE_ID", movieId);
                startActivity(intent);
            }
        });

    }
}