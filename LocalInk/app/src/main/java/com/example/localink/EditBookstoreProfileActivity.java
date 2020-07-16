package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.localink.Models.Bookstore;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.databinding.ActivityEditBookstoreProfileBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class EditBookstoreProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditBookstoreActivity";
    ActivityEditBookstoreProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityEditBookstoreProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        Intent i = getIntent();
        final LocalInkUser user = Parcels.unwrap(i.getParcelableExtra(ParseUser.class.getSimpleName()));
        String name = i.getStringExtra(Bookstore.class.getSimpleName());

        // TODO: change the way location is stored so all fields can be populated when the user edits the location
        binding.etStreetAddress.setText(user.getLocation());
        binding.etName.setText(name);

        binding.fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the new info from the views
                String name = binding.etName.getText().toString();
                String address = binding.etStreetAddress.getText().toString();

                user.setLocation(address);
                // TODO: set name when I get getBookstore working
                // user.getBookstore.setName(name)
                user.getUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error saving profile changes to Parse: " + e.getMessage(), e);
                        } else {
                            finish();
                        }
                    }
                });
            }
        });

    }


}