package com.michaellundie.fabapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Loader callback manager. Manages ongoing process in AsyncLoader and llows us to receive results
 * from our API request straight away once the AsyncLoader thread has completed it's work.
 */
public class BookSearchCallback implements LoaderManager.LoaderCallbacks<ArrayList<BookItem>> {

    private static final String LOG_TAG = BookSearchViewAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<BookItem> list;
    private String connectURL;
    private ProgressBar progressRing;
    private TextView emptyStateTextView;
    private RecycleViewWithSetEmpty.Adapter adapter;

    BookSearchCallback(Context context, String connectURL, ArrayList<BookItem> list,
                       RecycleViewWithSetEmpty.Adapter adapter, ProgressBar bar,
                       TextView emptyStateView) {
        this.context = context;
        this.connectURL = connectURL;
        this.list = list;
        this.adapter = adapter;
        this.progressRing = bar;
        this.emptyStateTextView = emptyStateView;
    }

    @Override
    public Loader<ArrayList<BookItem>> onCreateLoader(int id, Bundle args) {
        return new BookSearchAsyncLoader(context, connectURL);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // Reset was called. Clear our local ArrayList and notify our recyclerview adapter of the
        // change.
        list.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BookItem>> loader, ArrayList<BookItem> data) {
        //Loading is complete. Clear our local array list and notify the adapter of changes.
        list.clear();
        adapter.notifyDataSetChanged();
        //Load all of our fetched and parsed data into our local ArrayList. Notify adapter.
        list.addAll(data);
        adapter.notifyDataSetChanged();
        //Hide our UI progress spinner
        progressRing.setVisibility(View.GONE);
        emptyStateTextView.setText(R.string.search_nobooks);
    }
}