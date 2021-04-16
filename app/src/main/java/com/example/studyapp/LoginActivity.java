package com.example.studyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText idET, passwordET;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        idET = (EditText) findViewById(R.id.idET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = idET.getText().toString();
                String userPassword = passwordET.getText().toString();

                if(userID.isEmpty()) {
                    negativeBuilder("Please insert Email");
                    return;
                }
                if(userPassword.isEmpty()) {
                    negativeBuilder("Please insert Password");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("user_id", userID);
                    jsonObject.accumulate("user_password", userPassword);
                    new JSONTask(jsonObject, "login").execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Response.Listener<String> responseLister = new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonResponse = new JSONObject(response);
//                            boolean success = jsonResponse.getBoolean("success");
//                            if(success) {
//                                // Save user info
//                                SharedPreferences.Editor autoLogin = FirstActivity.userInfo.edit();
//                                autoLogin.putString(FirstActivity.USER_ID, userID);
//                                autoLogin.putString(FirstActivity.USER_PASSWORD, userPassword);
//                                autoLogin.commit();
//
//                                progressBar.setVisibility(View.GONE);
//
//                                // Next Screen
//                                Intent intent = new Intent(LoginActivity.this, UserNameActivity.class);
//                                LoginActivity.this.startActivity(intent);
//                                finish();
//                            }
//                            else {
//                                negativeBuilder("Failed Sign In");
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                };
//                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseLister);
//                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//                queue.add(loginRequest);
            }
        });
    }

    private void negativeBuilder(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(msg)
                .setNegativeButton("close", null)
                .create()
                .show();
    }
}