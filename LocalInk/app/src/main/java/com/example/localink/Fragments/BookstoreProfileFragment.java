package com.example.localink.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.localink.Activities.EditBookstoreProfileActivity;
import com.example.localink.Activities.LoginActivity;
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

    FloatingActionButton fabSave;
    ImageView ivProfileImage;
    TextView tvName;
    TextView tvAddress;
    MaterialButton btnLogout;
    MaterialButton btnWebsite;

    public BookstoreProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        // Find views in the bookstore profile screen
        fabSave = view.findViewById(R.id.fabEdit);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);
        tvAddress = view.findViewById(R.id.tvAddress);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnWebsite = view.findViewById(R.id.btnWebsite);

        final LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());

        populateViews(user);

        // Open the website if the user clicks on the website text button
        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());
                String url = user.getWebsite();

                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                Uri uri = Uri.parse(url);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "No application can handle this request."
                            + " Please install a web browser",  Toast.LENGTH_LONG).show();
                }
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EditBookstoreProfileActivity.class);
                i.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
                startActivity(i);
            }
        });

    }

    private void populateViews(LocalInkUser user) {

        tvAddress.setText("Address: " + user.getAddress());
        tvName.setText(user.getName());
        btnWebsite.setText(user.getWebsite());

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.miLogOut) {
            // Log out has been tapped, log the current user out of Parse
            ParseUser.logOut();
            Activity activity = getActivity();
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
            activity.finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}