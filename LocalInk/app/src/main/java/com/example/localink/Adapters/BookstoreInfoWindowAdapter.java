package com.example.localink.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class BookstoreInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = BookstoreInfoWindowAdapter.class.getSimpleName();
    private Context context;
    LocalInkUser bookstore;
    ImageView ivProfileImage;
    TextView tvStoreName;
    TextView tvAddress;
    TextView tvBooksCarried;

    public BookstoreInfoWindowAdapter(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null; // meaning I am only replacing the info window's contents
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.item_marker, null);

        // Get the bookstore this marker represents
        bookstore = (LocalInkUser) marker.getTag();

        // Find the views
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvStoreName = view.findViewById(R.id.tvStoreName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvBooksCarried= view.findViewById(R.id.tvBooksCarried);

        // Set the views
        ParseFile profileImage = bookstore.getProfileImage();
        if (profileImage != null) {
            Glide.with(context).load(profileImage.getUrl()).into(ivProfileImage);
        }
        tvStoreName.setText(bookstore.getName());
        tvAddress.setText(bookstore.getAddress());

        LocalInkUser currentUser = new LocalInkUser(ParseUser.getCurrentUser());
        List<Book> booksCarried = booksInWishlistCarried(bookstore, currentUser.getWishlist());
        tvBooksCarried.setText(join(booksCarried, "\n"));

        return view;
    }

    // Returns a list of the books in the user's wishlist that are sold at this store
    private List<Book> booksInWishlistCarried(LocalInkUser bookstore, List<Book> wishlist) {

        if (wishlist == null || wishlist.size() == 0) {
            return new ArrayList<>();
        }
        List<Book> booksCarried = new ArrayList<>();
        try {
            for (Book book : wishlist) {
                if (book.getBookstore().getObjectId().equals(bookstore.getUser().getObjectId())) {
                    booksCarried.add(book);
                }
            }
        } catch (ParseException e) {
            Log.e(TAG, "Could not get the bookstore from the books in the wishlist for the Google Maps marker", e);
        }

        return booksCarried;
    }

    // Join the list of books with the string given
    private String join(List<Book> list, String joinString) {
        if (list.size() == 0) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0).getTitle();
        }
        StringBuilder bookNames = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            bookNames.append(list.get(i).getTitle()).append(joinString);
        }
        bookNames.append(list.get(list.size() - 1).getTitle());

        return bookNames.toString();
    }

}
