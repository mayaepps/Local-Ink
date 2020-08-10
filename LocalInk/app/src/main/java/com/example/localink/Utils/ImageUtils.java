package com.example.localink.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class ImageUtils {

    private static final String TAG = "ImageUtils";
    static File photoFile;
    static String photoFileName;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 20;

    // Launch the implicit intent to open the camera application, and provide the camera with a fileProvider
    // to save the image
    public static void launchCamera(Context context, String fileName) {

        photoFileName = fileName;
        photoFile = null;
        // create implicit Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference for future access
        photoFile = getPhotoFile(context, photoFileName);

        // wrap File object into a content provider, required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(context, "com.localink.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // As long as the result is not null, it's safe to use the intent to go to the camera
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((Activity)context).startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public static File getPhotoFile(Context context, String photoFileName) {

        // Get the photos storage directory
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.e(TAG, "Failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
    }

    public static File getPhotoFile() {
        return photoFile;
    }

    public static String getPhotoFileName() {
        return photoFileName;
    }
}
