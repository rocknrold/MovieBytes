package com.example.moviebytes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviebytes.ShowDetails.ActorDetails;
import com.example.moviebytes.crud.ActorAdapter;
import com.example.moviebytes.crud.MovieAdapter;
import com.example.moviebytes.crud.ProducerActivity;
import com.example.moviebytes.models.Actor;
import com.example.moviebytes.models.Movie;
import com.example.moviebytes.models.Producer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorFragment extends Fragment implements ActorAdapter.OnActorClickListener {

    private static final int ACTOR_DETAILS_REQUEST_CODE = 2;
    private List<Actor> actorList = new ArrayList<>();
    private RecyclerView rvActors;
    private ActorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_actor,container,false);

        rvActors = (RecyclerView) view.findViewById(R.id.rvActors);

        getAllActors(view.getContext());

        return view;
    }

    private void getAllActors(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.BASE_URL)+"actor/all";
        System.out.println(url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response.toString());
                for(int i=0; i < response.length(); i++) {
                    try {
                        JSONObject actor = (JSONObject) response.get(i);
                        actorList.add(new Actor(
                                actor.getInt("id"),
                                actor.getString("name"),
                                actor.getString("note"),
                                actor.getString("poster")
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter = new ActorAdapter(context, actorList);
                rvActors.setAdapter(adapter);
                rvActors.setLayoutManager(new LinearLayoutManager(context));
                adapter.setOnActorClickListener(ActorFragment.this);
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
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(context).getString("access_token", null));
                System.out.println(headers);
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public void OnActorItemClick(int position) {
        Intent intent = new Intent(getActivity(), ActorDetails.class);
        Actor actor = actorList.get(position);
        intent.putExtra("actor_id", actor.getActorId());
        startActivity(intent);
//        startActivityForResult(intent, ACTOR_DETAILS_REQUEST_CODE);
//        Dialog box or intent its okay both are working!!!
//        ActorDetailsDialog(actor.getActorId());
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == ACTOR_DETAILS_REQUEST_CODE){
//            if(resultCode == getActivity().RESULT_OK) {
//                System.out.println(data.getExtras().toString());
//                Toast.makeText(getContext(), data.getStringExtra("name"), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    @Override
    public void OnActorLongClick(int position) {
        Actor ac = actorList.get(position);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("DELETE")
                .setMessage("Do you want to delete this actor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        String url = getString(R.string.BASE_URL)+"actor/".concat(String.valueOf(ac.getActorId()));
                        StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
                            try {
                                JSONObject prodDelete = new JSONObject(response);
                                if(prodDelete.getInt("status") == 200){
                                    actorList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    Toast.makeText(getContext(), prodDelete.getString("success"), Toast.LENGTH_LONG).show();
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

    private void ActorDetailsDialog(int position) {
            RequestQueue queue = Volley.newRequestQueue(getContext());

            String url = getString(R.string.BASE_URL)+"actor/".concat(String.valueOf(position));
            System.out.println(url);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    (Response.Listener<JSONObject>) response -> {
                        System.out.println(response.toString());
                        AlertDialog.Builder actorDialog = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.activity_actor_details, null);

                        ImageView ivActorPoster = dialogView.findViewById(R.id.ivDactorPoster);
                        TextView tvDactorName = dialogView.findViewById(R.id.tvDactorName);
                        TextView tvDactorNote = dialogView.findViewById(R.id.tvDactorNote);
                        TextView tvDactorMovies = dialogView.findViewById(R.id.tvDactorMovies);

                        try {
                            tvDactorName.setText(response.getString("name"));
                            tvDactorNote.setText(response.getString("note"));
                            Picasso.get().load(response.getString("poster")).into(ivActorPoster);

                            JSONArray movies = response.getJSONArray("films");
                            StringBuilder movieList = new StringBuilder();
                            movieList.append("Movies: \n");
                            for(int i =0; i < movies.length(); i++){
                                JSONObject actor = (JSONObject) movies.get(i);
                                movieList.append(actor.getString("name") + "\n");
                            }
                            tvDactorMovies.setText(movieList);

                            actorDialog.setView(dialogView)
                                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                                    .create()
                                    .show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    },
                    (Response.ErrorListener) Throwable::printStackTrace){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(getContext()).getString("access_token", null));
                    System.out.println(headers);
                    return headers;
                }
            };

            queue.add(request);
    }
}




