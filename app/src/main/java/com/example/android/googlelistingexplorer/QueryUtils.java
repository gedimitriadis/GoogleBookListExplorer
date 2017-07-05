package com.example.android.googlelistingexplorer;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.R.attr.description;
import static android.provider.Contacts.SettingsColumns.KEY;

/**
 * Created by georgeD on 29/06/2017.
 */

public class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /** Keys for JSON parsing */
    private static final String KEY_VOLUMEINFO = "volumeInfo";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGELINKS = "imageLinks";
    private static final String KEY_SMALLTHUMBNAIL = "smallThumbnail";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_PREVIEWLINK = "previewLink";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google dataset and return a list of {@link BookItem} objects.
     */
    public static ArrayList<BookItem> fetchBookData(String requestUrl) {
        Log.i(LOG_TAG, "fetching Book data");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link book}s
        ArrayList<BookItem> BookItems = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link book}s
        return BookItems;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        Log.i(LOG_TAG, "create URL");
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        Log.i(LOG_TAG, "make HTTP request");
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.i(LOG_TAG, "Respone to string");
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link BookItem} objects that has been built up from
     * parsing the given JSON response.
     */
    private static ArrayList<BookItem> extractFeatureFromJson(String BookJSON) {
        Log.i(LOG_TAG, "extract from JSON");
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(BookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        ArrayList<BookItem> BookItems = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(BookJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of features for books.
            if (baseJsonResponse.has("items")) {

                JSONArray BookArray = baseJsonResponse.optJSONArray("items");

                String bookImage;
                String bookTitle;
                String bookAuthor = "";
                String bookDescription;
                String bookLanguage;
                String bookPreviewLink;

                // For each book in the BookArray, create an {@link book} object
                for (int i = 0; i < BookArray.length(); i++) {

                    // Get a single book at position i within the list of books
                    JSONObject currentBook = BookArray.getJSONObject(i);

                    // For a given book, extract the JSONObject associated with the
                    // key called "volumeInfo", which represents a list of all properties
                    // for that book.
                    JSONObject volumeInfo = currentBook.getJSONObject(KEY_VOLUMEINFO);

                    // if there are images get image link else image link is null
                    if (volumeInfo.has(KEY_IMAGELINKS)) {
                        JSONObject imageLinks = volumeInfo.getJSONObject(KEY_IMAGELINKS);
                        bookImage = imageLinks.getString(KEY_SMALLTHUMBNAIL);

                    } else {
                        bookImage = null;
                    }

                    // if there is a title get book title else book title is null
                    if (volumeInfo.has(KEY_TITLE)) {
                        bookTitle = volumeInfo.getString(KEY_TITLE);
                    } else {
                        bookTitle = null;
                    }

                    // if there are authors get book authors from JSONArray authors or else authors is null
                    JSONArray authors = volumeInfo.optJSONArray(KEY_AUTHORS);
                    if (authors != null) {
                        // if there is just one author
                        if (authors.length() == 1) {
                            bookAuthor = authors.getString(0);
                            //if there are multiple authors
                        } else {
                            for (int j = 0; j < authors.length(); j++) {
                                bookAuthor += "\n" + authors.getString(j);
                            }
                        }
                    } else {
                        bookAuthor = null;
                    }

                    // if there is a a book description else book description is null
                    if (volumeInfo.has(KEY_DESCRIPTION)) {
                        bookDescription = volumeInfo.getString(KEY_DESCRIPTION);
                    } else {
                        bookDescription = "no description available";
                    }

                    // if there is a a book language else book language is null
                    if (volumeInfo.has(KEY_LANGUAGE)) {
                        bookLanguage = volumeInfo.getString(KEY_LANGUAGE);
                    } else {
                        bookLanguage = null;
                    }

                    // if there is a a book previewlink else previewlink is null
                    if (volumeInfo.has(KEY_PREVIEWLINK)) {
                        bookPreviewLink = volumeInfo.getString(KEY_PREVIEWLINK);
                    } else {
                        bookPreviewLink = null;
                    }


                    // Create a new {@link bookitem} object from the JSON response.
                    BookItem bookitem = new BookItem(bookImage, bookTitle, bookAuthor, bookLanguage, bookDescription, bookPreviewLink);

                    Log.v("QueryUtils", bookImage + bookAuthor + bookDescription + bookLanguage + bookTitle + bookPreviewLink);
                    // Add the new {@link book} to the list of books.
                    BookItems.add(bookitem);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.

            Log.e(LOG_TAG, "Problem parsing the BookItem JSON results", e);
        }

        // Return the list of BookItems
        return BookItems;
    }

}


