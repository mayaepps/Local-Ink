package com.example.localink.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.localink.Fragments.ProfileFragment;
import com.example.localink.Fragments.RecommendationsFragment;
import com.example.localink.Fragments.WishlistFragment;
import com.example.localink.R;
import com.example.localink.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportPostponeEnterTransition();

        // view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        // Create the different fragments the user can see
        final Fragment recommendationsFragment = new RecommendationsFragment();
        final Fragment wishlistFragment = new WishlistFragment();
        final Fragment profileFragment = new ProfileFragment();

        // The user can tap on the icons/items in the bottom navigation view to switch fragments
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        displayFragment(recommendationsFragment, wishlistFragment, profileFragment);
                        return true;
                    case R.id.action_wishlist:
                        displayFragment(wishlistFragment, recommendationsFragment, profileFragment);
                        return true;
                    case R.id.action_profile:
                        displayFragment(profileFragment, wishlistFragment, recommendationsFragment);
                        return true;
                    default:
                        displayFragment(recommendationsFragment, wishlistFragment, profileFragment);
                        return true;
                }

            }
        });

        // If coming from BookDetailsActivity, go to the wishlist (a book was just added to the wishlist)
        int layoutId = R.id.action_home;
        Intent i = getIntent();
        if (i.hasExtra(Integer.class.getSimpleName())) {
            layoutId = i.getIntExtra(Integer.class.getSimpleName(), R.id.action_home);
        }
        // Set default selection so when the app loads for the first time, it will have the recommendations/home fragment loaded
        binding.bottomNavigation.setSelectedItemId(layoutId);

    }

    // Show fragmentA and hide all the other fragments
    private void displayFragment(Fragment fragmentA, Fragment fragmentB, Fragment fragmentC) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentA.isAdded()) { // if the fragment is already in container
            ft.show(fragmentA);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.flContainer, fragmentA, fragmentA.getClass().getSimpleName());
        }

        if (fragmentB.isAdded()) {
            ft.hide(fragmentB);
        }

        if (fragmentC.isAdded()) {
            ft.hide(fragmentC);
        }

        ft.commit();
    }

    public AVLoadingIndicatorView getAVLoadingIndivatorView() {
        return binding.avi;
    }
}