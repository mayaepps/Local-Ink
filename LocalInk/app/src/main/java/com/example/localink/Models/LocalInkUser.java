package com.example.localink.Models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

public class LocalInkUser {
    ParseUser user;
    public static final String KEY_IS_BOOKSTORE = "isBookstore";
    public static final String KEY_BOOKSTORE = "bookstore";
    public static final String KEY_WISHLIST = "wishlist";
    public static final String KEY_GENRE_PREFERENCE = "genrePreference";
    public static final String KEY_AGE_PREFERENCE = "agePreference";


    public LocalInkUser (ParseUser obj) {
        user = obj;
    }

    protected ParseUser getUser() {
        return user;
    }

    public Boolean isBookstore() {
        return user.getBoolean(KEY_IS_BOOKSTORE);
    }

    public void setIsBookstore(boolean isBookstore) {
        user.put(KEY_IS_BOOKSTORE, isBookstore);
    }

    public ParseObject getBookstore() {
        return user.getParseObject(KEY_BOOKSTORE);
    }

    public void setBookstore(ParseObject bookstore) {
        user.put(KEY_BOOKSTORE, bookstore);
    }

    public JSONArray getWishlist() {
        return user.getJSONArray(KEY_WISHLIST);
    }

    public void setWishlist(JSONArray wishlist) {
        user.put(KEY_WISHLIST, wishlist);
    }

    public String getGenrePreference() {
        return user.getString(KEY_GENRE_PREFERENCE);
    }

    // TODO: make sure param is valid
    public void setGenrePreference(String genrePreference) {
        user.put(KEY_GENRE_PREFERENCE, genrePreference);
    }

    public String getAgePreference() {
        return user.getString(KEY_AGE_PREFERENCE);
    }

    // TODO: make sure param is valid
    public void setAgePreference(String agePreference) {
        user.put(KEY_AGE_PREFERENCE, agePreference);
    }
}
