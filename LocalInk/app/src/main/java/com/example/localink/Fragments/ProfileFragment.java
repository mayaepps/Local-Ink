package com.example.localink.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.localink.Activities.EditProfileActivity;
import com.example.localink.Activities.LoginActivity;
import com.example.localink.Activities.MainActivity;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.example.localink.Utils.ChipUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;


public class ProfileFragment extends Fragment {

    FloatingActionButton fabEdit;
    ImageView ivProfileImage;
    TextView tvName;
    TextView tvUsername;
    TextView tvGenre;
    TextView tvAgeRange;
    MaterialButton btnLogout;

    public ProfileFragment() {
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        // Find views in profile page
        fabEdit = view.findViewById(R.id.fabEdit);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvGenre = view.findViewById(R.id.tvGenre);
        tvAgeRange = view.findViewById(R.id.tvAgeRange);

        populateViews(ParseUser.getCurrentUser());

        // Go to edit profile screen when the floating action button (edit button) is tapped
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EditProfileActivity.class);
                final LocalInkUser user = new LocalInkUser(ParseUser.getCurrentUser());
                i.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
                startActivity(i);
            }
        });

    }

    // Set views using info in user
    private void populateViews(ParseUser parseUser) {

        ((MainActivity) getActivity()).getAVLoadingIndivatorView().smoothToShow();

        final LocalInkUser user = new LocalInkUser(parseUser);
        tvName.setText(user.getName());
        tvUsername.setText("Username: " + user.getUser().getUsername());

        ChipGroup cgAgeRange = getActivity().findViewById(R.id.cgAgeRange);
        ChipGroup cgGenre = getActivity().findViewById(R.id.cgGenre);

        ChipUtils.setUpChips(getContext(), cgAgeRange, user.getAgePreferences(), false);
        ChipUtils.setUpChips(getContext(), cgGenre, user.getGenrePreferences(), false);

        ParseFile profileImage = user.getProfileImage();
        if (profileImage != null) {
            Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
        }

        ((MainActivity) getActivity()).getAVLoadingIndivatorView().smoothToHide();
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                populateViews(user);
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