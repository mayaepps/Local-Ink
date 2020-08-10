package com.example.localink.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationUtils {

    public static final int ACCESS_LOCATION_REQUEST_CODE = 42;


    // Get the current location of the phone
    public static void getCurrentLocation(Context context, OnSuccessListener<Location> locationCallback) {

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // Ask for permission to access current location if they aren't already granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, locationCallback);
    }
}
