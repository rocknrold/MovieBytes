package com.example.moviebytes.crud;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationAttributes;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviebytes.LoginPreference;
import com.example.moviebytes.MainActivity;
import com.example.moviebytes.R;
import com.example.moviebytes.databinding.ActivityMovieBinding;
import com.example.moviebytes.models.Actor;
import com.example.moviebytes.models.Certs;
import com.example.moviebytes.models.Genre;
import com.example.moviebytes.models.Movie;
import com.example.moviebytes.models.Producer;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MovieActivity extends AppCompatActivity {

    private ActivityMovieBinding bind;
    private int MOVIE_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private final List<Genre> genreList = new ArrayList<Genre>();
    private final List<Certs> certList = new ArrayList<Certs>();
    private final List<Producer> producerList = new ArrayList<Producer>();
    private final List<Actor> actorList = new ArrayList<Actor>();
    private final HashMap<String,Integer> actorIds = new HashMap<>();
    private final HashMap<String,Boolean> validator = new HashMap<String, Boolean>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMovieBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        try {
            Intent mvDetails = getIntent();
            if(mvDetails.hasExtra("MOVIE_ID")){
                int mvId = mvDetails.getIntExtra("MOVIE_ID", 0);
                loadMovieDetails(mvId);
                bind.btnCmovie.setEnabled(true);
                bind.btnCmovie.setOnClickListener(this::updateMovie);
                System.out.println("MY LISTENER IS UPDATE");
            } else {
                bind.btnCmovie.setOnClickListener(this::saveMovie);
                System.out.println("MY LISTENER IS SAVE");
            }


        } catch (NullPointerException e) {
            e.getMessage();
        }

        validator.put("title", false);
        validator.put("story", false);
        validator.put("date", false);
        validator.put("info", false);
        validator.put("dur", false);
        validator.put("genre", false);
        validator.put("cert", false);
        validator.put("prod", false);
        validator.put("poster", false);


        bind.btnCmoviePoster.setOnClickListener(this::showFileChooser);
        bind.etCmovieDate.setOnClickListener(this::showDatePicker);

        if(bind.ivCmoviePoster.getDrawable() != null){
            validator.replace("poster",true);
        }

        genreSpinner();
        bind.sCmovieGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validator.replace("genre",true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                validation();
            }
        });

        certificateSpinner();
        bind.sCmovieCertificate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validator.replace("cert",true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                validation();
            }
        });

        producerSpinner();
        bind.sCmovieProducer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validator.replace("prod",true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                validation();
            }
        });

        bind.etCmovieTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!(bind.etCmovieTitle.getText().toString() == null)) {
                    validator.replace("title",false,true);
                    validation();
                }
            }
        });

        bind.etCmovieStory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!(bind.etCmovieStory.getText().toString() == null)){
                    validator.replace("story",true);
                    validation();
                }
            }
        });

        bind.etCmovieDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!(bind.etCmovieDate.getText().toString() == null)){
                    validator.replace("date",true);
                    validation();
                }
            }
        });

        bind.etCmovieInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!(bind.etCmovieInfo.getText().toString() == null)){
                    validator.replace("info", true);
                    validation();
                }
            }
        });

        bind.etCmovieDur.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!(bind.etCmovieDur.getText().toString() == null)){
                    validator.replace("dur",true);
                    validation();
                }
            }
        });

        actors();
        bind.acCmovieActors.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view, position, id) -> {
                Actor ac = (Actor) parent.getItemAtPosition(position);
                chipCreator(ac.getActorId(),ac.getName());
        });

    }

    private void chipCreator(int id, String name) {
//        Toast.makeText(MovieActivity.this, name , Toast.LENGTH_LONG).show();
        Chip chip = new Chip(MovieActivity.this);
        chip.setText(name);
        chip.setChipBackgroundColorResource(R.color.app_color);
        chip.setCloseIconVisible(true);
        chip.setTextAppearance(R.style.TextAppearance_AppCompat_Widget_TextView_SpinnerItem);
        chip.setOnCloseIconClickListener((View.OnClickListener) v -> {
            bind.chipGroupActors.removeView((View) chip);
            actorIds.remove(chip.getText());
        });

        bind.chipGroupActors.addView(chip);
        bind.acCmovieActors.setText("");
        actorIds.put(name,id);
    }

    private void validation(){
        bind.btnCmovie.setEnabled(!validator.containsValue(false));
    }

    private String toArrayChipsActor(){
        StringBuilder stringIds = new StringBuilder();
        for(int ids: actorIds.values()){
            stringIds.append(String.valueOf(ids).concat(","));
        }
        return stringIds.toString();
    }

    private JSONObject movieDetails(){
        JSONObject cmov = new JSONObject();
        try {
            Genre genre = (Genre) bind.sCmovieGenre.getSelectedItem();
            Certs cert = (Certs) bind.sCmovieCertificate.getSelectedItem();
            Producer prod = (Producer) bind.sCmovieProducer.getSelectedItem();

            cmov.put("name", bind.etCmovieTitle.getText().toString());
            cmov.put("story", bind.etCmovieStory.getText().toString());
            cmov.put("released_at",bind.etCmovieDate.getText().toString());
            cmov.put("duration", bind.etCmovieDur.getText().toString());
            cmov.put("info", bind.etCmovieInfo.getText().toString());
            cmov.put("genre_id",genre.getId());
            cmov.put("cert_id", cert.getId());
            cmov.put("producer_id", prod.getProducer_id());
            cmov.put("actors",toArrayChipsActor());

            if(bind.ivCmoviePoster.getDrawable() == null){
                Toast.makeText(MovieActivity.this, "No poster selected please select first", Toast.LENGTH_SHORT).show();
            } else {
                if(bitmap != null){
                    cmov.put("poster", getStringImage(bitmap));
                } else {
                    if(bind.ivCmoviePoster.getDrawable() != null){
                        cmov.put("poster", getStringImage(((BitmapDrawable)bind.ivCmoviePoster.getDrawable()).getBitmap()));
                    } else {
                        Toast.makeText(MovieActivity.this, "No poster selected", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        System.out.println(cmov);
        return  cmov;
    }

    private void loadMovieDetails(int movieId){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.BASE_URL).concat("film/"+ movieId);
        String photo_url = getString(R.string.PUBLIC_URL);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            System.out.println("LOAD NG MOVIES"+ response.toString());
            try {
                Picasso.get().load(photo_url.concat(response.getString("poster"))).into(bind.ivCmoviePoster);
                bind.etCmovieTitle.setText(response.getString("name"));
                bind.etCmovieStory.setText(response.getString("story"));
                bind.etCmovieDate.setText(response.getString("released_at"));
                bind.etCmovieDur.setText(response.getString("duration"));
                bind.etCmovieInfo.setText(response.getString("info"));
                bind.sCmovieGenre.setSelection(response.getInt("genre_id"));
                bind.sCmovieCertificate.setSelection(response.getInt("certificate_id"));
                bind.sCmovieProducer.setSelection(response.getInt("producer_id"));

                JSONObject genre = response.getJSONObject("genre");
//                for()


                JSONArray actors = response.getJSONArray("actors");
                System.out.println(actors.toString());
                for(int i = 0; i < actors.length(); i++){
                    JSONObject actor = (JSONObject) actors.get(i);
                    System.out.println(actor.toString());
                    chipCreator(actor.getInt("id"), actor.getString("name"));
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
//                System.out.println(LoginPreference.getInstance(MovieActivity.this).getString("access_token",null));
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(MovieActivity.this).getString("access_token", null));
                return headers;
            }
        };
        queue.add(request);
    }


    private void updateMovie(View view) {
        System.out.println(movieDetails());
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.BASE_URL).concat("film/"+getIntent().getIntExtra("MOVIE_ID", 0));
        System.out.println("MOVIE UPDATE "+url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, movieDetails() , new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {
//                if(response.has("status")){
                    Toast.makeText(MovieActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    for (Map.Entry<String, Boolean> entry : validator.entrySet()) {
                        entry.setValue(false);
                    }

                    bind.btnCmovie.setEnabled(false);
                    bind.etCmovieTitle.setText(null);
                    bind.etCmovieStory.setText(null);
                    bind.etCmovieDur.setText(null);
                    bind.etCmovieDate.setText(null);
                    bind.etCmovieInfo.setText(null);
                    bind.ivCmoviePoster.setImageDrawable(null);
                    System.out.println("SUCCESS"+validator.toString());
//                }
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
//                System.out.println(LoginPreference.getInstance(MovieActivity.this).getString("access_token",null));
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(MovieActivity.this).getString("access_token", null));
                return headers;
            }
        };
        queue.add(request);
    }

    private void saveMovie(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.BASE_URL).concat("film");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, movieDetails() , new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {
                if(response.has("status")){
                    Toast.makeText(MovieActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    for (Map.Entry<String, Boolean> entry : validator.entrySet()) {
                        entry.setValue(false);
                    }

                    bind.btnCmovie.setEnabled(false);
                    bind.etCmovieTitle.setText(null);
                    bind.etCmovieStory.setText(null);
                    bind.etCmovieDur.setText(null);
                    bind.etCmovieDate.setText(null);
                    bind.etCmovieInfo.setText(null);
                    bind.ivCmoviePoster.setImageDrawable(null);
                    System.out.println("SUCCESS"+validator.toString());
                }
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
//                System.out.println(LoginPreference.getInstance(MovieActivity.this).getString("access_token",null));
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(MovieActivity.this).getString("access_token", null));
                return headers;
            }
        };
            queue.add(request);
//            System.out.println("validator "+validator.toString());
    }


//    MOVIE POSTER FOR IMAGE
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MOVIE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MOVIE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Picasso.get().load(filePath).into(bind.ivCmoviePoster);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//  FOR DATEPICKER INPUT

    private void showDatePicker(View view) {
//       For DatePicker define properties
        MaterialDatePicker.Builder datepicker = MaterialDatePicker.Builder.datePicker();
        datepicker.setTitleText("Select Released Date");
//      instantiate new material datepicker
        final MaterialDatePicker<Long> datePicker = datepicker.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Create a date format, then a date object with our offset
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = new Date(selection);
            bind.etCmovieDate.setText(simpleFormat.format(date));
        });

//      show the date picker on click to edittext
        datePicker.show(getSupportFragmentManager(), "RELEASED_DATE");
    }


//  FOR SPINNER THAT WILL BE USED IN GENRE
    public void genreSpinner(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.BASE_URL).concat("genre/all");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i < response.length(); i++){
                    try {
                        JSONObject genre = (JSONObject) response.get(i);
                        genreList.add(new Genre(genre.getString("name"), genre.getInt("id")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                System.out.println("MY GENRES "+ genreList.toString());

                ArrayAdapter<Genre> adapter = new ArrayAdapter<Genre>(MovieActivity.this, android.R.layout.simple_spinner_dropdown_item,genreList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bind.sCmovieGenre.setAdapter(adapter);
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
                Log.d("CERTS","CERTS "+LoginPreference.getInstance(MovieActivity.this).getString("access_token",null));
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(MovieActivity.this).getString("access_token", null));
                return headers;
            }
        };

        queue.add(request);
    }

//    FOR SPINNER THAT WILL BE USED IN THE CERTIFICATES
    private void certificateSpinner() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.BASE_URL).concat("certificate/all");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i < response.length(); i++){
                    try {
                        JSONObject cert = (JSONObject) response.get(i);
                        certList.add(new Certs(cert.getString("name"), cert.getInt("id")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                System.out.println("MY CERTS "+ certList.toString());

                ArrayAdapter<Certs> adapter = new ArrayAdapter<Certs>(MovieActivity.this, android.R.layout.simple_spinner_dropdown_item,certList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bind.sCmovieCertificate.setAdapter(adapter);
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
                Log.d("CERTS","CERTS "+LoginPreference.getInstance(MovieActivity.this).getString("access_token",null));
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(MovieActivity.this).getString("access_token", null));
                return headers;
            }
        };

        queue.add(request);
    }

    private void producerSpinner() {
        RequestQueue queue = Volley.newRequestQueue(this);
            String url = getString(R.string.BASE_URL).concat("producer/all");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i < response.length(); i++){
                    try {
                        JSONObject prod = (JSONObject) response.get(i);
                        producerList.add(new Producer(prod.getInt("id"),
                                                        prod.getString("name"),
                                                        prod.getString("email"),
                                                        prod.getString("website")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                System.out.println("MY PRODS "+ producerList.toString());

                ArrayAdapter<Producer> adapter = new ArrayAdapter<Producer>(MovieActivity.this,
                                                                            android.R.layout.simple_spinner_dropdown_item,
                                                                            producerList);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bind.sCmovieProducer.setAdapter(adapter);
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
                Log.d("CERTS","CERTS "+LoginPreference.getInstance(MovieActivity.this).getString("access_token",null));
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(MovieActivity.this).getString("access_token", null));
                return headers;
            }
        };

        queue.add(request);
    }

    private void actors(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.BASE_URL).concat("actor/all");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                System.out.println("ACTOR RESPONSE "+ response.toString());
                for (int i=0; i < response.length(); i++){
                    try {
                        JSONObject ac = (JSONObject) response.get(i);
                        actorList.add(new Actor(ac.getInt("id"),
                                ac.getString("name"),
                                ac.getString("note"),
                                    ""));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("MY ACTORS "+ actorList.toString());

                ArrayAdapter<Actor> actorArrayAdapter = new ArrayAdapter<Actor>(MovieActivity.this,
                        android.R.layout.simple_dropdown_item_1line, actorList);

                bind.acCmovieActors.setAdapter(actorArrayAdapter);
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
                Log.d("CERTS","CERTS "+LoginPreference.getInstance(MovieActivity.this).getString("access_token",null));
                headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(MovieActivity.this).getString("access_token", null));
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}