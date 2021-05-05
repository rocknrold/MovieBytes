package com.example.moviebytes.crud;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviebytes.LoginPreference;
import com.example.moviebytes.R;
import com.example.moviebytes.databinding.ActivityProducerBinding;
import com.example.moviebytes.models.Producer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ProducerActivity extends AppCompatActivity implements  ProducerAdapter.onProducerItemClick {

    private ActivityProducerBinding binding;
    private List<Producer> producerList;
    private ProducerAdapter adapter;
    private int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProducerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rvProducers.setHasFixedSize(true);
        binding.rvProducers.setLayoutManager(new LinearLayoutManager(this));
        producerList = new ArrayList<>();
        adapter = new ProducerAdapter(producerList);
        getAllProducers();

        binding.btnSaveProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject prod = new JSONObject();
                try {
                    prod.put("name", binding.etProdName.getText().toString());
                    prod.put("email", binding.etProdEmail.getText().toString());
                    prod.put("website", binding.etProdWebsite.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = getString(R.string.BASE_URL)+"producer";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, prod, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("message")){
                            try {
                                Toast.makeText(ProducerActivity.this,response.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                producerList.add(new Producer(response.getInt("id"),
                                        response.getString("name"),
                                        response.getString("email"),
                                        response.getString("website")));

                                adapter.notifyItemChanged(producerList.size() - 1);

                                binding.etProdName.setText("");
                                binding.etProdEmail.setText("");
                                binding.etProdWebsite.setText("");

                                Toast.makeText(ProducerActivity.this,"SAVED",Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error.networkResponse.statusCode == 500) {
                           Toast.makeText(ProducerActivity.this, "Please provide producer details", Toast.LENGTH_SHORT).show();
                        }
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        System.out.println(LoginPreference.getInstance(ProducerActivity.this).getString("access_token",null));
                        headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(ProducerActivity.this).getString("access_token", null));
                        return headers;
                    }
                };
                queue.add(request);
            }
        });

        updateProd();

    }

    private void getAllProducers() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.BASE_URL)+"producer/all";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray prodArray = new JSONArray(response);
                    for(int i=0; i < prodArray.length(); i++){
                        JSONObject prod = prodArray.getJSONObject(i);
                        producerList.add(
                                new Producer(prod.getInt("id"),
                                    prod.getString("name"),
                                    prod.getString("email"),
                                    prod.getString("website")));
                    }
                        binding.rvProducers.setAdapter(adapter);
                        adapter.setProducerItemClick(ProducerActivity.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                System.out.println(LoginPreference.getInstance(ProducerActivity.this).getString("access_token",null));
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(ProducerActivity.this).getString("access_token", null));
                System.out.println(headers);
                return headers;
            }
        };
        queue.add(request);
    }

    @Override
    public void onProducerClick(int position) {
        Producer prod = producerList.get(position);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.BASE_URL)+"producer/".concat(String.valueOf(prod.getProducer_id()));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProducerActivity.this);
                View view = LayoutInflater.from(ProducerActivity.this).inflate(R.layout.dialog_producer_detais,null);

                TextView tvDprodName = view.findViewById(R.id.tvDprodName);
                TextView tvDprodEmail = view.findViewById(R.id.tvDprodEmail);
                TextView tvDprodFilms = view.findViewById(R.id.tvDprodFilms);

                try {
                    tvDprodName.setText(response.getString("name").toString());
                    tvDprodEmail.setText(response.getString("email").toString());

                    JSONArray movies = response.getJSONArray("films");
                    StringBuilder movieList = new StringBuilder();
                    movieList.append("Movies:\n");
                    for(int i=0; i < movies.length(); i++) {
                        JSONObject movie = (JSONObject) movies.get(i);
                        movieList.append(movie.getString("name").concat("\n"));
                    }
                    tvDprodFilms.setText(movieList);

                    dialog.setView(view);
                    dialog.setPositiveButton("BACK", (dialog1, which) -> dialog1.dismiss());
                    dialog.setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                binding.etProdName.setText(response.getString("name"));
                                binding.etProdEmail.setText(response.getString("email"));
                                binding.etProdWebsite.setText(response.getString("website"));
                                binding.btnSaveProd.setEnabled(false);
                                binding.btnEditProd.setEnabled(true);

                                dialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.create().show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setSelectedPosition(position);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(ProducerActivity.this).getString("access_token", null));
                System.out.println(headers);
                return headers;
            }
        };
        queue.add(request);
    }

    @Override
    public void onProducerLongClick(int position) {
        Producer prod = producerList.get(position);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("DELETE")
                .setMessage("Do you want to delete this producer?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = getString(R.string.BASE_URL)+"producer/".concat(String.valueOf(prod.getProducer_id()));
                        StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
                            try {
                                JSONObject prodDelete = new JSONObject(response);
                                if(prodDelete.getInt("status") == 200){
                                    producerList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    Toast.makeText(ProducerActivity.this, prodDelete.getString("success"), Toast.LENGTH_LONG).show();
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
                                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(ProducerActivity.this).getString("access_token", null));
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


    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    private void updateProd() {
        binding.btnEditProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tobepdate = getSelectedPosition();
                Producer prodUpdate = producerList.get(tobepdate);

                System.out.println(prodUpdate.getProducer_id() + " ang current na selected position ay ito");
                JSONObject prod = new JSONObject();
                try {
                    prod.put("name", binding.etProdName.getText());
                    prod.put("email", binding.etProdEmail.getText());
                    prod.put("website", binding.etProdWebsite.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = getString(R.string.BASE_URL)+"producer/".concat(String.valueOf(prodUpdate.getProducer_id()));
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, prod, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("status") == 1){
                                producerList.set(tobepdate,new Producer(prodUpdate.getProducer_id(),
                                                                        binding.etProdName.getText().toString(),
                                                                        binding.etProdEmail.getText().toString(),
                                                                        binding.etProdWebsite.getText().toString()));

                                adapter.notifyItemChanged(tobepdate);

                                binding.etProdEmail.setText("");
                                binding.etProdName.setText("");
                                binding.etProdWebsite.setText("");

                                Toast.makeText(ProducerActivity.this, "Producer Updated", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 500) {
                            Toast.makeText(ProducerActivity.this,"Please Check Inputs", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(ProducerActivity.this).getString("access_token", null));
                        System.out.println(headers);
                        return headers;
                    }
                };

                queue.add(request);
                binding.btnEditProd.setEnabled(false);
                binding.btnSaveProd.setEnabled(true);
            }
        });
    }
}