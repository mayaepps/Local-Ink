package com.example.localink;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.databinding.ActivityEditBookstoreProfileBinding;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;

public class EditBookstoreProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditBookstoreActivity";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 18;
    ActivityEditBookstoreProfileBinding binding;
    private File photoFile;
    private String photoFileName = "bookstorePhoto.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityEditBookstoreProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        Intent i = getIntent();
        final LocalInkUser user = Parcels.unwrap(i.getParcelableExtra(ParseUser.class.getSimpleName()));

        // TODO: change the way location is stored so all fields can be populated when the user edits the location
        binding.etStreetAddress.setText(user.getLocation());
        binding.etName.setText(user.getName());

        binding.fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToParse(user);

            }
        });

        binding.btnChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
    }

    private void saveToParse(LocalInkUser user) {
        // Get the new info from the views
        String name = binding.etName.getText().toString();
        String address = binding.etStreetAddress.getText().toString();

        user.setLocation(address);
        user.setName(name);
        if (photoFile != null) {
            user.setProfileImage(new ParseFile(photoFile));
        }
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

    private  void launchCamera() {
        // create implicit Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference for future access
        photoFile = EditProfileActivity.getPhotoFile(this, photoFileName);

        // wrap File object into a content provider, required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(this, "com.localink.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // As long as the result is not null, it's safe to use the intent to go to the camera
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When the camera activity comes back with the profile image
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(photoFile).circleCrop().into(binding.ivProfileImage);
                // TODO: compress/shrink the file so it will take less time loading to/from Parse
                Log.i(TAG, "Image successfully saved in the file provider");

            } else {
                // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}