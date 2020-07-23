package com.example.localink.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localink.Adapters.BooksAdapter;
import com.example.localink.BookDetailsActivity;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsFragment extends Fragment {

    private static final String TAG = "RecommendationsFragment";
    private static final int ACCESS_LOCATION_REQUEST_CODE = 15;
    private static final int MINIMUM_RECS = 10;
    private RecyclerView rvBooks;
    private BooksAdapter adapter;
    private List<Book> recommendedBooks;
    private List<Book> otherBooks;
    private LocalInkUser user;
    private FusedLocationProviderClient fusedLocationClient;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommendations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLastKnownLocation();

        user = new LocalInkUser(ParseUser.getCurrentUser());

        //Instantiate my OnClickListener from the interface in BooksAdapter
        BooksAdapter.OnClickListener clickListener = new BooksAdapter.OnClickListener() {

            // If clicked, the book item should open  a detail view activity for the book
            @Override
            public void onClick(int position) {
                Intent i = new Intent(getContext(), BookDetailsActivity.class);
                i.putExtra(Book.class.getSimpleName(), recommendedBooks.get(position));
                startActivity(i);
            }

            // Required by the interface
            @Override
            public void onLongClick(int position) {
                return;
            }
        };

        // Set up recycler view with the adapter and linear layout
        rvBooks = view.findViewById(R.id.rvBooks);
        recommendedBooks = new ArrayList<>(); // Have to initialize recommendedBooks before passing it into the adapter
        adapter = new BooksAdapter(getContext(), recommendedBooks, clickListener);
        rvBooks.setAdapter(adapter);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // Recommends books from the nearby bookstores param that fit the user's preferences
    // if there aren't enough perfect matches, it gets partial matches (that fit the age, not the genre)
    private void getRecommendations(List<ParseUser> nearbyBookstores) {
        otherBooks = new ArrayList<>();
        // Get the books from the 5 closest stores and get their available books
        for (ParseUser store : nearbyBookstores) {
            queryBooks(store);
        }

        // Get the books that perfectly match the user's preferences
        List<Book> booksToRemove = new ArrayList<>();
        for (Book book : otherBooks) {
            // Don't recommend books that are already in the wishlist
            if (inWishlist(book)) {
                booksToRemove.add(book);
                continue;
            }
            if (matchesAge(book) && matchesGenre(book)) {
                recommendedBooks.add(book);
                booksToRemove.add(book);
            }
        }
        otherBooks.removeAll(booksToRemove);
        booksToRemove.clear();

        // If there aren't enough books that fit the preferences,
        // Get the books that match the user's age preferences, but show them other genres
        if (recommendedBooks.size() < MINIMUM_RECS && otherBooks.size() > 0) {
            for (Book book : otherBooks) {
                if (matchesAge(book)) {
                    recommendedBooks.add(book);
                    booksToRemove.add(book);
                }
            }
        }
        otherBooks.removeAll(booksToRemove);
        booksToRemove.clear();

        adapter.notifyDataSetChanged();
    }

    private boolean inWishlist(Book book) {
        List<Book> wishlist = user.getWishlist();
        try {
            for (Book wishbook : wishlist) {
                if (wishbook.getIsbn().equals(book.getIsbn())) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all the books offered at the given store
    private void queryBooks(final ParseUser store) {
        ParseQuery<Book> query = ParseQuery.getQuery(Book.class);
        query.include(Book.KEY_BOOKSTORE);
        query.whereEqualTo(Book.KEY_BOOKSTORE, store);
        try {
            List<Book> queriedBooks = query.find();
            otherBooks.addAll(queriedBooks);
        } catch (ParseException e) {
            Log.e(TAG, "Error retrieving books from " + store.getUsername(), e);
            return;
        }
    }

    // Get the bookstores that are near the currently logged in user
    // Returns a list of bookstores of length limit (from nearest to farthest)
    private void getNearbyStores(ParseGeoPoint currentLocation, int limit) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereNear(LocalInkUser.KEY_GEO_LOCATION, currentLocation);
        // Only get the bookstores, not the readers
        query.whereEqualTo(LocalInkUser.KEY_IS_BOOKSTORE, true);
        query.setLimit(limit);
        final List<ParseUser> stores = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting nearby bookstores: " + e.getMessage(), e);
                    return;
                }
                stores.addAll(users);
                getRecommendations(stores);
            }
        });
    }

    // Get the user's last known location
    private void getLastKnownLocation() {
        final ParseGeoPoint currentLocation = new ParseGeoPoint();

        // Ask for permission to access current location if they aren't already granted
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }

        // Get the current location and put the latitude and longitude into the ParseGeoPoint
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentLocation.setLatitude(location.getLatitude());
                            currentLocation.setLongitude(location.getLongitude());
                            getNearbyStores(currentLocation, 5);
                        } else {
                            Log.e(TAG, "Current user's location could not be found!");
                        }
                    }
                });
    }

    private boolean matchesGenre(Book book) {
        try {
            List<String> genres = user.getGenrePreferences();
            for (String genre : genres) {
                if (genre.equals(book.getGenre())) {
                    return true;
                }
            }
            return false;
        } catch (ParseException e) {
            Log.e(TAG, "Error retrieving the genre of book " + book.getTitle(), e);
        }
        return false;
    }

    private boolean matchesAge(Book book) {
        try {
            List<String> ageRanges = user.getAgePreferences();
            for (String ageRange : ageRanges) {
                if (ageRange.equals(book.getAgeRange())) {
                    return true;
                }
            }
            return false;
        } catch (ParseException e) {
            Log.e(TAG, "Error retrieving the age range of book " + book.getTitle(), e);
        }
        return false;
    }

}