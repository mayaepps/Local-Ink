package com.example.localink;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.localink.Fragments.WishlistFragment;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.databinding.ActivityBookDetailsBinding;
import com.example.localink.databinding.ActivityEditBookstoreProfileBinding;
import com.example.localink.databinding.ActivityEditProfileBinding;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BookDetailsActivity extends AppCompatActivity {

    private static final String TAG = "BookDetailsActivity";
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        ActivityBookDetailsBinding binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        Intent i = getIntent();
        book = i.getParcelableExtra(Book.class.getSimpleName());

        binding.fabAddToWishlist.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                // Add book to wishlist
                LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());
                List<Book> wishlist = user.getWishlist();
                for (Book wishBook : wishlist) {
                    try {
                        if (wishBook.getIsbn().equals(book.getIsbn())) {
                            Toast.makeText(BookDetailsActivity.this, book.getTitle() + " is already in your wishlist!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Could not get ISBN from book " + book.getTitle(), e);
                    }
                }

                wishlist.add(book);
                user.setWishlist(wishlist);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error saving book to wishlist: " + e.getMessage());
                            return;
                        } else {
                            Toast.makeText(BookDetailsActivity.this, book.getTitle() + " saved to wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Go to MainActivity
                Intent i = new Intent(BookDetailsActivity.this, MainActivity.class);
                i.putExtra(Integer.class.getSimpleName(), R.id.action_wishlist);
                startActivity(i);
            }
        });

        try {
            binding.tvTitle.setText(book.getTitle());
            binding.tvAuthor.setText(book.getAuthor());
            binding.tvSynopsis.setText(book.getSynopsis());
            binding.tvGenre.setText(book.getGenre());
            binding.tvAgeRange.setText(book.getAgeRange());
            binding.tvStoreLocation.setText("Location: ");
            Glide.with(this).load(book.getCover()).into(binding.ivCover);
        } catch (ParseException e) {
            Log.e(TAG, "Error getting book details: " + e.getMessage());
        }
    }
}