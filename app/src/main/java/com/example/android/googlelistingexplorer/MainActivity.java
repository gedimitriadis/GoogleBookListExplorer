package com.example.android.googlelistingexplorer;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<BookItem>> {

    private static final String LOG_TAG = MainActivity.class.getName();
    //URL for earthquake data from the USGS dataset
    String finalQuery = "";

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    /** Adapter for the list of books */
    private BookItemAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the string from search EditText inSearchActivity
        Bundle bundle = getIntent().getExtras();
        finalQuery = bundle.getString("query");

        // Find a reference to the {@link ListView} in the layout
        ListView BookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        BookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookItemAdapter (this, new ArrayList<BookItem>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
       BookListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        BookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                BookItem currentBookItem = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri BookUri = Uri.parse(currentBookItem.getUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, BookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText("no_internet_connection");
        }
    }

    @Override
    public Loader<ArrayList<BookItem>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.i(LOG_TAG, "creating loader");
        return new BookItemLoader(this, finalQuery);

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<BookItem>> loader, ArrayList<BookItem> BookItems) {
        // Hide loading indicator because the data has been loaded
        Log.i(LOG_TAG, "Load finished");
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText("no books found");

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (BookItems != null && !BookItems.isEmpty()) {
            mAdapter.addAll(BookItems);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<BookItem>> loader) {
        Log.i(LOG_TAG, "loader reset");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}



