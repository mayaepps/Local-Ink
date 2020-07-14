package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.localink.databinding.ActivityEditProfileBinding;
import com.example.localink.databinding.ActivityLoginBinding;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Save changes in Parse
                finish();
            }
        });
    }
}