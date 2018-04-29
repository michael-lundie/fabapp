package com.michaellundie.fabapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookSearchViewAdapter extends RecyclerView.Adapter<BookSearchViewAdapter.ViewHolder> {

    public static final String LOG_TAG = BookSearchViewAdapter.class.getSimpleName();

    private final ArrayList<BookItem> mValues;

    public BookSearchViewAdapter(ArrayList<BookItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull BookSearchViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Log.i(LOG_TAG, "TEST: onBindViewHolder called. Current Item: " + holder.mItem);
        holder.mTitle.setText(mValues.get(position).getTitle());
        holder.mAuthor.setText(mValues.get(position).getAuthor());
        holder.mThumbnail.setImageResource(mValues.get(position).getThumbnailResourceId());

    }

    @Override
    public int getItemCount() {
        int items = mValues.size();
        String itemsCount = Integer.toString(items);
        Log.d("TEST: itemCount", itemsCount);
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final TextView mAuthor;
        public final ImageView mThumbnail;
        public BookItem mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.title);
            mAuthor = (TextView) view.findViewById(R.id.author);
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}