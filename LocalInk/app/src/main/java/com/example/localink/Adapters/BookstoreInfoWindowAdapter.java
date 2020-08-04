package com.example.localink.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class BookstoreInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private boolean showWishlist;

    public BookstoreInfoWindowAdapter(Context ctx){
        context = ctx;
        this.showWishlist = showWishlist;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null; // meaning I am only replacing the info window's contents
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.item_marker, null);

        LocalInkUser bookstore = (LocalInkUser) marker.getTag();

        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);
        TextView tvStoreName = view.findViewById(R.id.tvStoreName);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvBooksCarried = view.findViewById(R.id.tvBooksCarried);

        ParseFile profileImage = bookstore.getProfileImage();
        if (profileImage != null) {
            Glide.with(context).load(profileImage.getUrl()).into(ivProfileImage);
        }
        tvStoreName.setText(bookstore.getName());
        tvAddress.setText(bookstore.getAddress());

        // TODO: Figure out how to get the books carried
        LocalInkUser currentUser = new LocalInkUser(ParseUser.getCurrentUser());

        tvBooksCarried.setText(join(currentUser.getWishlist(), "\n"));

        return view;
    }

    // Join the list of books with the string given
    private String join(List<Book> list, String joinString) {
        StringBuilder bookNames = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            bookNames.append(list.get(i).getTitle()).append(joinString);
        }
        bookNames.append(list.get(list.size() - 1).getTitle());

        return bookNames.toString();
    }
}
