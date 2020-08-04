package com.example.localink.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.localink.Fragments.AddBookFragment;
import com.example.localink.Fragments.BookshelfFragment;
import com.example.localink.Fragments.BookstoreProfileFragment;
import com.example.localink.R;
import com.example.localink.Utils.FragmentUtils;
import com.example.localink.databinding.ActivityBookstoreMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wang.avi.AVLoadingIndicatorView;

import static com.example.localink.Utils.FragmentUtils.displayFragment;

public class BookstoreMainActivity extends AppCompatActivity {

    ActivityBookstoreMainBinding binding;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    private boolean bookshelfRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookstore_main);

        // view binding
        binding = ActivityBookstoreMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        // Create the different fragments the bookstore user can see
        final Fragment addBookFragment = new AddBookFragment();
        final Fragment bookshelfFragment = new BookshelfFragment();
        final Fragment bookstoreProfileFragment = new BookstoreProfileFragment();

        // The bookstore user can tap on the icons/items in the bottom navigation view to switch fragments
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fm = getSupportFragmentManager();

                switch (item.getItemId()) {
                    case R.id.action_add_book:
                        displayFragment(fm, addBookFragment, bookshelfFragment, bookstoreProfileFragment);
                        return true;
                    case R.id.action_bookshelf:
                        displayFragment(fm, bookshelfFragment, addBookFragment, bookstoreProfileFragment);
                        return true;
                    case R.id.action_profile:
                        displayFragment(fm, bookstoreProfileFragment, addBookFragment, bookshelfFragment);
                        return true;
                    default:
                        displayFragment(fm, bookstoreProfileFragment, addBookFragment, bookshelfFragment);
                        return true;
                }

            }
        });
        // Set default selection so when the app loads for the first time, it will have the bookshelf fragment loaded
        binding.bottomNavigation.setSelectedItemId(R.id.action_bookshelf);
    }

    public BottomNavigationView getBottomNavigation() {
        return binding.bottomNavigation;
    }

    public boolean isBookshelfRefresh() {
        return bookshelfRefresh;
    }

    public void setBookshelfRefresh(boolean bookshelfRefresh) {
        this.bookshelfRefresh = bookshelfRefresh;
    }


}
