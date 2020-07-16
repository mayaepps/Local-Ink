package com.example.localink.Models;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class LocalInkUser {
    ParseUser user;
    public static final String KEY_IS_BOOKSTORE = "isBookstore";
    public static final String KEY_BOOKSTORE = "bookstore";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_WISHLIST = "wishlist";
    public static final String KEY_GENRE_PREFERENCE = "genrePreference";
    public static final String KEY_AGE_PREFERENCE = "agePreference";

    public LocalInkUser() { }

    public LocalInkUser (ParseUser obj) {
        user = obj;
    }

    public ParseUser getUser() {
        return user;
    }

    public String getLocation() {
        return user.getString(KEY_LOCATION);
    }

    public void setLocation(String address) {
        user.put(KEY_LOCATION, address);
    }

    public Boolean isBookstore() {
        return user.getBoolean(KEY_IS_BOOKSTORE);
    }

    public void setIsBookstore(boolean isBookstore) {
        user.put(KEY_IS_BOOKSTORE, isBookstore);
    }

    public ParseObject getBookstore() {
        try {
            return user.fetchIfNeeded().getParseObject(KEY_BOOKSTORE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBookstore(Bookstore bookstore) {
        user.put(KEY_BOOKSTORE, bookstore);
    }

    public List<Book> getWishlist() {
        return user.getList(KEY_WISHLIST);
    }

    public void setWishlist(List<Book> wishlist) {
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
