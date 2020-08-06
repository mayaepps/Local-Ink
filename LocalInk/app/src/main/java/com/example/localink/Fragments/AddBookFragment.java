package com.example.localink.Fragments;

import android.Manifest;
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
import com.example.localink.Activities.MainActivity;
import com.example.localink.Clients.BookClient;
import com.example.localink.Models.Book;
import com.example.localink.R;
import com.example.localink.Utils.ChipUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Headers;

public class AddBookFragment extends Fragment {

    private static final String TAG = "AddBookFragment";

    private BookClient client;

    private ZXingScannerView scannerView;

    private boolean isScannerOn = false;

    EditText etSearchIsbn;
    MaterialButton btnSearchIsbn;
    EditText etTitle;
    EditText etAuthor;
    EditText etIsbn;
    EditText etSynopsis;
    EditText etCover;
    ChipGroup cgGenres;
    Spinner spnrAgeRange;
    MaterialButton btnCreate;
    MaterialButton btnScan;

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

        // Find all views
        etSearchIsbn = view.findViewById(R.id.etSearchIsbn);
        btnSearchIsbn = view.findViewById(R.id.btnSearchIsbn);
        btnScan = view.findViewById(R.id.btnScan);
        etTitle = view.findViewById(R.id.etTitle);
        etAuthor = view.findViewById(R.id.etAuthor);
        etIsbn = view.findViewById(R.id.etIsbn);
        etSynopsis = view.findViewById(R.id.etSynopsis);
        btnCreate = view.findViewById(R.id.btnCreate);
        etCover = view.findViewById(R.id.etCover);
        cgGenres = view.findViewById(R.id.cgGenre);
        spnrAgeRange = view.findViewById(R.id.spnrAgeRange);
        btnCreate = view.findViewById(R.id.btnCreate);
        scannerView = view.findViewById(R.id.zxing);

        scannerView.setVisibility(View.GONE);
        ChipUtils.setUpChips(getContext(), cgGenres, getResources().getStringArray(R.array.genres_array), false);
        spnrAgeRange.setPrompt("Select your favorite age range!");

        // When the scan button is tapped, request permission to use the camera
        // If permission is granted, start the scanner and set the result handler so when the scanner
        // finds a barcode, it enters the scanned value into the isbn search bar
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(final PermissionGrantedResponse response) {

                                // If the scanner is already on, tapping the button should close/hide the camera/scanner
                                // and change the button icon to the collapsed up-arrow icon
                                if (isScannerOn) {
                                    btnScan.setIcon(getContext().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
                                    scannerView.stopCamera();
                                    scannerView.stopCameraPreview();
                                    scannerView.setVisibility(View.GONE);
                                    isScannerOn = false;
                                } else {

                                    // If the scanner is not already on, tapping the button should open/show the camera/scanner
                                    // and change the button icon to the expanded down-arrow icon
                                    btnScan.setIcon(getContext().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));
                                    isScannerOn = true;

                                    scannerView.setVisibility(View.VISIBLE);
                                    scannerView.startCamera();
                                    scannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
                                        @Override
                                        public void handleResult(Result rawResult) {

                                            Toast.makeText(getContext(), "Scanned ISBN: " + rawResult.getText(), Toast.LENGTH_SHORT).show();
                                            etSearchIsbn.setText(rawResult.getText());
                                            // Don't just freeze after scanning one barcode
                                            scannerView.startCamera();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        })
                        .check();
            }
        });


        // When the search isbn button is tapped, make sure the isbn is valid (10 or 13 chars)
        // and query Google Books API for that isbn
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
                book.setGenres(ChipUtils.getChipSelections(cgGenres));
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
                            spnrAgeRange.setSelection(0);
                            spnrAgeRange.setPrompt("Select your favorite age range!");
                            cgGenres.clearCheck();

                            // Go to the bookshelf fragment to see the book you just created
                            // (and let it know it should refresh to get newly created book)
                            BookstoreMainActivity mainActivity = (BookstoreMainActivity) getActivity();
                            mainActivity.getBottomNavigation().setSelectedItemId(R.id.action_bookshelf);
                            mainActivity.setBookshelfRefresh(true);
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
            Toast.makeText(getContext(), "Please set the genre and age range, and confirm the " +
                    "filled in fields are correct", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Log.e(TAG, "Could not populate views with book object: " + e.getMessage());
        }
    }

    // Make call to Google Books API searching for a book with the given ISBN
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
                    Log.e(TAG, "Invalid JSON format from Google Books ISBN search response ", e);
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

    // Simple check that the ISBN value is valid
    // Old ISBNs (before 2008) were 10 digits, but since then ISBNs are 13 digits
    private boolean isValidISBN(String isbn) {
        return (!isbn.isEmpty() && (isbn.length() == 10 || isbn.length() == 13));
    }

    @Override
    public void onStop() {
        super.onStop();
        scannerView.stopCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }
}