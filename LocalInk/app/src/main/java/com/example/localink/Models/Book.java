package com.example.localink.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Book")
public class Book extends ParseObject {

    //These keys must exactly match the keys in the Parse database
    public static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    public static final String KEY_COVER = "cover";
    public static final String KEY_SYNOPSIS = "synopsis";
    public static final String KEY_BOOKSTORE = "bookstore";
    private static final String KEY_ISBN = "isbn";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_AGE_RANGE = "age_range";
    public static final String KEY_CREATED_AT = "createdAt";

    public String getTitle() {
        // Method getString() is defined in the Parse object class, is like a getter for the key
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        // Method put() is defined in the Parse object class, is like a setter for a key & value
        put(KEY_TITLE, title);
    }

    public String getAuthor() {
        return getString(KEY_AUTHOR);
    }

    public void setAuthor(String author) {
        put(KEY_AUTHOR, author);
    }

    public String getCover() {
        return getString(KEY_COVER);
    }

    public void setCover(String cover) {
        put(KEY_COVER, cover);
    }

    public String getSynopsis() {
        return getString(KEY_SYNOPSIS);
    }

    public void setSynopsis(String synopsis) {
        put(KEY_SYNOPSIS, synopsis);
    }

    public ParseObject getBookstore() {
        return getParseObject(KEY_BOOKSTORE);
    }

    public void setBookstore(ParseObject bookstore) {
        put(KEY_BOOKSTORE, bookstore);
    }

    public Number getIsbn() {
        return getNumber(KEY_ISBN);
    }

    public void setIsbn(Number isbn) {
        put(KEY_ISBN, isbn);
    }

    public String getGenre() {
        return getString(KEY_GENRE);
    }

    // TODO: check the genre is in the predefined list of genres before setting
    public void setGenre(String genre) {
        put(KEY_GENRE, genre);
    }

    // TODO: check the age range is in the predefined list of age ranges before setting
    public String getAgeRange() {
        return getString(KEY_AGE_RANGE);
    }

    public void setAgeRange(String age_range) {
        put(KEY_AGE_RANGE, age_range);
    }
}
