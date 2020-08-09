package com.example.localink.Adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localink.Models.Book;
import com.example.localink.Models.LocalInkUser;
import com.example.localink.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final String TAG = "SearchAdapter";
    private static final int TYPE_BOOK = 0;
    private static final int TYPE_BOOKSTORE = 1;
    private final Context context;
    private List<ParseObject> searchedObjects;

    public SearchAdapter(Context context, List<ParseObject> books){
        this.context = context;
        this.searchedObjects = books;
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

        public BookstoreViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStoreProfile = itemView.findViewById(R.id.ivStoreProfile);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvStoreAddress = itemView.findViewById(R.id.tvStoreAddress);
        }

        public void bind(ParseUser user) {
            LocalInkUser bookstore = new LocalInkUser(user);
            Glide.with(context).load(bookstore.getProfileImage().getUrl()).circleCrop().into(ivStoreProfile);
            tvStoreName.setText(bookstore.getName());
            tvStoreAddress.setText(bookstore.getAddress());
        }
    }

    public class BookViewHolder extends ViewHolder<Book> {

        ImageView ivBookCover;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvSynopsis;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover= itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvSynopsis = itemView.findViewById(R.id.tvSynopsis);
        }

        @Override
        public void bind(Book book) {
            try {
                Glide.with(context).load(book.getCover()).into(ivBookCover);
                tvTitle.setText(book.getTitle());
                tvAuthor.setText(book.getAuthor());
                tvSynopsis.setText(book.getSynopsis());
            } catch (ParseException e) {
                Log.e(TAG, "Error fetching book details from Parse in Search Adapter: " + e.getMessage());
            }
        }
    }

}
