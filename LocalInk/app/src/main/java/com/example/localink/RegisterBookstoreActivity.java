package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.localink.databinding.ActivityMainBinding;
import com.example.localink.databinding.ActivityRegisterBookstoreBinding;
import com.example.localink.databinding.ActivityRegisterReaderBinding;

public class RegisterBookstoreActivity extends AppCompatActivity {

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
                Intent i = new Intent(RegisterBookstoreActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}