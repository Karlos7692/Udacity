package com.nelson.karl.popularmovies.data.web.strategies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.nelson.karl.popularmovies.data.parsers.JsonParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class DownloadStrategy<Params, Result> {

    private Context mContext;
    private JsonParser<Result> mParser;

    public DownloadStrategy( Context context, JsonParser<Result> parser) {
        mContext = context;
        mParser = parser;
    }

    @SafeVarargs
    public final Result apply( Params... params ) {

        //Parse the list of movies from JSON;
        Result result = null;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecas

            Uri uri = getDownloadUri(params);
            URL url = new URL(uri.toString());

            Log.d(getLogTag(), uri.toString());

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
                Log.d(getLogTag(), "buffer: " + "is null!!!!!");
                return null;
            }
            Log.d(getLogTag(), "buffer: " + buffer.toString());
            try {
                // Parse result
                result = mParser.parse(buffer.toString());
                doAdditionalStrategies(result);
                return result;
            } catch (JSONException e) {
                Log.d(getLogTag(), "Could not parse Json.");
            }

        } catch (IOException e) {
            Log.e(getLogTag(), "Error ", e.getCause());
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(getLogTag(), "Error closing stream", e);
                }
            }
        }

        //Only happens if there was an error.
        return null;
    }

    /**
     * Kick off any additional tasks to be done before returning entire object back to user.
     * @param result the result type fully updated.
     */
    public void doAdditionalStrategies(Result result) {
        // Do nothing, let children override these functions if need be!
    }

    /**
     * Launch any sub tasks required but not needed at the moment.
     * @param result
     */


    public void updateDB(Result result) {
        // Do nothing, let children override these functions if need be!
    }

    public abstract Uri getDownloadUri(Params... params);

    public Context getContext() {
        return mContext;
    }

    public abstract String getLogTag();
}
