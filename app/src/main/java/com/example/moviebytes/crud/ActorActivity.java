package com.example.moviebytes.crud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
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
import com.example.moviebytes.ShowDetails.ActorDetails;
import com.example.moviebytes.databinding.ActivityActorBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActorActivity extends AppCompatActivity {

    private int ACTOR_PHOTO_REQUEST = 6;
    private ActivityActorBinding binding;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnCactorPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


        binding.btnCactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject actor = new JSONObject();
                try {
                    actor.put("name", binding.etCactorName.getText().toString());
                    actor.put("note", binding.etCactorNote.getText().toString());
                    actor.put("poster", getStringImage(bitmap));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(actor);

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = getString(R.string.BASE_URL).concat("actor");
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, actor, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("status") == 200){
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(ActorActivity.this).getString("access_token", null));
                        System.out.println(headers);
                        return headers;
                    }
                };
                queue.add(request);
            }
        });

    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),ACTOR_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTOR_PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Picasso.get().load(filePath).into(binding.ivCactorPoster);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}