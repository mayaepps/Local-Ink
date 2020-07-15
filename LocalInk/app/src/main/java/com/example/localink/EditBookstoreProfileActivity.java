package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.localink.Models.Bookstore;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.databinding.ActivityBookstoreMainBinding;
import com.example.localink.databinding.ActivityEditBookstoreProfileBinding;
import com.example.localink.databinding.ActivityEditProfileBinding;
import com.example.localink.databinding.ActivityLoginBinding;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class EditBookstoreProfileActivity extends AppCompatActivity {

    ActivityEditBookstoreProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityEditBookstoreProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        Intent i = getIntent();
        LocalInkUser user = Parcels.unwrap(i.getParcelableExtra(ParseUser.class.getSimpleName()));
        String name = i.getStringExtra(Bookstore.class.getSimpleName());

        // TODO: change the way location is stored so all fields can be populated when the user edits the location
        binding.etStreetAddress.setText(user.getLocation());
        binding.etName.setText(name);

        setContentView(view);

        binding.fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Save the changes to Parse
                finish();
            }
        });

    }


}