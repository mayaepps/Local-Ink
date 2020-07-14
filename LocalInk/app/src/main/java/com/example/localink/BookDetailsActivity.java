package com.example.localink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.localink.Fragments.WishlistFragment;
import com.example.localink.databinding.ActivityBookDetailsBinding;
import com.example.localink.databinding.ActivityEditBookstoreProfileBinding;
import com.example.localink.databinding.ActivityEditProfileBinding;

public class BookDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // view binding
        ActivityBookDetailsBinding binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        binding.fabAddToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Add book to wishlist
                Intent i = new Intent(BookDetailsActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}