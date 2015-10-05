package com.nelson.karl.popularmovies.data.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.parsers.MovieJsonParser;
import com.nelson.karl.popularmovies.data.parsers.TrailerJsonParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Karl on 4/10/2015.
 */
public class TrailerDownloadTask extends AsyncTask<String, Void, List<String>>{

    private static final String LOG_TAG = "Trailer Download Task:";
    private Context mContext;
    public TrailerDownloadTask( Context context ) {
        mContext = context;
    }

    @Override
    protected List<String> doInBackground(String... params) {
        return null;
    }

    private List<String> getData( String... params ) {

        // Verify that we need to download something.
//        if (params.length == 0) {
//            return null;
//        }

        //Parse the list of movies from JSON;
        List<String> trailers = null;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecas

            Uri trailersUri = APIUtil.discoverMovies( mContext );
            URL url = new URL( trailersUri.toString() );

            Log.d(LOG_TAG, trailersUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.d(LOG_TAG, "buffer: " + "is null!!!!!");
                return null;
            }
            Log.d(LOG_TAG, "buffer: " + buffer.toString());
            try {
                trailers = TrailerJsonParser.parseTrailers( buffer.toString() );
                return trailers;
            } catch (JSONException e) {
                Log.d( LOG_TAG, "Could not parse Json to movies" );
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e.getCause());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        //Only happens if there was an error.
        return null;
    }
}
