package com.michaellundie.fabapp;

import android.content.Context;
import android.app.LoaderManager;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private static String GBOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes/?q=test&langRestrict=en&maxResults=20";
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressRing;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private Bundle mBundleRecyclerViewState;

    private Button searchButton;
    private EditText searchEditText;
    static int mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SpinnerInteractionListener mSpinnerListener = new SpinnerInteractionListener();
        Spinner mSpinner = findViewById(R.id.languageSelect);
        setupSpinner(mSpinner, this, R.array.language_array, android.R.layout.simple_spinner_item);
        mSpinner.setOnTouchListener(mSpinnerListener);
        mSpinner.setOnItemSelectedListener(mSpinnerListener);

        mRecyclerView = (RecycleViewWithSetEmpty) findViewById(R.id.list);



        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecycleViewWithSetEmpty
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemViewCacheSize(5);

        //Set the empty state for our custom RecycleViewer
        mEmptyStateTextView = (TextView) findViewById(R.id.list_empty);
        mRecyclerView.setEmptyView(mEmptyStateTextView);

        //Set up the progress ring view
        mProgressRing = findViewById(R.id.progressRing);

        searchButton = findViewById(R.id.searchButton);
        searchEditText = findViewById(R.id.searchEditText);
        // Let's use a linear layout manager
        mAdapter = new BookSearchViewAdapter(mList, this);

        // Let's specify an adapter.
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mAdapter);
    }



    public void onSearchClicked(View searchButton) {
        // upon a new search initiation, destroy previous loader.
        getLoaderManager().destroyLoader(BOOKSEARCH_LOADER_ID);
        //clear the array list
        mList.clear();
        //clear our cache
        CacheManager.getInstance().clear();
        //notify the adapter
        mAdapter.notifyDataSetChanged();

        String userQuery = searchEditText.getText().toString();
        Context context = getApplicationContext();

        //TODO: Hook up language selection
        GBOOKS_REQUEST_URL = QueryUtils.queryRequestBuilder(this,userQuery,"en");
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
    private void setupSpinner(Spinner spinner, Context context, int resourceArray, int resourceItem) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                resourceArray, resourceItem);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public static void setmLanguage(int mLanguage) {
        MainActivity.mLanguage = mLanguage;
    }
}