package com.example.moviebytes.ShowDetails;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActorDetails extends AppCompatActivity{

    private int ACTOR_POSTER_REQUEST = 1;
    private TextView tvDactorName, tvDactorNote, tvDactorMovies;
    private Bitmap bitmap;
    private ImageView ivDactorPoster;
    private int actor_id;
    private String newName;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_details);

        intent = getIntent();
        actor_id = intent.getIntExtra("actor_id",0);

        tvDactorName = findViewById(R.id.tvDactorName);
        tvDactorNote = findViewById(R.id.tvDactorNote);
        tvDactorMovies = findViewById(R.id.tvDactorMovies);
        ivDactorPoster = findViewById(R.id.ivDactorPoster);


        RequestQueue queue = Volley.newRequestQueue(this);
        String public_url = getString(R.string.PUBLIC_URL);
        String url = getString(R.string.BASE_URL)+"actor/".concat(String.valueOf(actor_id));
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        try {
                            tvDactorName.setText(response.getString("name"));
                            tvDactorNote.setText(response.getString("note"));

                            Picasso.get().load(public_url.concat(response.getString("poster"))).fit().centerCrop().into(ivDactorPoster);

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

        Button btnEditActor = findViewById(R.id.btnActorEdit);
        btnEditActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        Button btnEditActorSave = findViewById(R.id.btnActorEditSave);
        btnEditActorSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject actor = new JSONObject();
                try {
                    actor.put("name",tvDactorName.getText().toString());
                    actor.put("note",tvDactorNote.getText().toString());
                    if(bitmap == null){
                        actor.put("poster", getStringImage(((BitmapDrawable)ivDactorPoster.getDrawable()).getBitmap()));
                    } else {
                        actor.put("poster", getStringImage(bitmap));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println(actor);

                RequestQueue queue = Volley.newRequestQueue(ActorDetails.this);
                String url = getString(R.string.BASE_URL)+"actor/".concat(String.valueOf(actor_id));
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, actor, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ActorDetails.this,"EDITED",Toast.LENGTH_SHORT).show();
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
                        headers.put("Authorization", "Bearer "+ LoginPreference.getInstance(ActorDetails.this).getString("access_token", null));
                        System.out.println(headers);
                        return headers;
                    }
                };

                queue.add(request);
            }
        });

        tvDactorName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editorDialog(tvDactorName);
                return true;
            }
        });

        tvDactorNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editorDialog(tvDactorNote);
                return true;
            }
        });

    }

    public void editorDialog(TextView tv){
        AlertDialog.Builder dialog = new AlertDialog.Builder(ActorDetails.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_actor, null);
        EditText etEactor = (EditText) view.findViewById(R.id.etEactor);

        System.out.println(etEactor.getText().toString());
        Toast.makeText(ActorDetails.this, etEactor.getText().toString(), Toast.LENGTH_LONG).show();

        dialog.setView(view);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tv.setText(etEactor.getText().toString());
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intentback = new Intent();
//        intentback.putExtra("name", tvDactorName.getText().toString());
//        intentback.putExtra("note", tvDactorNote.getText().toString());
////        intentback.putExtra("poster",((BitmapDrawable)ivDactorPoster.getDrawable()).getBitmap());
//        setResult(RESULT_OK,intentback);
//        finish();
//    }

    //GETTING BITMAP STRING
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //FILE CHOOSER METHOD | GALLERY BY DEFAULT
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),ACTOR_POSTER_REQUEST);
    }
    //ON AVTIVITY RESULT NG FILE CHOOSER
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTOR_POSTER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                //Picasso.get().load(bitmap.into(imageView);
                //Picasso.get().load(filePath).resize(100, 100).
                Picasso.get().load(filePath).fit().centerCrop().into(ivDactorPoster);
//                setFilePath(filePath);
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}