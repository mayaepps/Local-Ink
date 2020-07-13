package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.localink.databinding.ActivityRegisterReaderBinding;

public class RegisterReaderActivity extends AppCompatActivity {

    ActivityRegisterReaderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_reader);

        // view binding
        binding = ActivityRegisterReaderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterReaderActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}