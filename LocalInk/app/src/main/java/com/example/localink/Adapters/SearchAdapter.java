package com.example.localink.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localink.Activities.BookDetailsActivity;
import com.example.localink.Fragments.BookshelfFragment;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.example.localink.Utils.ChipUtils;
import com.example.localink.Utils.LocationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    public interface OnClickListener {
        void onBookClick(View view, int position);
        void onBookstoreClick(View view, int position);
    }

    private static final String TAG = "SearchAdapter";
    private static final int TYPE_BOOK = 0;
    private static final int TYPE_BOOKSTORE = 1;
    private static final float MILES_PER_METER = 1609;
    private final Context context;
    private List<ParseObject> searchedObjects;
    private OnClickListener clickListener;

    public SearchAdapter(Context context, List<ParseObject> books, OnClickListener clickListener) {
        this.context = context;
        this.searchedObjects = books;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case TYPE_BOOK: {
                view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
                return new BookViewHolder(view);
            }
            case TYPE_BOOKSTORE: {
                view = LayoutInflater.from(context).inflate(R.layout.item_bookstore, parent, false);
                return new BookstoreViewHolder(view);
            }
            default:
                throw new IllegalArgumentException("viewType is invalid: neither book nor bookstore");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        Object object = searchedObjects.get(position);
        holder.bind(object);
    }

    @Override
    public int getItemViewType(int position) {
        ParseObject object = searchedObjects.get(position);
        if (object instanceof Book) {
            return TYPE_BOOK;
        } else if (object instanceof ParseUser) {
            return TYPE_BOOKSTORE;
        } else {
            Log.e(TAG, "Invalid item view type: " + object.getClass().toString());
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return searchedObjects.size();
    }

    public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder {

        private ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(T object);
    }

    public class BookstoreViewHolder extends ViewHolder<ParseUser> {

        ImageView ivStoreProfile;
        TextView tvStoreName;
        TextView tvStoreAddress;
        TextView tvDistanceAway;

        public BookstoreViewHolder(@NonNull final View itemView) {
            super(itemView);
            ivStoreProfile = itemView.findViewById(R.id.ivStoreProfile);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvStoreAddress = itemView.findViewById(R.id.tvStoreAddress);
            tvDistanceAway = itemView.findViewById(R.id.tvDistanceAway);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onBookstoreClick(itemView, getAdapterPosition());
                }
            });
        }

        public void bind(ParseUser user) {
            final LocalInkUser bookstore = new LocalInkUser(user);
            Glide.with(context).load(bookstore.getProfileImage().getUrl()).circleCrop().into(ivStoreProfile);
            tvStoreName.setText(bookstore.getName());
            tvStoreAddress.setText(bookstore.getAddress());
            setDistanceAway(bookstore);
        }

        private void setDistanceAway(LocalInkUser bookstore) {

            final ParseGeoPoint bLocation = bookstore.getGeoLocation();

            LocationUtils.getCurrentLocation(context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location currentLocation) {

                    if (currentLocation == null) {
                        Toast.makeText(context, "Could not find your location", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final float[] results = new float[3];
                    Location.distanceBetween(bLocation.getLatitude(), bLocation.getLongitude(),
                            currentLocation.getLatitude(), currentLocation.getLongitude(), results);
                    int dist = Math.round(results[0]/MILES_PER_METER);
                    tvDistanceAway.setText(String.format("%s miles away", dist));
                }
            });
        }
    }

    public class BookViewHolder extends ViewHolder<Book> {

        ImageView ivBookCover;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvSynopsis;
        ChipGroup cgGenres;
        Chip cAgeRange;

        public BookViewHolder(@NonNull final View itemView) {
            super(itemView);
            ivBookCover= itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvSynopsis = itemView.findViewById(R.id.tvSynopsis);
            cgGenres = itemView.findViewById(R.id.cgGenresBook);
            cAgeRange = itemView.findViewById(R.id.chipAgeRange);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onBookClick(itemView, getAdapterPosition());
                }
            });
        }

        @Override
        public void bind(Book book) {
            try {
                Glide.with(context).load(book.getCover()).into(ivBookCover);
                tvTitle.setText(book.getTitle());
                tvAuthor.setText(book.getAuthor());
                tvSynopsis.setText(book.getSynopsis());
                cAgeRange.setText(book.getAgeRange());
                ChipUtils.setUpChips(context, cgGenres, book.getGenres(), true);
            } catch (ParseException e) {
                Log.e(TAG, "Error fetching book details from Parse in Search Adapter: " + e.getMessage());
            }
        }
    }

}
