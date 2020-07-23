package com.example.localink.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocoderUtils {

    private static final String TAG = "GeocoderUtils";

    public static ParseGeoPoint getGeoLocationFromAddress(Context context, String address) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.US);
            List<Address> addresses = geocoder.getFromLocationName(address, 5);

            if (addresses.size() > 0) {
                Double latitude = addresses.get(0).getLatitude();
                Double longitude = addresses.get(0).getLongitude();
                ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
                return point;
            } else {
                Toast.makeText(context, "Sorry, invalid address!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error creating GeoPoint for this address", e);
        }
        return null;
    }
}
