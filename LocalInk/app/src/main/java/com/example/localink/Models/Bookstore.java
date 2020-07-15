package com.example.localink.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Bookstore")
public class Bookstore extends ParseObject {

    //These keys must exactly match the keys in the Parse database
    public static final String KEY_NAME = "name";
    private static final String KEY_BOOKS = "books";
    private static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final String KEY_CREATED_AT = "createdAt";

    public String getName() {
        return getString(KEY_NAME);
    }

    public List<Book> getBooks() {
        return getList(KEY_BOOKS);
    }

    public void setBooks(ParseObject[] books) {
        put(KEY_BOOKS, books);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setProfileImage(ParseFile image) {
        put(KEY_PROFILE_IMAGE, image);
    }
}
