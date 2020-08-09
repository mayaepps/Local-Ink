package com.example.localink.Utils;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.localink.Fragments.MapsFragment;
import com.example.localink.R;
import com.google.android.gms.maps.MapView;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MapsUtils {

    // Start the Google Maps fragment in the MapView at the bottom of the details
    // Send the current user as a bundle (used to get store name/location for the marker)
    public static void startMap(FragmentTransaction ft, int mapViewId, ParseUser store) {
        Fragment mapsFragment = new MapsFragment();
        Bundle bundle = new Bundle();
        ArrayList<ParseUser> stores = new ArrayList<>();
        stores.add(store);
        bundle.putParcelableArrayList(ParseUser.class.getSimpleName(), stores);
        mapsFragment.setArguments(bundle);
        ft.add(mapViewId, mapsFragment).commit();
    }
}
