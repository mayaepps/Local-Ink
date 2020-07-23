package com.example.localink;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.Utils.ImageUtils;
import com.example.localink.databinding.ActivityEditProfileBinding;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        Intent i = getIntent();
        final LocalInkUser user = Parcels.unwrap(i.getParcelableExtra(ParseUser.class.getSimpleName()));

        // Populate the current values into the views
        binding.etName.setText(user.getName());
        ParseFile profileImage = user.getProfileImage();
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).circleCrop().into(binding.ivProfileImage);
        }

        //setSpinnerToValue(binding.spnrGenre, user.getGenrePreferences());
        setSpinnerToValue(binding.spnrAgeRange, user.getAgePreference());

        // When the save button is pressed, save all the selections to Parse and go back to profile activity
        binding.fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get values
                String name = binding.etName.getText().toString();
                List<String> genrePreferences = new ArrayList<>();
                genrePreferences.add(binding.spnrGenre.getSelectedItem().toString());
                String agePreference = binding.spnrAgeRange.getSelectedItem().toString();

                //Save new values to Parse
                user.setName(name);
                user.setGenrePreferences(genrePreferences);
                user.setAgePreference(agePreference);
                if (ImageUtils.getPhotoFile() != null) {
                    user.setProfileImage(new ParseFile(ImageUtils.getPhotoFile()));
                }

                // Save changes to user to Parse
                user.getUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error saving changes to parse: " + e.getMessage(), e);
                        } else {
                            // Return back to ProfileFragment by finishing this activity
                            finish();
                        }
                    }
                });
            }
        });

        // Launch the camera when the change profile image button is tapped
        binding.btnChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtils.launchCamera(EditProfileActivity.this, "photo.jpg");
            }
        });
    }

    //Per Codepath Spinner guide
    // Takes a spinner and the value the spinner should be set to and sets the spinner to that value
    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When the camera activity comes back with the profile image
        if (requestCode == ImageUtils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(ImageUtils.getPhotoFile().getAbsolutePath());
                // Load the taken image into a preview 
                binding.ivProfileImage.setImageBitmap(takenImage);
                // TODO: compress/shrink the file so it will take less time loading to/from Parse
                Log.i(TAG, "Image successfully saved in the file provider");

            } else {
                // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}