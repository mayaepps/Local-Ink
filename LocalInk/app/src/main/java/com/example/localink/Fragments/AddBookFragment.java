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

import com.example.localink.Models.Book;
import com.example.localink.R;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AddBookFragment extends Fragment {

    private static final String TAG = "AddBookFragment";
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
                        }
                    }
                });
            }
        });
    }

    private boolean isValidISBN(String isbn) {
        return (!isbn.isEmpty() && isbn.length() >= 10 && isbn.length() <= 13);
    }
}