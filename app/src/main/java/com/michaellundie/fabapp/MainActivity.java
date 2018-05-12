package com.michaellundie.fabapp;

import android.app.AlertDialog;
import android.content.Context;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    public static final String LOG_TAG = MainActivity.class.getName();

    private LoaderManager.LoaderCallbacks<ArrayList<BookItem>> bookSearchLoaderCallback;
    private RecycleViewWithSetEmpty mRecyclerView;
    private RecycleViewWithSetEmpty.Adapter mAdapter;
    private ArrayList<BookItem> mList = new ArrayList<>();

    private static final int BOOKSEARCH_LOADER_ID = 1;
    private static String GBOOKS_REQUEST_URL;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressRing;

    static String mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecycleViewWithSetEmpty) findViewById(R.id.list);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemViewCacheSize(5);

        //Set the empty state for our custom RecycleViewer
        mEmptyStateTextView = (TextView) findViewById(R.id.list_empty);
        mRecyclerView.setEmptyView(mEmptyStateTextView);

        //Set up the progress ring view
        mProgressRing = findViewById(R.id.progressRing);

        FloatingActionButton searchDialogButton = findViewById(R.id.fab_search);
        searchDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = QueryUtils.checkNetworkAccess(view.getContext());
                if (!isConnected) {
                    // Todo: stringliteral
                    showToast("No Internet access. Please check your settings.");
                } else {
                    showSearchDialogue(view.getContext());
                }
            }
        });

        if(savedInstanceState != null)
        {
            Log.i(LOG_TAG, "TEST: SAVEDINSTANCE not null");
            mList = savedInstanceState.getParcelableArrayList("mList");
            if (mList !=null) {Log.i(LOG_TAG, "TEST: Resume list is not null.");}
            mAdapter = new BookSearchViewAdapter(mList, this);

            // Re-attach our loader manager. https://stackoverflow.com/a/16525445/9738433
            getLoaderManager().initLoader(BOOKSEARCH_LOADER_ID, null, bookSearchLoaderCallback);
        } else {
            mAdapter = new BookSearchViewAdapter(mList, this);
        }

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == 1) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //Saving parcelable code adapted from : https://stackoverflow.com/a/12503875/9738433
        outState.putParcelableArrayList("mList", mList);
        super.onSaveInstanceState(outState);
    }

    public void onSearchClicked(String searchInput) {
        // upon a new search initiation, destroy previous loader.
        getLoaderManager().destroyLoader(BOOKSEARCH_LOADER_ID);
        //clear the array list
        mList.clear();
        //clear our cache
        CacheManager.getInstance().clear();
        //notify the adapter
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);

        //TODO: Hook up language selection
        //Check for chosen language.

        GBOOKS_REQUEST_URL = QueryUtils.queryRequestBuilder(this,searchInput, mLanguage);

        executeSearch();
    }

    public void executeSearch() {

        // Create loader from class, as opposed to implementing the LoaderManager withing MainActivity
        // Used assistance and code from: https://stackoverflow.com/a/20839825
        bookSearchLoaderCallback = new BookSearchCallback(this, GBOOKS_REQUEST_URL, mList, mAdapter,
                mProgressRing, mEmptyStateTextView);

        boolean isConnected = QueryUtils.checkNetworkAccess(this);
        if (!isConnected) {
            mProgressRing.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText("TEST: No Internet Connection"); //TODO: Correct String Literals
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
            getLoaderManager().initLoader(BOOKSEARCH_LOADER_ID, null, bookSearchLoaderCallback);
            Log.i(LOG_TAG, "TEST: initLoader executed");
        }
    }

    /*
     * Helper methods
     */


    private void showSearchDialogue(Context context) {
        LayoutInflater dialogInflator = getLayoutInflater();


        final ViewGroup viewRoot = findViewById(R.id.searchDialog);


        View dialogView = dialogInflator.inflate(R.layout.search_dialogue, viewRoot);

        AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(this);
        searchDialogBuilder.setView(dialogView);

        final EditText searchInputEditText = (EditText) dialogView.findViewById(R.id.searchEditText);
        final TextView englishSelectButton = (TextView) dialogView.findViewById(R.id.englishSelector);
        final TextView japaneseSelectButton = (TextView) dialogView.findViewById(R.id.japaneseSelector);

        englishSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLanguage = "en";
                englishSelectButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });
        japaneseSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLanguage = "ja";
                japaneseSelectButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });

        // Setup Language options array
        final CharSequence[] items = {"English","Japanese"};

        searchDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Search",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                // Fetch the users search query from EditText input.
                                //Append trim method to make sure spaces are accounted for when using
                                //TextUtils.isEmpty method.
                                String searchInput = searchInputEditText.getText().toString().trim();
                                //Best check to make sure there was some input
                                if (TextUtils.isEmpty(searchInput)) {
                                    // Show a toast informing the user of the error
                                    showToast("message");
                                } else {
                                    // Process the search input string
                                    onSearchClicked(searchInput);
                                }
                                Log.i(LOG_TAG, "TEST: Input : " + searchInput);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // Create the dialog from the builder
        AlertDialog alertDialog = searchDialogBuilder.create();

        // Show the dialogue (attached to FAB onClick event)
        alertDialog.show();
    }

    private void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
}