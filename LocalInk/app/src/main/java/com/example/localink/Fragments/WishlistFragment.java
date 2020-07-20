package com.example.localink.Fragments;

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
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class WishlistFragment extends Fragment {

    private static final String TAG = "WishlistFragment";
    private RecyclerView rvBooks;
    private List<Book> wishlistBooks;
    private BooksAdapter adapter;

    public WishlistFragment() {
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
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up recycler view with the adapter and linear layout
        rvBooks = view.findViewById(R.id.rvBooks);
        wishlistBooks = new ArrayList<>(); // Have to initialize allBooks before passing it into the adapter
        adapter = new BooksAdapter(getContext(), wishlistBooks, null);
        rvBooks.setAdapter(adapter);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));

        getWishlistBooks();
    }

    // Gets the wishlist list stored in ParseUser and save the list to wishlistBooks
    private void getWishlistBooks() {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    LocalInkUser localInkuser = new LocalInkUser(user);
                    List<Book> wishlist = localInkuser.getWishlist();
                    wishlistBooks.clear();
                    wishlistBooks.addAll(wishlist);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error getting wishlist: " + e.getMessage());
                }

            }
        });
    }
}