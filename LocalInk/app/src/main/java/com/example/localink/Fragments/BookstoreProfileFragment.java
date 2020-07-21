package com.example.localink.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.localink.EditBookstoreProfileActivity;
import com.example.localink.LoginActivity;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class BookstoreProfileFragment extends Fragment {

    private static final String TAG = "BookstoreProfileFragmnt";
    FloatingActionButton fabSave;
    ImageView ivProfileImage;
    TextView tvName;
    TextView tvAddress;
    MaterialButton btnLogout;

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
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnLogout = view.findViewById(R.id.btnLogout);

        final LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());

        populateViews(user);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EditBookstoreProfileActivity.class);
                i.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
                startActivity(i);
            }
        });

        // When the user taps the log out button, log the current user out of Parse
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Activity activity = getActivity();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                activity.finish();
            }
        });
    }

    private void populateViews(LocalInkUser user) {

        tvAddress.setText("Address: " + user.getLocation());
        tvName.setText(user.getName());

        ParseFile profileImage = user.getProfileImage();
        if (profileImage != null) {
            Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                populateViews(new LocalInkUser(user));
            }
        });

    }
}