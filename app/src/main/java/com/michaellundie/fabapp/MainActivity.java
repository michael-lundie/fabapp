package com.michaellundie.fabapp;

import android.content.Context;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<BookItem>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    private RecycleViewWithSetEmpty mRecyclerView;
    private RecycleViewWithSetEmpty.Adapter mAdapter;
    private ArrayList<BookItem> mList = new ArrayList<>();

    private static final int BOOKSEARCH_LOADER_ID = 1;
    private static final String GBOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&langRestrict=en&maxResults=5";

    private TextView mEmptyStateTextView;
    private ProgressBar mProgressRing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecycleViewWithSetEmpty) findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecycleViewWithSetEmpty
        mRecyclerView.setHasFixedSize(true);

        //Set the empty state for our custom RecycleViewer
        mEmptyStateTextView = (TextView) findViewById(R.id.list_empty);
        mRecyclerView.setEmptyView(mEmptyStateTextView);

        //Set up the progress ring view
        mProgressRing = findViewById(R.id.progressRing);

        // Let's use a linear layout manager
        mAdapter = new BookSearchViewAdapter(mList);

        // Let's specify an adapter.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);


        boolean isConnected = QueryUtils.checkNetworkAccess(this);
        if (!isConnected) {
            mProgressRing.setVisibility(View.GONE);
            mEmptyStateTextView.setText("TEST: No Internet Connection"); //TODO: Correct String Literals
        } else {
            getLoaderManager().initLoader(BOOKSEARCH_LOADER_ID, null, this);
            Log.i(LOG_TAG, "TEST: initLoader executed");
        }
    }

    //Why do we have to use ArrayList here as opposed to List?
    //If List is an interface...we can't use it as a return type. Is there no way to work
    // around this?
    @Override
    public Loader<ArrayList<BookItem>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "TEST: onCreateLoader called.");
        return new BookSearchAsyncLoader(this, GBOOKS_REQUEST_URL);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.i(LOG_TAG, "TEST: onLoaderReset executed");
        mList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BookItem>> loader, ArrayList<BookItem> data) {
        Log.i(LOG_TAG, "TEST: onLoadFinished executed");
        Log.i(LOG_TAG, "TEST: onLoadFinished data:" + data); //NOTE: OK!
        mList.clear();
        mAdapter.notifyDataSetChanged();
        mList.addAll(data);
        mAdapter.notifyDataSetChanged();
        mProgressRing.setVisibility(View.GONE);
        mEmptyStateTextView.setText("No Books"); //TODO: Edit string Literal
    }
}
