package com.example.localink;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.Utils.ChipUtils;
import com.example.localink.Utils.ImageUtils;
import com.example.localink.databinding.ActivityEditProfileBinding;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

        ChipUtils.setUpChips(this, binding.ageRangeChips, getResources().getStringArray(R.array.age_ranges_array), false);
        ChipUtils.setUpChips(this, binding.genreChips, getResources().getStringArray(R.array.genres_array), false);
        ChipUtils.selectChips(user.getGenrePreferences(), binding.genreChips);
        ChipUtils.selectChips(user.getAgePreferences(), binding.ageRangeChips);

        // When the save button is pressed, save all the selections to Parse and go back to profile activity
        binding.fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get values
                String name = binding.etName.getText().toString();

                //Save new values to Parse
                user.setName(name);

                List<String> genres = ChipUtils.getChipSelections(binding.genreChips);
                if (genres.size() > 0) {
                    user.setGenrePreferences(genres);
                } else {
                    Toast.makeText(EditProfileActivity.this, "You must select at least one genre!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> ageRange = ChipUtils.getChipSelections(binding.ageRangeChips);
                if (ageRange.size() > 0) {
                    user.setAgePreferences(ageRange);
                } else {
                    Toast.makeText(EditProfileActivity.this, "You must select an age range!", Toast.LENGTH_SHORT).show();
                    return;
                }
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