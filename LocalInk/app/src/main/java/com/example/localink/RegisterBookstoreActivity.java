package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.localink.Models.Book;
import com.example.localink.Models.Bookstore;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.databinding.ActivityMainBinding;
import com.example.localink.databinding.ActivityRegisterBookstoreBinding;
import com.example.localink.databinding.ActivityRegisterReaderBinding;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class RegisterBookstoreActivity extends AppCompatActivity {

    private static final String TAG = "RegisterBookstoreActivi";
    ActivityRegisterBookstoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityRegisterBookstoreBinding.inflate(getLayoutInflater());;
        View view = binding.getRoot();

        setContentView(view);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        Intent i = new Intent(RegisterBookstoreActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

}