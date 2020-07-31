package com.example.localink.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.localink.Activities.BookstoreMainActivity;
import com.example.localink.Clients.BookClient;
import com.example.localink.Models.Book;
import com.example.localink.R;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;

public class AddBookFragment extends Fragment {

    private static final String TAG = "AddBookFragment";

    private BookClient client;

    EditText etSearchIsbn;
    MaterialButton btnSearchIsbn;
    EditText etTitle;
    EditText etAuthor;
    EditText etIsbn;
    EditText etSynopsis;
    EditText etCover;
    Spinner spnrGenre;
    Spinner spnrAgeRange;
    MaterialButton btnCreate;

    public AddBookFragment() {
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
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearchIsbn = view.findViewById(R.id.etSearchIsbn);
        btnSearchIsbn = view.findViewById(R.id.btnSearchIsbn);

        etTitle = view.findViewById(R.id.etTitle);
        etAuthor = view.findViewById(R.id.etAuthor);
        etIsbn = view.findViewById(R.id.etIsbn);
        etSynopsis = view.findViewById(R.id.etSynopsis);
        btnCreate = view.findViewById(R.id.btnCreate);
        etCover = view.findViewById(R.id.etCover);
        spnrGenre = view.findViewById(R.id.spnrGenre);
        spnrGenre.setPrompt("Select your favorite genre!");
        spnrAgeRange = view.findViewById(R.id.spnrAgeRange);
        spnrAgeRange.setPrompt("Select your favorite age range!");
        btnCreate = view.findViewById(R.id.btnCreate);

        btnSearchIsbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidISBN(etSearchIsbn.getText().toString())) {
                    queryBook(etSearchIsbn.getText().toString());
                    etSearchIsbn.setText("");
                } else {
                    Toast.makeText(getContext(), "Invalid ISBN", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create a new book according to the specifications
                Book book = new Book();
                book.setTitle(etTitle.getText().toString());
                book.setAuthor(etAuthor.getText().toString());
                if (isValidISBN(etIsbn.getText().toString())) {
                    book.setIsbn(etIsbn.getText().toString());
                } else {
                    Toast.makeText(getContext(), "Invalid ISBN", Toast.LENGTH_SHORT).show();
                    return;
                }
                book.setSynopsis(etSynopsis.getText().toString());
                book.setCover(etCover.getText().toString());
                book.setGenre(spnrGenre.getSelectedItem().toString());
                book.setAgeRange(spnrAgeRange.getSelectedItem().toString());
                book.setBookstore(ParseUser.getCurrentUser());

                // Save the new book to Parse
                book.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error saving book to Parse: ", e);
                            Toast.makeText(getContext(), "Could not create book: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            // Clear the fields when the book is saved
                            etTitle.setText("");
                            etAuthor.setText("");
                            etIsbn.setText("");
                            etSynopsis.setText("");
                            etCover.setText("");
                            spnrGenre.setSelection(0);
                            spnrGenre.setPrompt("Select your favorite genre!");
                            spnrAgeRange.setSelection(0);
                            spnrAgeRange.setPrompt("Select your favorite age range!");

                            // Go to the bookshelf fragment to see the book you just created
                            ((BookstoreMainActivity) getActivity()).getBottomNavigation().setSelectedItemId(R.id.action_bookshelf);
                        }
                    }
                });
            }
        });
    }

    // Populate the edit text views so the user can make any changes to Google Books' response
    private void populateViews(Book book) {
        try {
            etTitle.setText(book.getTitle());
            etAuthor.setText(book.getAuthor());
            etSynopsis.setText(book.getSynopsis());
            etCover.setText(book.getCover());
            etIsbn.setText(book.getIsbn());
            etCover.setText(book.getCover());
            Toast.makeText(getContext(), "Please set the genre and age range and confirm the " +
                    "filled in fields are correct", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Log.e(TAG, "Could not populate views with book object: " + e.getMessage());
        }
    }

    private void queryBook(String isbn) {
        client = new BookClient();
        client.getBookByIsbn(isbn, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON response) {
                try {
                    if (response != null && statusCode == 200 && response.jsonObject.getInt("totalItems") > 0) {

                        JSONArray items = response.jsonObject.getJSONArray("items");
                        JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");

                        Book book = Book.fromJSON(volumeInfo);
                        populateViews(book);
                    } else {
                        Toast.makeText(getContext(), "Error finding a book with that ISBN", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String responseString, Throwable throwable) {
                // Handle failed request here
                Log.e(TAG,
                        "Request failed with code " + statusCode + ". Response message: " + responseString);
            }
        });
    }

    private boolean isValidISBN(String isbn) {
        return (!isbn.isEmpty() && isbn.length() >= 10 && isbn.length() <= 13);
    }
}