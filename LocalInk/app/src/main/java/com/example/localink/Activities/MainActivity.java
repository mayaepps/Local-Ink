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
import com.example.localink.Fragments.WishlistContainerFragment;
import com.example.localink.Fragments.WishlistFragment;
import com.example.localink.R;
import com.example.localink.Utils.FragmentUtils;
import com.example.localink.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    private boolean wishlistRefresh = false;


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
        final Fragment wishlistContainerFragment = new WishlistContainerFragment();
        final Fragment profileFragment = new ProfileFragment();

        // The user can tap on the icons/items in the bottom navigation view to switch fragments
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.action_home:
                        FragmentUtils.displayFragment(fragmentManager, recommendationsFragment, wishlistContainerFragment, profileFragment);
                        return true;
                    case R.id.action_wishlist:
                        FragmentUtils.displayFragment(fragmentManager, wishlistContainerFragment, recommendationsFragment, profileFragment);
                        return true;
                    case R.id.action_profile:
                        FragmentUtils.displayFragment(fragmentManager, profileFragment, wishlistContainerFragment, recommendationsFragment);
                        return true;
                    default:
                        FragmentUtils.displayFragment(fragmentManager, recommendationsFragment, wishlistContainerFragment, profileFragment);
                        return true;
                }
            }
        });

        // Set default selection so when the app loads for the first time, it will have the recommendations/home fragment loaded
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);

    }

    public AVLoadingIndicatorView getAVLoadingIndivatorView() {
        return binding.avi;
    }

    public boolean isWishlistRefresh() {
        return wishlistRefresh;
    }

    public void setWishlistRefresh(boolean wishlistRefresh) {
        this.wishlistRefresh = wishlistRefresh;
    }
}