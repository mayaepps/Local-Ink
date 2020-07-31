package com.example.localink.Clients;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BookClient {

    private static final String API_BASE_URL = "https://www.googleapis.com/books/v1/";
    private AsyncHttpClient client;

    public BookClient() {
        this.client = new AsyncHttpClient();
    }

    // Method for accessing the Google Books API
    // Searches for a book (Google calls books "volumes") with the given isbn
    public void getBookByIsbn(final String isbn, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("volumes?q=isbn:");
            client.get(url + URLEncoder.encode(isbn, "utf-8"), handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }
}
