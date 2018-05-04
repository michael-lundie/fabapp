package com.michaellundie.fabapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookSearchAsyncLoader extends AsyncTaskLoader<ArrayList<BookItem>> {

    private static final String LOG_TAG = BookSearchAsyncLoader.class.getSimpleName();

    private ArrayList<BookItem> bookQueryResults = null;
    private String urlString;

    public BookSearchAsyncLoader(Context context, String url) {
        super(context);
        this.urlString = url;
    }

    @Override
    protected void onStartLoading() {
        // Let's check for cached data
        if (bookQueryResults !=null) {
            //Use cached data
            deliverResult(bookQueryResults);
            Log.i(LOG_TAG, "TEST: BookSearchAsyncLoader: Delivering some cached data.");
        } else {
            //There is no data so let's get the party started!
            forceLoad();
            Log.i(LOG_TAG, "TEST: BookSearchAsyncLoader: onStartLoading executed");
        }
    }

    @Override
    public ArrayList<BookItem> loadInBackground() {
        Log.i(LOG_TAG, "TEST: BookSearchAsyncLoader: loadInBackground executed");
        if (urlString.length() > 0 && urlString != null) {
            try {
                ArrayList resultItems = QueryUtils.fetchBookResults(urlString);
                if (resultItems != null) {
                    Log.i(LOG_TAG, "TEST Result items are:" + resultItems);
                    bookQueryResults = resultItems;
                } else {
                    throw new IOException("No response received.");
                }
            } catch(Exception e) {
                Log.e("Log error", "Problem with Request URL", e);
            }
        }
        return bookQueryResults;
    }
}