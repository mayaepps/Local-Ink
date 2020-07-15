package com.example.localink.Adapters;

import android.content.Context;
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

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {

    private Context context;

    // The list of books to be adapted and shown in the recycler view
    private List<Book> books;

    public BooksAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
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
        private TextView tvTitle;
        private TextView tvAuthor;
        private TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find all the of the relevant views in item_book
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        // Bind the book information to each of the views in item_book
        public void bind(Book book) {
            if (book.getCover() != null) {
                Glide.with(context).load(book.getCover()).into(ivCover);

            }
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvDescription.setText(book.getSynopsis());
        }
    }
}
