package com.nelson.karl.popularmovies.data.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nelson.karl.popularmovies.MovieAdapter;
import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.parsers.MovieJsonParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Karl on 16/08/2015.
 */
public class MovieDownloadTask extends AsyncTask<String, Void, List<Movie>>{

    private static final String LOG_TAG = "Movie Download Task";

    private Context mContext;
    private MovieAdapter mDataAdapter;

    public MovieDownloadTask( Context context, MovieAdapter dataAdapter ) {
        mContext = context;
        mDataAdapter = dataAdapter;
    }

    @Override
    protected List<Movie> doInBackground( String... params ) {
        return getData( params );
    }

    @Override
    protected void onPostExecute( List<Movie> movies ) {
        if ( movies != null && mDataAdapter != null ) {
            mDataAdapter.clear();
            for ( Movie movie : movies ) {
                mDataAdapter.add( movie );
            }
        }
    }

    private List<Movie> getData( String... params ) {

        // Verify that we need to download something.
//        if (params.length == 0) {
//            return null;
//        }

        //Parse the list of movies from JSON;
        List<Movie> movies = null;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecas

            Uri discoverMovies = APIUtil.discoverMovies( mContext );
            URL url = new URL( discoverMovies.toString() );

            Log.d(LOG_TAG, "dm: " + discoverMovies.toString() );

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
                movies = MovieJsonParser.parseMovies( buffer.toString() );
                return movies;
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
