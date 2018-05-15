package com.michaellundie.fabapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from Google Books API.
 */
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     */
    private QueryUtils() {
    }

    /**
     * Method for building our query URL
     * @param context The current activity context.
     * @param searchInput The search input from the user.
     * @return url string
     */
    public static String queryRequestBuilder (Context context, String searchInput, String language){

        //Set up our variables ready for our string builder
        //NOTE: Is it better to initialise these outside of this method? Are they recreated and
        // destroyed every time the method is called?
        final String API_AUTHORITY = context.getResources().getString(R.string.api_authority);
        final String API_BOOKS_PATH = "books";
        final String API_VERSION = context.getResources().getString(R.string.api_version);
        final String API_VOLUMES_PATH = "volumes";
        final String API_QUERY_PARAM = "q";
        final String API_RESULTS_PARAM = "maxResults";
        final String API_LANG_PARAM = "langRestrict";
        final String API_RETURN_VALUE = context.getResources().getString(R.string.api_return_value);

        //Use URL builder to construct our URL
        Uri.Builder query = new Uri.Builder();
        query.scheme("https")
                .authority(API_AUTHORITY)
                .appendPath(API_BOOKS_PATH)
                .appendPath(API_VERSION)
                .appendPath(API_VOLUMES_PATH)
                .appendQueryParameter(API_QUERY_PARAM, searchInput)
                .appendQueryParameter(API_LANG_PARAM, language)
                .appendQueryParameter(API_RESULTS_PARAM, API_RETURN_VALUE)
                .build();
        URL returnUrl = null;

        //Attempt to return our URL, check for exception, then convert to String on return.
        try {
            returnUrl = new URL(query.toString());
        } catch (MalformedURLException e) {
            //We'll do further checking in AsyncLoader, but perhaps it's nice to check for
            //any initial errors.
            Log.e(LOG_TAG, "There is a problem with URL construction.", e);
        }
        //Handle any null pointer exception that may be thrown by .toString() method;
        if (returnUrl == null) {
            Log.i(LOG_TAG, "URL returned null.");
            return null;
        } return returnUrl.toString();
    }

    /**
     * Query the Google Books API and return an {@link List<BookItem>} object to represent a.
     * list of earthquakes
     * @param requestUrl the URL for our API data request
     * @return parsed JSON query results (as a bookitem object)
     */
    public static ArrayList<BookItem> fetchBookResults(String requestUrl) {
        Log.i(LOG_TAG, "TEST: fetchBookResults: method called");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and return a List<BookItem> object
        return extractBookResults(jsonResponse);
    }

    //TODO: Handle Null Pointer Exception
    /**
     * Checks to make sure the smart phone has access to the internet.
     * @param context the application context
     * @return boolean
     */
    public static boolean checkNetworkAccess(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Check the connectivity manager is not null first to avoid NPE.
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            // Returns true or false depending on connectivity status.
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        //Connectivity manager is null so returning false.
        return false;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
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
            Log.e(LOG_TAG, "Problem retrieving the Google Books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
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
     * parsing a JSON response.
     */
    private static ArrayList<BookItem> extractBookResults(String bookQueryJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookQueryJSON)) {
            return null;
        }

        // Create an empty List that we can start adding earthquakes to
        ArrayList<BookItem> bookQueryResults = new ArrayList<>();

        try {
            // Assign our returned query string to a new JSONObject
            JSONObject jsonObj = new JSONObject(bookQueryJSON);

            // Get JSONArray items from string
            JSONArray booksItemsJsa = jsonObj.getJSONArray("items");

            //Set up loop to parse each item in our JSONObject
            for (int bookNumber = 0; bookNumber < booksItemsJsa.length(); bookNumber++) {

                JSONObject currentBookJso = booksItemsJsa.getJSONObject(bookNumber);
                JSONObject bookInfoJso = currentBookJso.getJSONObject("volumeInfo");
                String bookTitle = bookInfoJso.optString("title");

                // Get the authors array and parse returned data.
                JSONArray bookAuthorsJsa = bookInfoJso.optJSONArray("authors");
                ArrayList<String> authors = new ArrayList<>();

                if (bookAuthorsJsa != null) {
                        // Add each author to a Java ArrayList object.
                        for (int authorNumber = 0; authorNumber < bookAuthorsJsa.length(); authorNumber++) {
                            authors.add(bookAuthorsJsa.getString(authorNumber));
                        }
                }
                // Get the book image thumbnail.
                JSONObject imageLinks = bookInfoJso.optJSONObject("imageLinks");
                String thumbnailURL;
                if(imageLinks != null) {
                    thumbnailURL = imageLinks.optString("thumbnail");
                } else {
                    thumbnailURL = null;
                }
                bookQueryResults.add(new BookItem(bookTitle, authors, thumbnailURL, bookNumber));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the JSON results.", e);
    }

        // Return the list of earthquakes
        return bookQueryResults;
    }
}