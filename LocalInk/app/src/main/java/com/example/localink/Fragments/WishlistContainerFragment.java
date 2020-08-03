package com.example.localink.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.localink.Models.Book;
import com.example.localink.R;
import com.example.localink.databinding.FragmentRecommendationsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.localink.Utils.FragmentUtils.displayFragment;


public class WishlistContainerFragment extends Fragment {

    private FragmentManager childFragmentManager;

    public WishlistContainerFragment() {
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
        return inflater.inflate(R.layout.fragment_wishlist_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTabs();
    }

    // Set up the logic for switching between the wishlist and map fragment when their tabs are selected
    private void setupTabs() {

        TabLayout tabLayout = getView().findViewById(R.id.tabLayoutWishlist);

        childFragmentManager = getChildFragmentManager();

        // Create the different fragments the user can see
        final Fragment wishlistFragment = new WishlistFragment();
        final Fragment mapsFragment = new MapsFragment();

        FragmentTransaction ft = childFragmentManager.beginTransaction();
        ft.add(R.id.flContainer, wishlistFragment, wishlistFragment.getClass().getSimpleName());
        ft.add(R.id.flContainer, mapsFragment, mapsFragment.getClass().getSimpleName());

        // Add an on tab selected listener to switch the fragments when the tabs are selected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                FragmentManager cfm = getChildFragmentManager();

                switch (tab.getPosition()) {
                    case 0:
                        displayFragment(cfm, wishlistFragment, mapsFragment);
                        break;
                    case 1:
                        displayFragment(cfm, mapsFragment, wishlistFragment);
                        break;
                    default:
                        displayFragment(cfm, wishlistFragment, mapsFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // TODO: bug - doesn't work on first load (it shows no fragment), but works if I go to the other tab and back
        tabLayout.getTabAt(1).select();
        tabLayout.getTabAt(0).select();
    }
}