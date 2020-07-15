package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.localink.Models.LocalInkUser;
import com.example.localink.databinding.ActivityLoginBinding;
import com.google.android.material.button.MaterialButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        // If someone is already logged in, go straight to the main activity

        ParseUser.getCurrentUser().fetchInBackground();
        if (ParseUser.getCurrentUser() != null) {
            LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());
            if (user.isBookstore()) {
                goToActivity(BookstoreMainActivity.class);
            } else {
                goToActivity(MainActivity.class);
            }
        }

        // Log in as a reader button takes the user to MainActivity
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                logInUser(username, password);
            }
        });

        // Register as a reader button takes the user to the RegisterReaderActivity
        binding.btnReaderRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(RegisterReaderActivity.class);
            }
        });

        // Register as a bookstore button takes the user to the RegisterBookstoreActivity
        binding.btnBookstoreRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(RegisterBookstoreActivity.class);
            }
        });
    }

    private void logInUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error", e);
                    Toast.makeText(LoginActivity.this, "Error logging in: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                } else if (user != null) {
                    Toast.makeText(LoginActivity.this, "Logging " + user.getUsername() + " in to Local Ink", Toast.LENGTH_SHORT).show();
                    if (user.getBoolean("isBookstore")) {
                        goToActivity(BookstoreMainActivity.class);
                    } else {
                        goToActivity(MainActivity.class);
                    }
                }
            }
        });
    }

    private void goToActivity(Class target) {
        Intent i = new Intent(LoginActivity.this, target);
        startActivity(i);
    }
}