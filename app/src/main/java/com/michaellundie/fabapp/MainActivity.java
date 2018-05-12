package com.michaellundie.fabapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    private Button searchButton;
    private EditText searchEditText;
    static int mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SpinnerInteractionListener mSpinnerListener = new SpinnerInteractionListener();
        Spinner mSpinner = findViewById(R.id.languageSelect);
        setupSpinner(mSpinner);
        mSpinner.setOnTouchListener(mSpinnerListener);
        mSpinner.setOnItemSelectedListener(mSpinnerListener);

        mRecyclerView = (RecycleViewWithSetEmpty) findViewById(R.id.list);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemViewCacheSize(5);

        //Set the empty state for our custom RecycleViewer
        mEmptyStateTextView = (TextView) findViewById(R.id.list_empty);
        mRecyclerView.setEmptyView(mEmptyStateTextView);

        //Set up the progress ring view
        mProgressRing = findViewById(R.id.progressRing);

        searchEditText = findViewById(R.id.searchEditText);
        final Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchClicked();
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

    public void onSearchClicked() {
        // upon a new search initiation, destroy previous loader.
        getLoaderManager().destroyLoader(BOOKSEARCH_LOADER_ID);
        //clear the array list
        mList.clear();
        //clear our cache
        CacheManager.getInstance().clear();
        //notify the adapter
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
        String userQuery = searchEditText.getText().toString();

        //TODO: Hook up language selection
        //Check for chosen language.

        GBOOKS_REQUEST_URL = QueryUtils.queryRequestBuilder(this,userQuery);

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
    private void setupSpinner(Spinner spinner) {
        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    public static void setmLanguage(int mLanguage) {
        MainActivity.mLanguage = mLanguage;
    }


    private void showSearchDialogue() {
        LayoutInflater dialogInflator = getLayoutInflater();
        ViewGroup viewRoot = findViewById(R.id.searchDialog);


        View dialogView = dialogInflator.inflate(R.layout.search_dialogue, viewRoot);

        AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(this);
        searchDialogBuilder.setView(dialogView);

        final EditText searchInputEditText = (EditText) viewRoot.findViewById(R.id.searchEditText);
        final Spinner languageSelectSpinner = (Spinner) viewRoot.findViewById(R.id.languageSelect);



        //final Dialog searchDialog = new Dialog(this);
        //searchDialog.setContentView(searchDialog.findViewById(R.id.searchDialog));
        //searchDialog.setTitle("Search Books");

        //Set-up dialogue views
        EditText searchInput = (EditText) searchDialog.findViewById(R.id.searchEditText);


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Search Books")
                .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}