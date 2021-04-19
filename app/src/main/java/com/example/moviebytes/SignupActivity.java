package com.example.moviebytes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviebytes.databinding.ActivitySignupBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String registerUrl = "http://192.168.0.26:8000/api/register";
    private ActivitySignupBinding bind;
    private LoginPreference pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        getSupportActionBar().hide();

        emailValidate();

        signUp();

        backLogin();

    }

    public void signUp() {
        bind.btnSign.setOnClickListener(v -> {
            RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);

            if(bind.etEmailSign.getError() != getString(R.string.error_validate)) {
                JSONObject credentials = new JSONObject();
                try {
                    credentials.put("name", bind.etNameSign.getText().toString());
                    credentials.put("email", bind.etEmailSign.getText().toString());
                    credentials.put("password", bind.etPasswordSign.getText().toString());
                    credentials.put("password_confirmation", bind.etConfirmSign.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, registerUrl, credentials, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("message")) {
                            try {
                                Snackbar.make(bind.signUpCard, response.getString("message").toString(),Snackbar.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("OK response "+ response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                queue.add(request);
//              finish the activity to return to the login activity
                finish();
            }
        });
    }

    public void emailValidate() {
        bind.etEmailSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  bind.tfEmailSign.setErrorEnabled(false);}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bind.tfEmailSign.setErrorEnabled(true);
                bind.tfEmailSign.setEndIconDrawable(ContextCompat.getDrawable(SignupActivity.this, R.drawable.ic_baseline_check_24)); }

            @Override
            public void afterTextChanged(Editable s) {
                if(Patterns.EMAIL_ADDRESS.matcher(bind.etEmailSign.getText().toString().trim()).matches()) {
                    bind.tfEmailSign.setError(null);
                    bind.tfEmailSign.setErrorEnabled(false);
                    bind.tfEmailSign.setEndIconDrawable(ContextCompat.getDrawable(SignupActivity.this, R.drawable.ic_baseline_check_24));
                } else {
                    bind.tfEmailSign.setError(getString(R.string.error_validate));
                }
            }
        });
    }

    public void backLogin() {
        bind.btnBackLogin.setOnClickListener(v -> {
            finish();
        });
    }
}
