package com.example.localink.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.localink.EditBookstoreProfileActivity;
import com.example.localink.Models.Bookstore;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class BookstoreProfileFragment extends Fragment {

    private static final String TAG = "BookstoreProfileFragmnt";
    FloatingActionButton fabSave;
    ImageView ivProfileImage;
    TextView tvName;
    TextView tvAddress;

    public BookstoreProfileFragment() {
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
        return inflater.inflate(R.layout.fragment_bookstore_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views in the bookstore profile screen
        fabSave = view.findViewById(R.id.fabEdit);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);
        tvAddress = view.findViewById(R.id.tvAddress);

        LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());
        ParseObject store = user.getBookstore();

        tvAddress.setText("Address: " + user.getLocation());

        store.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject bookstore, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting bookstore from Parse: " + e.getMessage());
                }
                // Set name using info in bookstore
                tvName.setText(bookstore.getString(Bookstore.KEY_NAME));
            }
        });


        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EditBookstoreProfileActivity.class);
                startActivity(i);
            }
        });
    }
}