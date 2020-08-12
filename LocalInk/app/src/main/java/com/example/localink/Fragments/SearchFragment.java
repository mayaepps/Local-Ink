package com.example.localink.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localink.Activities.BookDetailsActivity;
import com.example.localink.Activities.BookstoreDetailsActivity;
import com.example.localink.Activities.MainActivity;
import com.example.localink.Adapters.SearchAdapter;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private List<ParseObject> searchedObjects;
    private RecyclerView rvSearch;
    private SearchAdapter searchAdapter;
    private SearchView svBooks;

    private List<ParseObject> objectsContaining = new ArrayList<>();

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

        searchedObjects = new ArrayList<>();

        SearchAdapter.OnClickListener clickListener= new SearchAdapter.OnClickListener() {
            @Override
            public void onBookClick(View view, int position) {
                Intent intent = new Intent(getContext(), BookDetailsActivity.class);
                intent.putExtra(Book.class.getSimpleName(), searchedObjects.get(position));
                intent.putExtra(BookshelfFragment.class.getSimpleName(), true);
                getActivity().startActivityForResult(intent, MainActivity.BOOK_DETAILS_INTENT_CODE);
            }

            @Override
            public void onBookstoreClick(View view, int position) {
                Intent intent = new Intent(getContext(), BookstoreDetailsActivity.class);
                intent.putExtra(ParseUser.class.getSimpleName(), searchedObjects.get(position));
                getActivity().startActivity(intent);
            }
        };


        // Get recycler view and set search adapter
        rvSearch = view.findViewById(R.id.rvSearch);
        searchAdapter = new SearchAdapter(getContext(), searchedObjects, clickListener);
        rvSearch.setAdapter(searchAdapter);

        //set layout manager on recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSearch.setLayoutManager(linearLayoutManager);

        // Set a query listener for when the user taps search
        svBooks = view.findViewById(R.id.svBooks);
        svBooks.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchedObjects.clear();
                if (query.length() == 0) {
                    searchAdapter.notifyDataSetChanged();
                } else {
                    queryBooks(query);
                }

                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchedObjects.clear();
                if (newText.length() == 0) {
                    searchAdapter.notifyDataSetChanged();
                } else if (newText.length() == 1) {
                    objectsContaining.clear();
                    queryBooks(newText);
                    queryBookstores(newText);
                } else {
                    searchBooksStartingWith(newText);
                }
                return true;
            }
        });
    }

    // Then I search the books that queryBooksStartingWith just got when book text is added
    // Check all words in the title and all the author's names
    private void searchBooksStartingWith(String newText) {

        objectsLoop: for (ParseObject object : objectsContaining) {

            if (object instanceof Book) {
                Book book = (Book) object;
                String[] tokenizedTitle = book.getTitle().split(" ");
                String[] tokenizedAuthor = book.getAuthor().split(" ");

                for (String word : tokenizedTitle) {
                    if (word.startsWith(newText)) {
                        searchedObjects.add(book);
                        // Don't go on to check the name if this book is being added to the list
                        continue objectsLoop;
                    }
                }

                for (String name : tokenizedAuthor) {
                    if (name.startsWith(newText)) {
                        searchedObjects.add(book);
                        continue objectsLoop;
                    }
                }

            } else if (object instanceof ParseUser) {
                LocalInkUser user = new LocalInkUser((ParseUser) object);
                String[] tokenizedBookstoreName = user.getName().split(" ");
                for (String word : tokenizedBookstoreName) {
                    if (word.startsWith(newText)) {
                        searchedObjects.add(object);
                        continue objectsLoop;
                    }
                }
            }

            searchAdapter.notifyDataSetChanged();
        }
    }

    // First I query Parse to get all the books with titles/authors starting with that letter
    // (whereStartsWith is apparently very expensive, so I am trying to only use it sparingly)
    private void queryBooks(final String query) {

        // query for books with query in title
        ParseQuery<Book> bookSearchQuery = ParseQuery.getQuery(Book.class);
        bookSearchQuery.whereContains(Book.KEY_TITLE, query);

        // query for books with query in author
        ParseQuery<Book> authorSearchQuery = ParseQuery.getQuery(Book.class);
        authorSearchQuery.whereContains(Book.KEY_AUTHOR, query);

        //Put the queries into a list of queries
        List<ParseQuery<Book>> queries = new ArrayList<>();
        queries.add(bookSearchQuery);
        queries.add(authorSearchQuery);

        // Gets results that satisfy either query in the list
        ParseQuery<Book> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<Book>() {
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

               objectsContaining.addAll(books);
               searchedObjects.addAll(books);
               searchAdapter.notifyDataSetChanged();
           }
       });
    }

    private void queryBookstores(final String query) {
        // query for books with query in title
        ParseQuery<ParseUser> bookstoreSearchQuery = ParseQuery.getQuery(ParseUser.class);
        bookstoreSearchQuery.whereEqualTo(LocalInkUser.KEY_IS_BOOKSTORE, true);
        bookstoreSearchQuery.whereContains(LocalInkUser.KEY_NAME, query);

        bookstoreSearchQuery.findInBackground(new FindCallback<ParseUser>() {
          @Override
          public void done(List<ParseUser> users, ParseException e) {
              objectsContaining.addAll(users);
              searchedObjects.addAll(users);
              searchAdapter.notifyDataSetChanged();
          }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.BOOK_DETAILS_INTENT_CODE && resultCode == RESULT_OK) {
            boolean ifBookAddedToWishlist = data.getBooleanExtra(MainActivity.ADDED_TO_WISHLIST, false);
            if (ifBookAddedToWishlist) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.setWishlistRefresh(true);
                    mainActivity.setMapRefresh(true);
                }
            }
        }
    }

    // When the fragment is hidden, remove the previous searches
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            searchedObjects.clear();
            searchAdapter.notifyDataSetChanged();
            svBooks.setQuery("", false);

        }
    }
}