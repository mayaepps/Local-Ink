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
    private RecyclerView rvBooks;
    private BooksAdapter adapter;
    private List<Book> recommendedBooks;
    private List<Book> otherBooks;
    private LocalInkUser user;
    private ParseGeoPoint currentLocation;
    private FusedLocationProviderClient fusedLocationClient;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLastKnownLocation();
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
        recommendedBooks = new ArrayList<>(); // Have to initialize allBooks before passing it into the adapter
        adapter = new BooksAdapter(getContext(), recommendedBooks, clickListener);
        rvBooks.setAdapter(adapter);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));

        getRecommendations();

    }

    // New recommendation system (in progress)
    private void getRecommendations() {
        otherBooks = new ArrayList<>();
        // Get the 5 closest stores and get their available books
        List<ParseUser> nearbyBookstores = getNearbyStores(5);
        for (ParseUser store : nearbyBookstores) {
            queryBooks(store);
        }

        List<Book> booksToRemove = new ArrayList<>();
        for (Book book : otherBooks) {
            try {
                if (book.getAgeRange().equals(user.getAgePreference())) {
                    recommendedBooks.add(book);
                    booksToRemove.add(book);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error retrieving details from " + book.getTitle(), e);
            }

            if (recommendedBooks.size() > 10 || otherBooks.size() == 0) {
                return;
            }
        }
        otherBooks.removeAll(booksToRemove);

    }

    // Get all the books offered at the given store
    private void queryBooks(final ParseUser store) {
        ParseQuery<Book> query = ParseQuery.getQuery(Book.class);
        query.include(Book.KEY_BOOKSTORE);
        query.whereEqualTo(Book.KEY_BOOKSTORE, store);
        query.findInBackground(new FindCallback<Book>() {
            @Override
            public void done(List<Book> queriedBooks, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error retrieving books from " + store.getUsername(), e);
                    return;
                }

                otherBooks.addAll(queriedBooks);

                for (Book book : queriedBooks) {
                    if (matchesPreferences(book)) {
                        recommendedBooks.add(book);
                        otherBooks.remove(book);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private boolean matchesPreferences(Book book) {
        try {
            return (book.getAgeRange().equals(user.getAgePreference())
                    && book.getGenre().equals((user.getGenrePreference())));
        } catch (ParseException e) {
            Log.e(TAG, "Error retrieving the age range and genre of book " + book.getTitle());
        }
        return false;
    }

    // Get the bookstores that are near the currently logged in user
    // Returns a list of bookstores of length limit (from nearest to farthest)
    private List<ParseUser> getNearbyStores(int limit) {

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereNear(LocalInkUser.KEY_GEO_LOCATION, currentLocation);
        // Only get the bookstores, not the readers
        query.whereEqualTo(LocalInkUser.KEY_IS_BOOKSTORE, true);
        // Top 10 for now
        query.setLimit(limit);
        final List<ParseUser> stores = new ArrayList<>();
        try {
            List<ParseUser> users = query.find();
            stores.addAll(users);
        } catch (ParseException e) {
            Log.e(TAG, "Error getting nearby bookstores: " + e.getMessage(), e);
        }

        return stores;
    }

    // Get the user's last known location
    private void getLastKnownLocation() {
        currentLocation = new ParseGeoPoint();

        // Ask for permission to access current location if they aren't already granted
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }

        // Get the current location and put the latitude and longitude into the ParseGeoPoint
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public synchronized void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentLocation.setLatitude(location.getLatitude());
                            currentLocation.setLongitude(location.getLongitude());
                            Log.d(TAG, currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                        } else {
                            Log.e(TAG, "Current user's location could not be found!");
                        }

                    }
                });
    }

    // When the request for permission comes back, get the user's recommendations
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            getRecommendations();
        }
    }
}