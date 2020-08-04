package com.example.localink.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localink.Adapters.BookstoreInfoWindowAdapter;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    private static final int PADDING = 150;
    private static final float ZOOM = 14F;
    private List<ParseUser> stores;
    private View mapView;
    private GoogleMap map;
    private LatLngBounds.Builder builder;
    private BookstoreInfoWindowAdapter infoWindowAdapter;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;

            //Set the adapter for the marker custom info windows
            stores = new ArrayList<>();
            infoWindowAdapter = new BookstoreInfoWindowAdapter(getContext());
            map.setInfoWindowAdapter(infoWindowAdapter);

            // If there are stores passed in using a bundle, get them, otherwise just query Parse for the stores.
            Bundle bundle = getArguments();
            if (bundle != null && !bundle.isEmpty()) {
                stores = getStores();

                for (ParseUser user : stores) {
                    setMarker(user);
                }

            } else {
                queryWishlistStores();
            }

            disallowParentScroll(googleMap);

            // If there is only one store on the map, let the user tap the map to see the store in the Google Maps app
            if (stores != null && stores.size() == 1) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng arg0) {
                        LocalInkUser store = new LocalInkUser(stores.get(0));
                        goGoogleMaps(store);
                    }
                });
            }
        }
    };

    // When used in the BookDetailView, this disallows the parent scroll view from intepreting the
    // scroll event as scrolling up and down in the scroll view, and allows the map to be dragged/panned around
    private void disallowParentScroll(GoogleMap googleMap) {
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                mapView = getView().findViewById(R.id.map);
                mapView.getParent().requestDisallowInterceptTouchEvent(true);
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mapView = getView().findViewById(R.id.map);
                mapView.getParent().requestDisallowInterceptTouchEvent(false);
            }
        });
    }

    // Open google maps and search for the bookstore
    private void goGoogleMaps(LocalInkUser store) {
         // Create a Uri from an intent string. Use the result to create an Intent.
        final Uri mapsIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(store.getName() + ", " + store.getAddress()));

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsIntentUri);
        // Make sure there is an app that can handle this intent
        if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    // Put a marker on the map at each of the stores
    private void setMarker(ParseUser store) {
        if (builder == null) {
            builder = new LatLngBounds.Builder();
        }

        LocalInkUser bookstore = new LocalInkUser(store);

        ParseGeoPoint location = bookstore.getGeoLocation();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        builder.include(currentLatLng);
        map.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .title(bookstore.getName()))
                .setTag(bookstore);

        map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

        // Zoom map in to fit the markers
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate;

        if (stores.size() == 1) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM);
        } else {
            cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
        }

        map.moveCamera(cameraUpdate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    //get stores from BookDetailsActivity 
    private List<ParseUser> getStores() {
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            Log.e(TAG, "Bundle of stores to maps fragment is null");
            return null;
        }

        return bundle.getParcelableArrayList(ParseUser.class.getSimpleName());
    }

    // Query Parse for the stores on the user's wishlist
    private void queryWishlistStores() {
        // Get the stores for the books in the wishlist
        List<Book> wishlist = (new LocalInkUser(ParseUser.getCurrentUser())).getWishlist();

        try {
            for (Book book : wishlist) {
                book.getBookstore().fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        stores.add(user);

                        setMarker(user);

                    }
                });
            }
        } catch (ParseException e) {
            Log.e(TAG, "Could not get wishlist bookstores: " + e.getLocalizedMessage());
        }
    }
}