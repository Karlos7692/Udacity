package com.nelson.karl.popularmovies.data.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.nelson.karl.popularmovies.R;

/**
 * Created by Karl on 17/08/2015.
 */
public class APIUtil {

    //API Keys
    private static final String API_KEY_QUERY = "api_key";
    //TODO Insert API key here.
    private static final String API_KEY = "da0d214971cf7d332409622116e8aac1";

    //Base Urls
    private static final String MOVIES_BASE_URL = "http://api.themoviedb.org/3";
    private static final String MOVIES_THUMB_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com";

    private static final String MOVIE_PATH = "movie";
    private static final String VIDEOS_PATH = "videos";
    private static final String DISCOVER_MOVIES_PATH = "/discover/movie";

    private static final String YOUTUBE_WATCH_PATH = "watch";
    private static final String YOUTUBE_VIDEO_KEY="v";

    private static final String IMAGE_SIZE = "w185/";

    private static final String SORT_BY = "sort_by";
    private static final String DESC = ".desc";

    //Base Uris
    private static final Uri MOVIES_BASE_URI = Uri.parse(MOVIES_BASE_URL);
    public static final Uri MOVIES_THUMB_BASE_URI = Uri.parse(MOVIES_THUMB_BASE_URL);
    private static final Uri YOUTUBE_BASE_URI = Uri.parse(YOUTUBE_BASE_URL);

    public static Uri discoverMovies( Context context ) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );

        String sortBy = preferences.getString(
                context.getString(R.string.pref_discover_movies_key),
                context.getString(R.string.discover_popular_movies)
        );

            return MOVIES_BASE_URI.buildUpon()
                    .appendPath(DISCOVER_MOVIES_PATH)
                    .appendQueryParameter(SORT_BY, sortBy+DESC)
                    .appendQueryParameter(API_KEY_QUERY, API_KEY).build();

    }

    public static Uri getTrailers( long movieID ) {
        return MOVIES_BASE_URI.buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(Long.toString(movieID))
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter(API_KEY_QUERY, API_KEY).build();
    }

    public static Uri getImage( String relativeImagePath ) {
        return MOVIES_THUMB_BASE_URI.buildUpon()
                .appendEncodedPath(IMAGE_SIZE)
                .appendEncodedPath(relativeImagePath)
                .build();
    }

    public static Uri getTrailer( String trailerYoutubeValue ) {
        return YOUTUBE_BASE_URI.buildUpon()
                .appendPath(YOUTUBE_WATCH_PATH)
                .appendQueryParameter(YOUTUBE_VIDEO_KEY, trailerYoutubeValue)
                .build();
    }

}
