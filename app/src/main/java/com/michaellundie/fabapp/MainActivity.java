package com.michaellundie.fabapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchResultsFragment.OnListFragmentInteractionListener {

    private static final String GBOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecycleViewWithSetEmpty mRecyclerView = (RecycleViewWithSetEmpty) findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecycleViewWithSetEmpty
        mRecyclerView.setHasFixedSize(true);

        List<BookItem> bookResults = new ArrayList<>();
        //Sample data

        bookResults.add(new BookItem("title", "author", R.drawable.book_thumb_dummy));
        bookResults.add(new BookItem("title", "author", R.drawable.book_thumb_dummy));
        bookResults.add(new BookItem("title", "author", R.drawable.book_thumb_dummy));

        // Let's use a linear layout manager
        mRecyclerView.setAdapter(new BookSearchViewAdapter(bookResults));

        // Let's specify an adapter.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //Implementing mandatory method constructor.
    //Implementation notes found here: https://goo.gl/NDHKaA
    @Override
    public void onListFragmentInteraction(BookItem item) { }

}
