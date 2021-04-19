package com.example.moviebytes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviebytes.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences.Editor edits;
    private LoginPreference logPref;
    private ActivityLoginBinding bind;
    private static final String loginURL = "http://192.168.0.26:8000/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      binds all the views/fields of the Activity login xml file and inflate the layout
        bind = ActivityLoginBinding.inflate(getLayoutInflater());
//      set the view by calling the root view
        setContentView(bind.getRoot());
//      this will hide the actionbar or the top bar inside the current activity only
        getSupportActionBar().hide();
//      create a look a like link ex: <anchor>
        generateTextViewLink(bind.btnRegister,20, bind.btnRegister.length());
        generateTextViewLink(bind.btnForgotPass,0, bind.btnForgotPass.length());

        logPref = LoginPreference.getInstance(getApplicationContext());
//      this will check for access token it there is login first then if not show mainactivity
        checkLogin(this);
        textInputs();
    }

    /**
     * Loads Activity for Registration Form <br>
     *<br>
     * This will determine what Activity to load
     * Verifies if an email is present on the SharedPreference file
     * Also preference file for login is handled on {@link LoginPreference LoginPreference} class
     *
     * @param cont Activity Context
     *
     */
    public void checkLogin(Context cont) {
        LoginPreference ckPref = LoginPreference.getInstance(cont);
        if(ckPref.contains("email")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            login();
            signUp();
        }
    }

    /**
     * Login method <br>
     *<br>
     * Method for Login this will handle logic and request
     * to the Laravel backend server
     *
     */
    private void login() {
        bind.btnLogin.setOnClickListener(v -> {
            String email = bind.etEmail.getText().toString();
            String pass = bind.etPassword.getText().toString();
            JSONObject loginDetails = new JSONObject();

            if(email.contains("@")) {
                Toast.makeText(LoginActivity.this,"Email Valid", Toast.LENGTH_SHORT).show();
                try {
                    loginDetails.put("email",bind.etEmail.getText().toString());
                    loginDetails.put("password",bind.etPassword.getText().toString());

                    RequestQueue queue = Volley.newRequestQueue(this);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, loginURL, loginDetails, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Ok response "+response);

                            try {
                                if(response.has("message")) {
                                    bind.tfEmail.setError("Invalid Credentials check email");
                                    bind.tfPassword.setError("Invalid Credentials check password");
                                } else {
                                    String token = response.getString("access_token");
                                    rememberMe(token);
                                    loadMain();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, error -> {
                        System.out.println("ERROR IN RESPONSE");
                        error.printStackTrace();
                    });

                    queue.add(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//           else if the email is not valid
            } else {
                Toast.makeText(LoginActivity.this,"Invalid Email Provided", Toast.LENGTH_SHORT).show();
            } //end if else
        }); //end listener
    } //end login function

    /**
     * Loads Activity for Registration Form <br>
     *<br>
     * Method for Sign Up activity, this will create a new
     * intent for SignupActivity class
     *
     */
    private void signUp() {
        bind.btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Remember User login Credentials <br>
     *
     * It will handle logic for saving data in the preferences file
     *edit the SharedPreferences and put email, password and token if chosen
     *to be remembered. And put email as guest, and the token only if not checked.
     *
     * @param token The token for accessing API endpoints from Laravel
     *
     */
    private void rememberMe(String token) {
        edits = logPref.edit();

        if(bind.cbxRemember.isChecked() == true) {
            edits.putString("email", bind.etEmail.getText().toString());
            edits.putString("password", bind.etPassword.getText().toString());
            edits.putString("access_token", token);
        } else {
            edits.putString("guest", bind.etEmail.getText().toString());
            edits.putString("access_token", token);
        }

//        Snackbar.make(bind.card,
//                "Welcome "+ logPref.getString("email", "guest user").replace("@",""),
//                Snackbar.LENGTH_LONG).show();

        edits.apply();
    }

    private void loadMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Generate a link look a like for TextView.
     *
     * @param textView The TextView that will be formatted as link.
     * @param start Starting index from the TextView characters to be formatted.
     * @param end Ending index from the TextView characters to be formatted.
     *
     */
    private void generateTextViewLink(TextView textView,int start, int end) {
        SpannableString registerLink = new SpannableString(textView.getText());
        registerLink.setSpan(new URLSpan("#"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerLink.setSpan(new ForegroundColorSpan(Color.BLUE), start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(registerLink, TextView.BufferType.SPANNABLE);
    }

    private void textInputs(){
        bind.etEmail.setOnFocusChangeListener((v, hasFocus) -> bind.tfEmail.setError(null));
        bind.etPassword.setOnFocusChangeListener((v, hasFocus) -> bind.tfPassword.setError(null));
    }
}