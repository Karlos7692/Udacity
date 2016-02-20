package com.nelson.karl.popularmovies.data.parsers;

import com.nelson.karl.popularmovies.data.model.Movie;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Returns a movie class with specific details only from the details API.
 * This movie stub contains only runtime/duration.
 */
public class MovieDetailsParser implements JsonParser<Movie> {

    private static final String RUNTIME = "runtime";

    private Movie mMovieToUpdate;

    public MovieDetailsParser(Movie movie) {
        mMovieToUpdate = movie;
    }

    @Override
    public Movie parse(String result) throws JSONException {
        JSONObject object = new JSONObject(result);
        mMovieToUpdate.setDuration(object.getInt(RUNTIME));
        return mMovieToUpdate;
    }
}
