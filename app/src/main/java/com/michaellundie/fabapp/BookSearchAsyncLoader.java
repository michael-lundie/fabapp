package com.michaellundie.fabapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookSearchAsyncLoader extends AsyncTaskLoader<List<BookItem>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    private List<BookItem> bookQueryResults = null;
    private String urlString;

    public BookSearchAsyncLoader(Context context, String url) {
        super(context);
        this.urlString = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.i(LOG_TAG, "BookSearchAsyncLoader: onStartLoading executed");
    }

    @Override
    public List<BookItem> loadInBackground() {
        Log.i(LOG_TAG, "BookSearchAsyncLoader: loadInBackground executed");
        if (urlString.length() > 0 && urlString != null) {
            try {
                List resultItems = QueryUtils.fetchBookResults(urlString);
                if (resultItems != null) {
                    this.bookQueryResults = resultItems;
                } else {
                    throw new IOException("No response received.");
                }
            } catch(Exception e) {
                Log.e("Log error", "Problem with Request URL", e);
            }
        }
        return this.bookQueryResults;
    }

}
