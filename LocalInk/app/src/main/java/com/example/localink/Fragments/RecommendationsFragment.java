package com.example.localink.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localink.Activities.BookstoreMainActivity;
import com.example.localink.Activities.MainActivity;
import com.example.localink.Adapters.BooksAdapter;
import com.example.localink.Activities.BookDetailsActivity;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class RecommendationsFragment extends Fragment {

    private static final String TAG = "RecommendationsFragment";
    private static final int ACCESS_LOCATION_REQUEST_CODE = 15;
    private static final int MINIMUM_RECS = 10;
    private static final int NUM_INITIAL_STORES = 5;
    private static final int NUM_INITIAL_MILES = 20;
    private RecyclerView rvBooks;
    private BooksAdapter adapter;
    private List<Book> recommendedBooks;
    private List<Book> otherBooks;
    private LocalInkUser user;
    private BottomSheetBehavior bottomSheetBehavior;
    private SeekBar seekbarRadiusStores;
    private SeekBar seekbarNumStores;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean alreadyAddedExploreBooks = false;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommendations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        setUpBottomSheet(view);

        getLastKnownLocation(NUM_INITIAL_STORES, NUM_INITIAL_MILES);

        user = new LocalInkUser(ParseUser.getCurrentUser());

        //Instantiate my OnClickListener from the interface in BooksAdapter
        BooksAdapter.OnClickListener clickListener = new BooksAdapter.OnClickListener() {

            // If clicked, the book item should open  a detail view activity for the book
            @Override
            public void onClick(int position, View view) {
                // Fire an intent when a contact is selected
                Intent i = new Intent(getContext(), BookDetailsActivity.class);
                i.putExtra(Book.class.getSimpleName(), recommendedBooks.get(position));
                i.putExtra(BookshelfFragment.class.getSimpleName(), true);
                startActivity(i);
            }

            // Required by the interface
            @Override
            public void onLongClick(int position) { }
        };

        // Set up recycler view with the adapter and linear layout
        rvBooks = view.findViewById(R.id.rvBooks);
        recommendedBooks = new ArrayList<>(); // Have to initialize recommendedBooks before passing it into the adapter
        adapter = new BooksAdapter(getContext(), recommendedBooks, clickListener);
        rvBooks.setAdapter(adapter);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBooks.setItemAnimator(new SlideInUpAnimator());
    }

    // Set up the initial views and click listener for the bottom sheet that will pop up when the recommendations settings is tapped
    private void setUpBottomSheet(View view) {
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.recSettingsBottomSheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        seekbarNumStores = view.findViewById(R.id.numStoresSeekBar);
        setSeekBar(view, seekbarNumStores, R.id.tvNumStoresSeekBarValue, NUM_INITIAL_STORES);
        seekbarRadiusStores = view.findViewById(R.id.seekBarRadiusStores);
        setSeekBar(view, seekbarRadiusStores, R.id.tvRadiusStoresSeekBarValue, NUM_INITIAL_MILES);

        // Get all the recommendations again when the save button is pressed
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                getLastKnownLocation(seekbarNumStores.getProgress(), seekbarRadiusStores.getProgress());
            }
        });
    }

    private void setSeekBar(View view, SeekBar seekBar, int seekBarValueId, int progress) {
        // Set initial values for the seek bar (scrolling bar to select # of stores)
        seekBar.setProgress(progress);
        final TextView seekBarValue = view.findViewById(seekBarValueId);
        seekBarValue.setText(String.valueOf(progress));

        // Change the text view next to the seek bar to reflect the numeric value of the seek bar when it is changed
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    // Recommends books from the nearby bookstores that fit the user's preferences
    // if there aren't enough perfect matches, it gets partial matches (that fit the age, not the genre)
    private void getRecommendations(List<ParseUser> nearbyBookstores) {
        recommendedBooks.clear();
        otherBooks = new ArrayList<>();

        // Get the books from the closest stores and get their available books
        for (ParseUser store : nearbyBookstores) {
            queryBooks(store);
        }

        removeDuplicateBooks();

        // Add books that perfectly match the user's preferences to the recommendation list
        List<Book> perfectMatches = getPerfectMatchBooks();
        recommendedBooks.addAll(perfectMatches);
        adapter.notifyDataSetChanged();

        // Get books that don't perfectly match the user's preferences but may still interest them
        final List<Book> exploreBooks = getExploreBooks();

        ((MainActivity) getActivity()).getAVLoadingIndivatorView().smoothToHide();

        // Show a snackbar with the option to see other recommended (but not perfect match) books
        rvBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1) && !alreadyAddedExploreBooks) {
                    Snackbar.make(getView(), R.string.get_more_books_snackbar, Snackbar.LENGTH_LONG)
                            .setAction(R.string.get_more_books_snackbar_action, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    recommendedBooks.addAll(exploreBooks);
                                    otherBooks.removeAll(exploreBooks);
                                    adapter.notifyDataSetChanged();
                                    alreadyAddedExploreBooks = true;
                                }
                            }).show();
                }
            }
        });
    }

    // If there aren't enough books that fit the preferences,
    // get the books that match the user's age preferences, but show them other genres
    private List<Book> getExploreBooks() {
        final List<Book> exploreBooks = new ArrayList<>();

        // start with "exploring" related genres (similar genres are defined in Book class)
        if (recommendedBooks.size() < MINIMUM_RECS && otherBooks.size() > 0) {
            List<String> relatedGenres = new ArrayList<>();
            for (String genre : user.getGenrePreferences()) {
                if (Book.similarGenres.containsKey(genre)){
                    relatedGenres.addAll(Book.similarGenres.get(genre));
                }
            }

            for (Book book : otherBooks) {
                if (matchesAge(book) && matchesGenre(book, relatedGenres)) {
                    exploreBooks.add(book);
                }
            }
        }

        // If there would still not be enough books even with the current explore books
        // and there would be more books left at the store, get all books at the preferred reading level
        if (recommendedBooks.size() + exploreBooks.size() < MINIMUM_RECS && otherBooks.size() - exploreBooks.size() > 0) {
            for (Book book : otherBooks) {
                if (matchesAge(book)) {
                    exploreBooks.add(book);
                }
            }
        }

        return exploreBooks;
    }

    private List<Book> getPerfectMatchBooks() {
        final List<Book> perfectMatches = new ArrayList<>();
        List<Book> booksToRemove = new ArrayList<>();

        for (Book book : otherBooks) {
            // (But don't recommend books that are already in the wishlist)
            if (inWishlist(book)) {
                booksToRemove.add(book);
                continue;
            }
            if (matchesAge(book) && matchesGenre(book, user.getGenrePreferences())) {
                perfectMatches.add(book);
                booksToRemove.add(book);
            }
        }
        otherBooks.removeAll(booksToRemove);

        return  perfectMatches;
    }

    // Returns whether or not the given book is already in this user's wishlist
    private boolean inWishlist(Book book) {
        List<Book> wishlist = user.getWishlist();
        try {
            for (Book wishbook : wishlist) {
                if (wishbook.getIsbn().equals(book.getIsbn())) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all the books offered at the given store
    private void queryBooks(final ParseUser store) {
        ParseQuery<Book> query = ParseQuery.getQuery(Book.class);
        query.include(Book.KEY_BOOKSTORE);
        query.whereEqualTo(Book.KEY_BOOKSTORE, store);
        try {
            List<Book> queriedBooks = query.find();
            otherBooks.addAll(queriedBooks);
        } catch (ParseException e) {
            Log.e(TAG, "Error retrieving books from " + store.getUsername(), e);
        }
    }

    // Get the bookstores that are near the currently logged in user
    // Returns a list of bookstores of length limit (from nearest to farthest)
    private void getNearbyStores(ParseGeoPoint currentLocation, int numLimit, int radiusLimit) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereNear(LocalInkUser.KEY_GEO_LOCATION, currentLocation);
        // Only get the bookstores, not the readers
        query.whereEqualTo(LocalInkUser.KEY_IS_BOOKSTORE, true);
        query.whereWithinMiles(LocalInkUser.KEY_GEO_LOCATION, currentLocation, radiusLimit);
        query.setLimit(numLimit);
        final List<ParseUser> stores = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting nearby bookstores: " + e.getMessage(), e);
                    return;
                }
                stores.addAll(users);
                getRecommendations(stores);
            }
        });
    }

    // Get the user's last known location
    private void getLastKnownLocation(final int numLimit, final int radiusLimit) {
        ((MainActivity) getActivity()).getAVLoadingIndivatorView().smoothToShow();

        final ParseGeoPoint currentLocation = new ParseGeoPoint();

        // Ask for permission to access current location if they aren't already granted
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }

        // Get the current location and put the latitude and longitude into the ParseGeoPoint
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentLocation.setLatitude(location.getLatitude());
                            currentLocation.setLongitude(location.getLongitude());
                            getNearbyStores(currentLocation, numLimit, radiusLimit);
                        } else {
                            Toast.makeText(getContext(), "Could not find your location", Toast.LENGTH_SHORT).show();
                            //TODO: Try a saved location in Parse
                            Log.e(TAG, "Current user's location could not be found!");
                        }
                    }
                });
    }

    // Returns whether or not the book fits any of the user's preferred genres
    private boolean matchesGenre(Book book, List<String> genres) {
        try {
            for (String genre : genres) {
                if (genre.equals(book.getGenre())) {
                    return true;
                }
            }
            return false;
        } catch (ParseException e) {
            Log.e(TAG, "Error retrieving the genre of book " + book.getTitle(), e);
        }
        return false;
    }

    // Returns whether or not the book fits any of the user's preferred reading levels
    private boolean matchesAge(Book book) {
        try {
            List<String> ageRanges = user.getAgePreferences();
            for (String ageRange : ageRanges) {
                if (ageRange.equals(book.getAgeRange())) {
                    return true;
                }
            }
            return false;
        } catch (ParseException e) {
            Log.e(TAG, "Error retrieving the age range of book " + book.getTitle(), e);
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recommendations_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.miRecSettings) {
            // Recommendation settings icon has been tapped
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // When the user goes back to the recommendations screen
    // Check whether the user has made any changes to their profile or wishlist while on other tabs
    // if they have made changes, refresh the recommendations to match those changes
    // TODO: bug - this method is not called after a book is deleted from the wishlist
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        final MainActivity mainActivity = (MainActivity) getActivity();

        if (!hidden) {
            ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser nUser, ParseException e) {
                    LocalInkUser newUser = new LocalInkUser(nUser);
                    if (!newUser.getAgePreferences().equals(user.getAgePreferences())
                            || !newUser.getGenrePreferences().equals(user.getGenrePreferences())
                            || newUser.getWishlist().size() != user.getWishlist().size()) {
                        user = newUser;

                        getLastKnownLocation(NUM_INITIAL_STORES, NUM_INITIAL_MILES);
                        setSeekBar(getView(), seekbarRadiusStores, R.id.tvRadiusStoresSeekBarValue, NUM_INITIAL_MILES);
                        setSeekBar(getView(), seekbarNumStores, R.id.tvNumStoresSeekBarValue, NUM_INITIAL_STORES);
                        mainActivity.getAVLoadingIndivatorView().smoothToShow();
                    }
                }
            });
        }
    }


    // Searches through the list of books and checks
    // if there are any duplicate books (i.e. the same book sold at different stores)
    // Removes the latter duplicates (the books further away in distance from the user)
    private void removeDuplicateBooks() {
        try {
            for (int i = 0; i < otherBooks.size(); i++) {
                for (int j = i + 1; j < otherBooks.size(); j++) {
                    if (otherBooks.get(i).getIsbn().equals(otherBooks.get(j).getIsbn())) {
                        otherBooks.remove(j);
                        j--;
                    }
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "Could not get ISBN for books from Parse ", e);
        }
    }
}