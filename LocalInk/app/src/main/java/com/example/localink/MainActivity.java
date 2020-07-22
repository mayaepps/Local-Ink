package com.example.localink;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.localink.Fragments.ProfileFragment;
import com.example.localink.Fragments.RecommendationsFragment;
import com.example.localink.Fragments.WishlistFragment;
import com.example.localink.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding binding;
    final FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = recommendationsFragment;
                        break;
                    case R.id.action_wishlist:
                        fragment = wishlistFragment;
                        break;
                    case R.id.action_profile:
                        fragment = profileFragment;
                        break;
                    default:
                        fragment = recommendationsFragment;
                        break;
                }
                // Switch out the frame layout with the specified fragment
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
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

}