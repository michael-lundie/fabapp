package com.michaellundie.fabapp;

import android.app.LoaderManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookSearchViewAdapter extends RecyclerView.Adapter<BookSearchViewAdapter.ViewHolder> {


    public static final String LOG_TAG = BookSearchViewAdapter.class.getSimpleName();
    private Context mContext;
    private final ArrayList<BookItem> mValues;

    //TODO: Log tool
    private static int viewPositionVariable;
    private static int viewPositionKey;

    public BookSearchViewAdapter(ArrayList<BookItem> items, Context context) {
        mValues = items;
        mContext = context;
        Log.i(LOG_TAG, "TEST: Initialising BookSearchViewAdapter");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(LOG_TAG, "TEST: OnCreateViewHolder called.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull BookSearchViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Log.i(LOG_TAG, "TEST: onBindViewHolder called. Current Item: " + holder.mItem);
        holder.mTitleView.setText(mValues.get(position).getTitle());

        ArrayList<String> authorArray = holder.mItem.getAuthors();
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

        // Let's load our thumbnail images.
        //TODO: Log tool
        viewPositionVariable = position;
        viewPositionKey = holder.mItem.getItemID();

        ImageView imageView;

        imageView = (ImageView) holder.mThumbnailView;

        // Fetch the URL we will use for downloading our image
        String dataItem = mValues.get(position).getThumbnailURL();

        // -Begin code from https://stackoverflow.com/a/22855962/9738433-
        Log.i(LOG_TAG, "TEST: Attempting to get bitmap from cache for position " + position);

        BitmapDrawable image = CacheManager.getInstance().getBitmapFromMemCache(holder.mItem.getItemID());

        if(image != null) {
            holder.thumbnailProgressBar.setVisibility(View.INVISIBLE);
            holder.mThumbnailView.setVisibility(View.VISIBLE);
            Log.i(LOG_TAG, "TEST: Looks like image is in cache - return it.");
            // This internally is checking reference count on previous bitmap it used.
            imageView.setImageDrawable(image);
        } else {
            Log.i(LOG_TAG, "TEST: Nothing in cache, download and put it in cache");
            // Set up a loading spinner
            holder.thumbnailProgressBar.setVisibility(View.VISIBLE);
            holder.mThumbnailView.setVisibility(View.INVISIBLE);
            HashMap<Integer, String> imageAndViewPair = new HashMap<Integer, String>();
            imageAndViewPair.put(holder.mThumbnailViewId, holder.mItem.getThumbnailURL());

            loadImagesAsync(imageAndViewPair, holder.mView, holder.mItem.getItemID(), holder.thumbnailProgressBar);
        }
        // -End code from https://stackoverflow.com/a/22855962/9738433-

    }

    @Override
    public int getItemCount() {
        int items = mValues.size();
        //TODO: Remove log
        //String itemsCount = Integer.toString(items);
        //Log.d("TEST: itemCount", itemsCount);
        return mValues.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        Log.i(LOG_TAG, "TEST: onViewRecycled ITEM ID:");
        super.onViewRecycled(holder);
    }

    @Override
    public long getItemId(int position) {
        Log.i(LOG_TAG, "TEST: Current ITEM ID:" + getItemId(position));
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mAuthor0View;
        public final TextView mAuthor1View;
        public final TextView mAuthor2View;
        public final Integer mThumbnailViewId;
        public final ImageView mThumbnailView;
        public final ProgressBar thumbnailProgressBar;
        public BookItem mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mAuthor0View = (TextView) view.findViewById(R.id.author);
            mAuthor1View = (TextView) view.findViewById(R.id.author1);
            mAuthor2View = (TextView) view.findViewById(R.id.author2);
            mThumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            mThumbnailViewId = R.id.thumbnail;
            thumbnailProgressBar = (ProgressBar) view.findViewById(R.id.thumb_progress_spinner);
        }
    }

    // Image Async Downloader code from:
    // https://android.jlelse.eu/async-loading-images-on-android-like-a-big-baws-fd97d1a91374
    private void loadImagesAsync(final Map<Integer, String> bindings, final View view, final int id, final ProgressBar progressBar) {
        for (final Map.Entry<Integer, String> binding :
                bindings.entrySet()) {
            new DownloadImageAsync(new DownloadImageAsync.Listener() {
                @Override
                public void onImageDownloaded(final Bitmap bitmap) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);

                    CacheManager.getInstance().addBitmapToMemoryCache(id, bitmapDrawable);
                    ImageView thumbnailView = view.findViewById(binding.getKey());
                    thumbnailView.setImageDrawable(bitmapDrawable);
                    thumbnailView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onImageDownloadError() {
                    Log.e(LOG_TAG, "Failed to download image for "
                            + binding.getKey());
                }
            }).execute(binding.getValue());
        }
    }

    /*
    BEGIN LOG TOOLS
     */

    public static int getViewPosition() {
        return viewPositionVariable;
    }
    public static int getViewKey() {
        return viewPositionKey;
    }
}
