package com.example.sloff.tokentestempty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//192.168.50.126:3000
public class LoginActivity extends AppCompatActivity {
    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    UserClient userClient = retrofit.create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        Button getSecretButton = findViewById(R.id.getSecretButton);

        //Logic for when button is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        getSecretButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSecret();
            }
        });
    }

    private static String token;

    private void login() {
        EditText mUsername = (EditText)findViewById(R.id.loginUsernameField);
        EditText mPassword = (EditText)findViewById(R.id.loginPasswordField);
        Login login = new Login(mUsername.getText().toString(), mPassword.getText().toString());
        Call<User> call = userClient.login(login);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Token acquired", Toast.LENGTH_SHORT).show();
                    token = response.body().getToken();
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong Login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSecret() {
        Call<ResponseBody> call = userClient.getJSON(token);

        final Intent getUserScreenIntent = new Intent(this,  DashboardActivity.class);

        final int result = 1;

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
//                        Toast.makeText(LoginActivity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                        String res = response.body().string();

                        getUserScreenIntent.putExtra("user", res);
                        startActivity(getUserScreenIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong Token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
