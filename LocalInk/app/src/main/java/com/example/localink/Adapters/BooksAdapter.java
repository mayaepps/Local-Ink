package com.example.localink.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localink.Models.Book;
import com.example.localink.R;
import com.example.localink.Utils.ChipUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseException;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {

    private static final String TAG = "BooksAdapter";

    public interface OnClickListener {
        void onClick(int position, View view);
        void onLongClick(int position);
    }

    private Context context;

    // The list of books to be adapted and shown in the recycler view
    private List<Book> books;
    private OnClickListener clickListener;

    public BooksAdapter(Context context, List<Book> books, OnClickListener clickListener) {
        this.context = context;
        this.books = books;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);

        // Bind the view at this position to the book in the list at this position
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCover;
        private TextView tvBookTitle;
        private TextView tvAuthor;
        private TextView tvSynopsis;
        private ChipGroup cgGenresBook;
        private Chip chipAgeRange;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            // Find all the of the relevant views in item_book
            ivCover = itemView.findViewById(R.id.ivCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvSynopsis = itemView.findViewById(R.id.tvSynopsis);
            cgGenresBook = itemView.findViewById(R.id.cgGenresBook);
            chipAgeRange = itemView.findViewById(R.id.chipAgeRange);

            // If a clickListener was passed into the adapter, set the methods on the itemView
            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickListener.onClick(getAdapterPosition(), itemView);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        clickListener.onLongClick(getAdapterPosition());
                        return true;
                    }
                });
            }
        }

        // Bind the book information to each of the views in item_book
        public void bind(Book book) {
            try {
                if (book.getCover() != null) {
                    Glide.with(context).load(book.getCover()).into(ivCover);

                }
                tvBookTitle.setText(book.getTitle());
                tvAuthor.setText(book.getAuthor());
                tvSynopsis.setText(book.getSynopsis());
                ChipUtils.setUpChips(context, cgGenresBook, book.getGenres(), true);
                chipAgeRange.setText(book.getAgeRange());
            } catch (ParseException e) {
                Log.e("ViewHolder", "Error fetching book fields from Parse " + e.getMessage());
            }
        }
    }
}
