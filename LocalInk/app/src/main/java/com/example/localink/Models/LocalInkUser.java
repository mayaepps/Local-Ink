package com.example.localink.Models;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;


@Parcel
public class LocalInkUser {
    ParseUser user;
    public static final String KEY_IS_BOOKSTORE = "isBookstore";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_WISHLIST = "wishlist";
    public static final String KEY_GENRE_PREFERENCES = "genrePreferences";
    public static final String KEY_AGE_PREFERENCES = "agePreferences";
    public static final String KEY_NAME = "name";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_GEO_LOCATION = "geoLocation";

    public LocalInkUser() { }

    public LocalInkUser (ParseUser obj) {
        user = obj;
    }

    public ParseUser getUser() {
        return user;
    }

    public String getAddress() {
        return user.getString(KEY_ADDRESS);
    }

    public void setAddress(String address) {
        user.put(KEY_ADDRESS, address);
    }

    public Boolean isBookstore() {
        return user.getBoolean(KEY_IS_BOOKSTORE);
    }

    public void setIsBookstore(boolean isBookstore) {
        user.put(KEY_IS_BOOKSTORE, isBookstore);
    }

    public List<Book> getWishlist() {
        List<Book> wishlist = user.getList(KEY_WISHLIST);
        return wishlist;
    }

    public void setWishlist(List<Book> wishlist) {
        user.put(KEY_WISHLIST, wishlist);
    }

    public List<String> getGenrePreferences() {
        return user.getList(KEY_GENRE_PREFERENCES);
    }

    // TODO: make sure param is valid
    public void setGenrePreferences(List<String> genrePreferences) {
        user.put(KEY_GENRE_PREFERENCES, genrePreferences);
    }

    public List<String> getAgePreferences() {
        return user.getList(KEY_AGE_PREFERENCES);
    }

    // TODO: make sure param is valid
    public void setAgePreferences(List<String> agePreferences) {
        user.put(KEY_AGE_PREFERENCES, agePreferences);
    }

    public String getName() {
        return user.getString(KEY_NAME);
    }

    public void setName(String name) {
        user.put(KEY_NAME, name);
    }

    public ParseFile getProfileImage() {
        return user.getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setProfileImage(ParseFile profileImage) {
        user.put(KEY_PROFILE_IMAGE, profileImage);
    }

    public ParseGeoPoint getGeoLocation() {
        return user.getParseGeoPoint(KEY_GEO_LOCATION);
    }

    public void setGeoLocation(ParseGeoPoint point) {
        user.put(KEY_GEO_LOCATION, point);
    }
}
