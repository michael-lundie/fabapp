package com.michaellundie.fabapp;

import android.app.LoaderManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        holder.mTitleView.setText(mValues.get(position).getTitle());

        ArrayList<String> authorArray = mValues.get(position).getAuthors();
        for (int authorNumber = 0; authorNumber < authorArray.size(); authorNumber++) {
            //TODO: Handle case more than 3 authors.
            switch (authorNumber) {
                case 0:
                    holder.mAuthor0View.setText(authorArray.get(authorNumber));
                    break;
                case 1:
                    holder.mAuthor1View.setText(authorArray.get(authorNumber));
                    holder.mAuthor1View.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    holder.mAuthor2View.setText(authorArray.get(authorNumber));
                    holder.mAuthor2View.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        //holder.mThumbnailView.setImageResource(mValues.get(position).getThumbnailURL());
        //holder.mThumbnailView.setImageResource(R.drawable.book_thumb_dummy);

        // Let's retrieve our thumbnail images.


       // Bitmap thumnail =

        HashMap<Integer, String> hm = new HashMap<Integer, String>();
        hm.put(holder.mThumbnailId, holder.mItem.getThumbnailURL());

        loadImagesAsync(hm, holder.mView);
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
        public final TextView mTitleView;
        public final TextView mAuthor0View;
        public final TextView mAuthor1View;
        public final TextView mAuthor2View;
        public final Integer mThumbnailId;
        public BookItem mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mAuthor0View = (TextView) view.findViewById(R.id.author);
            mAuthor1View = (TextView) view.findViewById(R.id.author1);
            mAuthor2View = (TextView) view.findViewById(R.id.author2);
            mThumbnailId = R.id.thumbnail;
        }
    }

    private void loadImagesAsync(final Map<Integer, String> bindings, final View view) {
        for (final Map.Entry<Integer, String> binding :
                bindings.entrySet()) {
            new DownloadImageAsync(new DownloadImageAsync.Listener() {
                @Override
                public void onImageDownloaded(final Bitmap bitmap) {
                    ((ImageView) view.findViewById(binding.getKey()))
                            .setImageBitmap(bitmap);
                }
                @Override
                public void onImageDownloadError() {
                    Log.e(LOG_TAG, "Failed to download image from "
                            + binding.getKey());
                }
            }).execute(binding.getValue());
        }
    }
}