package com.example.android.googlelistingexplorer;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by georgeD on 29/06/2017.
 */

public class BookItemLoader extends AsyncTaskLoader<ArrayList<BookItem>> {

    /**
     * Loads a list of books by using an AsyncTask to perform the
     * network request to the given URL.
     */

        /** Tag for log messages */
        private static final String LOG_TAG = BookItemLoader.class.getName();

        /** Query URL */
        private String mUrl;

        /**
         * Constructs a new {@link BookItemLoader}.
         *
         * @param context of the activity
         * @param url to load data from
         */
        public BookItemLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        /**
         * This is on a background thread.
         */
        @Override
        public ArrayList<BookItem> loadInBackground() {
            if (mUrl == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of books.
            ArrayList<BookItem> BookItems = QueryUtils.fetchBookData(mUrl);
            return BookItems;
        }
    }


