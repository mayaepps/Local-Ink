package com.example.localink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.Utils.ChipUtils;
import com.example.localink.databinding.ActivityRegisterReaderBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

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

        ChipUtils.setUpChips(this, binding.ageRangeChips, getResources().getStringArray(R.array.age_ranges_array), true);
        ChipUtils.setUpChips(this, binding.genreChips, getResources().getStringArray(R.array.genres_array), false);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    // Create a new user and sign it up in Parse
    private void registerUser() {
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(binding.etUsername.getText().toString());
        user.setPassword(binding.etPassword.getText().toString());
        // Set custom properties
        LocalInkUser newUser = new LocalInkUser(user);
        newUser.setName(binding.etName.getText().toString());
        newUser.setIsBookstore(false);

        List<String> genres = ChipUtils.getChipSelections(binding.genreChips);
        if (genres.size() > 0) {
            newUser.setGenrePreferences(genres);
        } else {
            Toast.makeText(this, "You must select at least one genre!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> ageRange = ChipUtils.getChipSelections(binding.ageRangeChips);
        if (ageRange.size() > 0) {
            newUser.setAgePreferences(ageRange);
        } else {
            Toast.makeText(this, "You must select an age range!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        newUser.setWishlist(new ArrayList<Book>());

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    // Sign up didn't succeed
                    Toast.makeText(RegisterReaderActivity.this, "Could not sign up new user: "
                            + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    // Sign up successful, go to login activity
                    Intent i = new Intent(RegisterReaderActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}