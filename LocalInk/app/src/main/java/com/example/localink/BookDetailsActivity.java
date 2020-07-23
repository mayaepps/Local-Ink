package com.example.localink;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.localink.Fragments.MapsFragment;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.databinding.ActivityBookDetailsBinding;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class BookDetailsActivity extends AppCompatActivity {

    private static final String TAG = "BookDetailsActivity";
    private FragmentTransaction fragmentTransaction;
    private Book book;
    private ParseUser store;
    ActivityBookDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        // Get the book to be detailed from the intent
        Intent i = getIntent();
        book = i.getParcelableExtra(Book.class.getSimpleName());

        // When the add button is tapped, add the book to the user's wishlist
        binding.fabAddToWishlist.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                // Make sure the book isn't already in the wishlist
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

                // Add and save the book to the wishlist
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

        populateViews();
    }

    private void populateViews() {
        // Populate the views in the detail activity with the information from the book object
        try {
            binding.tvTitle.setText(book.getTitle());
            binding.tvAuthor.setText(book.getAuthor());
            binding.tvSynopsis.setText(book.getSynopsis());
            binding.tvGenre.setText(book.getGenre());
            binding.tvAgeRange.setText(book.getAgeRange());
            book.getBookstore().fetchInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    LocalInkUser localInkUser = new LocalInkUser((user));
                    store = user;
                    startMap();

                    String address = localInkUser.getLocation();
                    binding.tvStoreLocation.setText("At " + localInkUser.getName() + ": " + address);
                }
            });
            Glide.with(this).load(book.getCover()).into(binding.ivCover);
        } catch (ParseException e) {
            Log.e(TAG, "Error getting book details: " + e.getMessage());
        }
    }

    // Start the Google Maps fragment in the MapView at the bottom of the details
    // Send the current user as a bundle (used to get store name/location for the marker)
    private void startMap() {
        Fragment mapsFragment = new MapsFragment();
        Bundle bundle = new Bundle();
        ArrayList<ParseUser> stores = new ArrayList<>();
        stores.add(store);
        bundle.putParcelableArrayList(ParseUser.class.getSimpleName(), stores);
        mapsFragment.setArguments(bundle);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapView, mapsFragment).commit();
    }
}