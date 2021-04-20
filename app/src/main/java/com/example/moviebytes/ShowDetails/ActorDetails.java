package com.example.moviebytes.ShowDetails;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviebytes.LoginPreference;
import com.example.moviebytes.R;
import com.example.moviebytes.models.Actor;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActorDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_details);

        Intent intent = getIntent();
        int actor_id = intent.getIntExtra("actor_id",0);

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = getString(R.string.BASE_URL)+"actor/show/".concat(String.valueOf(actor_id));
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        TextView tvDactorName = findViewById(R.id.tvDactorName);
                        TextView tvDactorNote = findViewById(R.id.tvDactorNote);
                        TextView tvDactorMovies = findViewById(R.id.tvDactorMovies);
                        ImageView ivDactorPoster = findViewById(R.id.ivDactorPoster);

                        try {
                            tvDactorName.setText(response.getString("name"));
                            tvDactorNote.setText(response.getString("note"));
                            Picasso.get().load(response.getString("poster")).fit().centerCrop().into(ivDactorPoster);

                            JSONArray movies = response.getJSONArray("films");
                            StringBuilder movieList = new StringBuilder();
                            movieList.append("Movies: \n");
                            for(int i =0; i < movies.length(); i++){
                                JSONObject actor = (JSONObject) movies.get(i);
                                movieList.append(actor.getString("name") + "\n");
                            }
                            tvDactorMovies.setText(movieList);

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
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(ActorDetails.this).getString("access_token", null));
                System.out.println(headers);
                return headers;
            }
        };

        queue.add(request);
    }
}