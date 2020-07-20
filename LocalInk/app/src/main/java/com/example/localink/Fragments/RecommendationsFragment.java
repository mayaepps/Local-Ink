package com.example.localink.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.localink.Adapters.BooksAdapter;
import com.example.localink.BookDetailsActivity;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class RecommendationsFragment extends Fragment {

    private static final String TAG = "RecommendationsFragment";
    private RecyclerView rvBooks;
    private BooksAdapter adapter;
    private List<Book> allBooks;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //Instantiate my OnClickListener from the interface in BooksAdapter
        BooksAdapter.OnClickListener clickListener = new BooksAdapter.OnClickListener() {

            // If clicked, the book item should open  a detail view activity for the book
            @Override
            public void onClick(int position) {

                Intent i = new Intent(getContext(), BookDetailsActivity.class);
                i.putExtra(Book.class.getSimpleName(), allBooks.get(position));
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
        allBooks = new ArrayList<>(); // Have to initialize allBooks before passing it into the adapter
        adapter = new BooksAdapter(getContext(), allBooks, clickListener);
        rvBooks.setAdapter(adapter);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));

        // Old way of populating feed; just here until I get the recommendations feature working
        queryBooks();

        // New recommendation system (in progress)
        List<ParseUser> nearbyBookstores = getNearbyStores(10);
    }

    // Queries Parse for all the book objects and adds them to allBooks/the adapter
    private void queryBooks() {
        ParseQuery<Book> query = ParseQuery.getQuery(Book.class);
        query.include(Book.KEY_BOOKSTORE);
        query.findInBackground(new FindCallback<Book>() {
            @Override
            public void done(List<Book> books, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting books from Parse: " + e.getMessage());
                    return;
                }

                allBooks.clear();
                allBooks.addAll(books);
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Get the bookstores that are near the currently logged in user (from nearest to farthest)
    private List<ParseUser> getNearbyStores(int limit) {
        LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());

        ParseGeoPoint userLocation = user.getGeoLocation();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereNear(LocalInkUser.KEY_GEO_LOCATION, userLocation);
        // Only get the bookstores, not the readers
        query.whereEqualTo(LocalInkUser.KEY_IS_BOOKSTORE, true);
        // Top 10 for now
        query.setLimit(limit);
        final List<ParseUser> stores = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting nearby bookstores: " + e.getMessage(), e);
                } else {
                    stores.addAll(users);
                }
            }
        });
        return stores;
    }
}