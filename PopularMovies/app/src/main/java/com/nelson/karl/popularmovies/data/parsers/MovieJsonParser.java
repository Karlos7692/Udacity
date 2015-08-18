package com.nelson.karl.popularmovies.data.parsers;

import android.util.Log;

import com.nelson.karl.popularmovies.data.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Karl on 17/08/2015.
 */
public class MovieJsonParser {

    public static final String LOG_TAG = "Movie Json Parser";

    public static final String MOVIES_RESULT_ARRAY = "results";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "original_title";
    private static final String MOVIE_POSTER_PATH = "poster_path";
    private static final String MOVIE_SYNOPSIS = "overview";
    private static final String MOVIE_USER_RATING = "vote_average";
    private static final String MOVIE_RELEASE_DATE = "release_date";

    private static final String DATE_FORMAT_STR = "yyyy-mm-dd";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR,
            Locale.getDefault());



    public static List<Movie> parseMovies( String result ) throws JSONException {
        JSONObject jsonObject = new JSONObject( result );

        List<Movie> movies = new ArrayList<>();

        JSONArray resultsArray = jsonObject.getJSONArray( MOVIES_RESULT_ARRAY );
        for ( int i=0; i<resultsArray.length(); i++ ) {

            try {
                Movie movie = parseMovie( resultsArray.getJSONObject(i) );
                movies.add( movie );
            } catch ( JSONException e ) {
                //Do not assume data is valid, if invalid should not affect other movies.
                //If we see an error we should fix this.
                Log.d( LOG_TAG, e.getMessage() );
            }

        }
        return movies;
    }

    public static Movie parseMovie( JSONObject jsonMovie ) throws JSONException {
        Movie movie = new Movie();

        //Required attributes
        movie.setId( parseID( jsonMovie) );
        movie.setTitle( parseTitle( jsonMovie ) );
        movie.setPosterPath(parsePosterPath(jsonMovie));

        movie.setSynopsis( parseSynopsis( jsonMovie) );
        movie.setUserRating( parseUserRating( jsonMovie ) );
        movie.setReleaseDate( parseReleaseDate( jsonMovie ) );

        return movie;
    }

    /* Movie requires an id */
    private static long parseID( JSONObject jsonMovie ) throws JSONException {
        return jsonMovie.getLong( MOVIE_ID );
    }

    /* Movie title is required */
    private static String parseTitle( JSONObject jsonMovie ) throws JSONException {
        return jsonMovie.getString( MOVIE_TITLE );
    }

    private static String parsePosterPath(JSONObject jsonMovie) throws JSONException {
            return jsonMovie.getString( MOVIE_POSTER_PATH );
    }

    private static String parseSynopsis( JSONObject jsonMovie ) {
        String synopsis = "";

        try {
            synopsis = jsonMovie.getString( MOVIE_SYNOPSIS );
        } catch ( JSONException e ) {
            Log.d( LOG_TAG, e.getMessage() );
        }

        return synopsis;
    }

    private static double parseUserRating( JSONObject jsonMovie ) {
        double ret = Movie.INVALID_RATING;

        try {
            ret = jsonMovie.getDouble( MOVIE_USER_RATING );
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

        return ret;
    }


    private static Date parseReleaseDate( JSONObject jsonMovie ) {
        Date date = null;
        try {
            String dateStr = jsonMovie.getString( MOVIE_RELEASE_DATE );
            date = DATE_FORMAT.parse( dateStr );
        } catch ( ParseException e ) {
            Log.d(LOG_TAG, e.getMessage());
        } catch ( JSONException e ) {
            e.printStackTrace();
        }

        return date;
    }

}
