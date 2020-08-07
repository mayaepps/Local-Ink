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
import com.example.localink.R;
import com.parse.ParseException;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final String TAG = "SearchAdapter";
    private final Context context;
    private List<Book> searchedBooks;

    public SearchAdapter(Context context, List<Book> books){
        this.context = context;
        this.searchedBooks = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(searchedBooks.get(position));
    }

    @Override
    public int getItemCount() {
        return searchedBooks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
        }

        public void bind(Book book) {
            try {
                Glide.with(context).load(book.getCover()).into(ivCover);
                tvTitle.setText(book.getTitle());
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
