package com.michaellundie.fabapp;

import android.app.AlertDialog;
import android.content.Context;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

    static String mLanguage = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up our content view
        setContentView(R.layout.activity_main);

        // Set up our custom recycler view
        mRecyclerView = (RecycleViewWithSetEmpty) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemViewCacheSize(5);

        //Set the empty state view for our custom RecycleViewer
        mEmptyStateTextView = (TextView) findViewById(R.id.list_empty);
        mRecyclerView.setEmptyView(mEmptyStateTextView);

        //Set up the progress ring view
        mProgressRing = findViewById(R.id.progressRing);

        // Setting up our FAB search button
        FloatingActionButton searchDialogButton = findViewById(R.id.fab_search);
        searchDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = QueryUtils.checkNetworkAccess(view.getContext());
                if (!isConnected) {
                    showToast(getResources().getString(R.string.no_connection));
                } else {
                    showSearchDialogue();
                }
            }
        });

        //Check for a saved instance to handle rotation and resume
        if(savedInstanceState != null)
        {
            Log.i(LOG_TAG, "TEST: SAVEDINSTANCE not null");
            mList = savedInstanceState.getParcelableArrayList("mList");
            if (mList != null ) {
                Log.i(LOG_TAG, "TEST: Resume list is not null.");
                findViewById(R.id.splash_image).setVisibility(View.INVISIBLE);
                // Re-attach our loader manager. https://stackoverflow.com/a/16525445/9738433
                getLoaderManager().initLoader(BOOKSEARCH_LOADER_ID, null,
                        bookSearchLoaderCallback);
            } else {

                mList = new ArrayList<>();
            }
        }
        // Create our new custom recycler adapter
        mAdapter = new BookSearchViewAdapter(mList, this);

        //Check for screen orientation
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == 1) {
            // If portrait mode use Linear Layout
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mAdapter);
        } else {
            // If landscape mode set our grid layout to 2 columns
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Saving parcelable code adapted from : https://stackoverflow.com/a/12503875/9738433
        if (!mList.isEmpty()){
            outState.putParcelableArrayList("mList", mList);
        }
        super.onSaveInstanceState(outState);
    }

    public void onSearchClicked(String searchInput) {

        // upon a new search initiation, destroy previous loader.
        getLoaderManager().destroyLoader(BOOKSEARCH_LOADER_ID);
        //clear the array list
        mList.clear();
        //clear our cache
        CacheManager.getInstance().clear();
        //notify the adapter and scroll to position 0
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);

        //Build our URL from user input
        GBOOKS_REQUEST_URL = QueryUtils.queryRequestBuilder(this,searchInput, mLanguage);
        // Let's get started! Execute search!
        executeSearch();
    }

    public void executeSearch() {

        // Create loader from class, as opposed to implementing the LoaderManager withing MainActivity
        // Used assistance and code from: https://stackoverflow.com/a/20839825
        bookSearchLoaderCallback = new BookSearchCallback(this, GBOOKS_REQUEST_URL, mList, mAdapter,
                mProgressRing, mEmptyStateTextView);

        boolean isConnected = QueryUtils.checkNetworkAccess(this);
        if (!isConnected) {
            // There is no internet connection. Let's deal with that.
            // We already checked for connection, but just in case the user resumed while the dialog
            // was open, perhaps a double check is good here.
            mProgressRing.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            showToast(getResources().getString(R.string.no_connection));
        } else {
            // Looks like we are good to go.
            mEmptyStateTextView.setVisibility(View.GONE);
            // Let's get our loader manager hooked up and started
            getLoaderManager().initLoader(BOOKSEARCH_LOADER_ID, null, bookSearchLoaderCallback);
        }
    }

    /**
     * This method creates, displays and handles our search dialogue.
     */
    private void showSearchDialogue() {

        LayoutInflater dialogInflator = getLayoutInflater();
        final ViewGroup viewRoot = findViewById(R.id.searchDialog);

        // Let's inflate our dialogue from the XML
        View dialogView = dialogInflator.inflate(R.layout.search_dialogue, viewRoot);

        // Begin a new AlertDialog builder.
        AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(this);

        // Assign our inflate view to the dialog
        searchDialogBuilder.setView(dialogView);

        //Set up our EditText and language selection TextViews.
        final EditText searchInputEditText = (EditText) dialogView.findViewById(R.id.searchEditText);
        final TextView englishSelectButton = (TextView) dialogView.findViewById(R.id.englishSelector);
        final TextView japaneseSelectButton = (TextView) dialogView.findViewById(R.id.japaneseSelector);

        // Check which language has previously been selected. (Default is English)
        // Set our buttons appropriately.
        if (mLanguage == "ja") {
            setBackground(japaneseSelectButton, R.drawable.rounded_selected);
        } else {
            setBackground(englishSelectButton, R.drawable.rounded_selected);
        }

        // Setting up onClickListeners for our language selection buttons (TextViews)
        englishSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLanguage = "en";
                setBackground(englishSelectButton, R.drawable.rounded_selected);
                setBackground(japaneseSelectButton, R.drawable.rounded);
            }
        });
        japaneseSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLanguage = "ja";
                setBackground(japaneseSelectButton, R.drawable.rounded_selected);
                setBackground(englishSelectButton, R.drawable.rounded);

            }
        });

        // Let's build the rest of our dialog
        searchDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.search_button_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                // Fetch the users search query from EditText input.
                                //Append trim method to make sure spaces are accounted for when using
                                //TextUtils.isEmpty method.
                                String searchInput = searchInputEditText.getText().toString().trim();
                                //Best check to make sure there was some input
                                if (TextUtils.isEmpty(searchInput)) {
                                    // Show a toast informing the user of the error
                                    showToast(getResources().getString(R.string.empty_search));
                                } else {
                                    // Hide our splash image
                                    findViewById(R.id.splash_image).setVisibility(View.GONE);
                                    findViewById(R.id.progressRing).setVisibility(View.VISIBLE);
                                    // Process the search input string
                                    onSearchClicked(searchInput);
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel_button_text),
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
/*
  Helper methods.
 */
    private void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }
    private void setBackground(View view, int resourceID) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(this, resourceID));
    }
}