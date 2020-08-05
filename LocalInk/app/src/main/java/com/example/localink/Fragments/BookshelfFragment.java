package com.example.localink.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.localink.Activities.BookDetailsActivity;
import com.example.localink.Activities.BookstoreMainActivity;
import com.example.localink.Activities.MainActivity;
import com.example.localink.Adapters.BooksAdapter;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class BookshelfFragment extends Fragment {

    private static final String TAG = "BookshelfFragment";
    private RecyclerView rvBooks;
    private BooksAdapter adapter;
    private List<Book> storeBooks;

    public BookshelfFragment() {
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
        return inflater.inflate(R.layout.fragment_bookshelf, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BooksAdapter.OnClickListener clickListener = new BooksAdapter.OnClickListener() {

            // If clicked, the book item should open  a detail view activity for the book
            @Override
            public void onClick(int position, View view) {
                // Fire an intent when a contact is selected
                Intent i = new Intent(getContext(), BookDetailsActivity.class);
                i.putExtra(Book.class.getSimpleName(), storeBooks.get(position));
                i.putExtra(BookshelfFragment.class.getSimpleName(), false);
                startActivity(i);
            }

            @Override
            public void onLongClick(int position) { }
        };

        // Set up recycler view with the adapter and linear layout
        rvBooks = view.findViewById(R.id.rvBooks);
        storeBooks = new ArrayList<>(); // Have to initialize storeBooks before passing it into the adapter
        adapter = new BooksAdapter(getContext(), storeBooks, clickListener);
        rvBooks.setAdapter(adapter);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBooks.setItemAnimator(new SlideInUpAnimator());

        // Set up swipe listener that removes the swiped book
        ItemTouchHelper touchHelper = createTouchHelper();
        touchHelper.attachToRecyclerView(rvBooks);

        queryBooks();
    }


    // Query all the books whose bookstore is the same as the currently logged in bookstore (current user)
    private void queryBooks() {
        ParseUser.getCurrentUser().fetchInBackground();
        LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());

        // Query for books from this bookstore
        ParseQuery<Book> query = ParseQuery.getQuery(Book.class);
        query.whereEqualTo(Book.KEY_BOOKSTORE, user.getUser());
        query.findInBackground(new FindCallback<Book>() {
            @Override
            public void done(List<Book> books, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting books from Parse: " + e.getMessage());
                    return;
                }

                // add to the list of storeBooks and notify adapter
                storeBooks.clear();
                storeBooks.addAll(books);
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Creates and returns an ItemTouchHelper that listens for swipes and deletes the book that has been swiped
    private ItemTouchHelper createTouchHelper() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // When the user swipes to the left, delete the book from Parse and remove it from the screen
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Book book = storeBooks.get(position);
                book.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Could not delete book: " + e.getMessage(), e);
                        } else {
                            Toast.makeText(getContext(), "Removing " + book.getTitle() + " from bookshelf", Toast.LENGTH_SHORT).show();
                            storeBooks.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                    }
                });
            }
        });
        return helper;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        BookstoreMainActivity mainActivity = (BookstoreMainActivity) getActivity();
        if (!hidden && mainActivity.isBookshelfRefresh()) {
            // refresh the screen -- query parse again
            queryBooks();

            mainActivity.setBookshelfRefresh(false);
        }
    }
}