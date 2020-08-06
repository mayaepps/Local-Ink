package com.example.localink.Models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParseClassName("Book")
public class Book extends ParseObject {

    //These keys must exactly match the keys in the Parse database
    public static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    public static final String KEY_COVER = "cover";
    public static final String KEY_SYNOPSIS = "synopsis";
    public static final String KEY_BOOKSTORE = "bookstore";
    private static final String KEY_ISBN = "isbn";
    private static final String KEY_GENRES = "genres";
    private static final String KEY_AGE_RANGE = "age_range";
    public static final String KEY_CREATED_AT = "createdAt";

    public static final Map<String, List<String>> similarGenres = new HashMap<String, List<String>>() {
        {
            put("Fantasy", Arrays.asList("Science Fiction"));
            put("Science Fiction", Arrays.asList("Fantasy"));
            put("Horror", Arrays.asList("Thriller", "Mystery"));
            put("Mystery", Arrays.asList("Thriller", "Horror"));
            put("Thriller", Arrays.asList("Mystery", "Horror"));
            put("History", Arrays.asList("Historical Fiction", "Biography/Autobiography"));
            put("Historical Fiction", Arrays.asList("Biography/Autobiography", "History"));
            put("Chic lit", Arrays.asList("Romance"));
            put("Romance", Arrays.asList("Chic lit"));
        }
    };


    // Takes a JSON Book and returns a Book object
    public static Book fromJSON(JSONObject json) throws JSONException {
        Book book = new Book();
        book.setTitle(json.getString("title"));
        JSONArray authors = json.getJSONArray("authors");
        if (authors.length() == 1) {
            book.setAuthor(authors.getString(0));
        } else if (authors.length() > 1) {
            book.setAuthor(authors.join(", "));
        }
        book.setSynopsis(json.getString("description"));
        //ISBN 13
        book.setIsbn(json.getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier"));

        JSONObject imageLinks = json.getJSONObject("imageLinks");
        JSONArray keys = imageLinks.names();
        book.setCover(imageLinks.getString(keys.getString(keys.length() - 1)));

        return book;
    }

    public String getTitle() {
        // Method getString() is defined in the Parse object class, is like a getter for the key
        try {
            return fetchIfNeeded().getString(KEY_TITLE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

    public String getCover() throws ParseException {
        return fetchIfNeeded().getString(KEY_COVER);
    }

    public void setCover(String cover) {
        put(KEY_COVER, cover);
    }

    public String getSynopsis() throws ParseException {
        return fetchIfNeeded().getString(KEY_SYNOPSIS);
    }

    public void setSynopsis(String synopsis) {
        put(KEY_SYNOPSIS, synopsis);
    }

    public String getIsbn() throws ParseException {
        return fetchIfNeeded().getString(KEY_ISBN);
    }

    public void setIsbn(String isbn) {
        put(KEY_ISBN, isbn);
    }

    public List<String> getGenres() throws ParseException {
        return fetchIfNeeded().getList(KEY_GENRES);
    }

    // TODO: check the genre is in the predefined list of genres before setting
    public void setGenres(List<String> genres) {
        put(KEY_GENRES, genres);
    }

    // TODO: check the age range is in the predefined list of age ranges before setting
    public String getAgeRange() throws ParseException {
        return fetchIfNeeded().getString(KEY_AGE_RANGE);
    }

    public void setAgeRange(String age_range) {
        put(KEY_AGE_RANGE, age_range);
    }

    // TODO: check the age range is in the predefined list of age ranges before setting
    public ParseUser getBookstore() throws ParseException {
        return fetchIfNeeded().getParseUser(KEY_BOOKSTORE);
    }

    public void setBookstore(ParseUser bookstore) {
        put(KEY_BOOKSTORE, bookstore);
    }

}
