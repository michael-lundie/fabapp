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

public class BookSearchCallback implements LoaderManager.LoaderCallbacks<ArrayList<BookItem>> {

    public static final String LOG_TAG = BookSearchViewAdapter.class.getSimpleName();
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
        Log.i(LOG_TAG, "TEST: onCreateLoader called.");
        return new BookSearchAsyncLoader(context, connectURL);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.i(LOG_TAG, "TEST: onLoaderReset executed");
        list.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BookItem>> loader, ArrayList<BookItem> data) {
        Log.i(LOG_TAG, "TEST: onLoadFinished executed");
        Log.i(LOG_TAG, "TEST: onLoadFinished data:" + data); //NOTE: OK!
        list.clear();
        adapter.notifyDataSetChanged();
        list.addAll(data);
        adapter.notifyDataSetChanged();
        progressRing.setVisibility(View.GONE);
        emptyStateTextView.setText("No Books"); //TODO: Edit string Literal
    }
}