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
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;

public class MapsFragment extends Fragment {

    private static final String TAG = "MapsFragment";
    private static final int PADDING = 100;
    private static final float ZOOM = 14F;
    private List<ParseUser> stores;

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
            stores = getStores();
            setMarkers(googleMap);

            // If there is only one store on the map, let the user tap the map to see the store in the Google Maps app
            if (stores.size() == 1) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng arg0) {
                        android.util.Log.i("onMapClick", "Horray!");
                        LocalInkUser store = new LocalInkUser(stores.get(0));
                        goGoogleMaps(store);
                    }
                });
            }
        }
    };

    // Open google maps and search for the bookstore
    private void goGoogleMaps(LocalInkUser store) {
         // Create a Uri from an intent string. Use the result to create an Intent.
        Uri mapsIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(store.getName() + ", " + store.getLocation()));

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsIntentUri);
        // Make sure there is an app that can handle this intent
        if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    // Put a marker on the map at each of the stores
    private void setMarkers(GoogleMap map) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng currentLatLng = null;
        for (ParseUser store : stores) {
            LocalInkUser bookstore = new LocalInkUser(store);
            ParseGeoPoint location = bookstore.getGeoLocation();
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            builder.include(currentLatLng);
            map.addMarker(new MarkerOptions().position(currentLatLng).title(bookstore.getName()));
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        }

        // Zoom map in to fit the markers
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate;
        if (stores.size() == 1 && currentLatLng != null) {
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
}