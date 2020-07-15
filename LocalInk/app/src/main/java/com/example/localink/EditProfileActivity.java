package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.localink.Models.LocalInkUser;
import com.example.localink.databinding.ActivityEditProfileBinding;
import com.example.localink.databinding.ActivityLoginBinding;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        Intent i = getIntent();
        LocalInkUser user = Parcels.unwrap(i.getParcelableExtra(ParseUser.class.getSimpleName()));

        // TODO: change the way location is stored so all fields can be populated when the user edits the location
        binding.etStreetAddress.setText(user.getLocation());

        binding.fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Save changes in Parse
                finish();
            }
        });
    }
}