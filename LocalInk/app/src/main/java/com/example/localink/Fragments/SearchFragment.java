package com.example.localink.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.localink.Adapters.SearchAdapter;
import com.example.localink.Models.Book;
import com.example.localink.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private List<Book> searchedBooks;
    private RecyclerView rvSearch;
    private SearchAdapter searchAdapter;

    public SearchFragment() {
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchedBooks = new ArrayList<>();

        // Get recycler view and set search adapter
        rvSearch = view.findViewById(R.id.rvSearch);
        searchAdapter = new SearchAdapter(getContext(), searchedBooks);
        rvSearch.setAdapter(searchAdapter);

        //set layout manager on recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSearch.setLayoutManager(linearLayoutManager);

        // Set a query listener for when the user taps search
        SearchView svBooks = view.findViewById(R.id.svBooks);
        svBooks.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchedBooks.clear();
                queryBooks(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void queryBooks(final String query) {

        // query for books
        ParseQuery<Book> bookSearchQuery = ParseQuery.getQuery(Book.class);

        bookSearchQuery.whereStartsWith(Book.KEY_TITLE, query);

        bookSearchQuery.findInBackground(new FindCallback<Book>() {
            @Override
            public void done(List<Book> books, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get search results: " + e.getMessage());
                    Toast.makeText(getContext(), "Error getting search results for query " + query + ", please try again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (books.size() == 0) {
                    Toast.makeText(getContext(), "No results found for search query: " + query, Toast.LENGTH_SHORT).show();
                    return;
                }

                searchedBooks.addAll(books);
                searchAdapter.notifyDataSetChanged();
            }
        });
    }

    // When the fragment is hidden, remove the previous searches
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            searchedBooks.clear();
            searchAdapter.notifyDataSetChanged();
        }
    }
}