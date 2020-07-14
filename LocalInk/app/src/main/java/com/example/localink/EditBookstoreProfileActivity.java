package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.localink.databinding.ActivityBookstoreMainBinding;
import com.example.localink.databinding.ActivityEditBookstoreProfileBinding;
import com.example.localink.databinding.ActivityEditProfileBinding;
import com.example.localink.databinding.ActivityLoginBinding;

public class EditBookstoreProfileActivity extends AppCompatActivity {

    ActivityEditBookstoreProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // view binding
        binding = ActivityEditBookstoreProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Save the changes to Parse
                finish();
            }
        });

    }


}