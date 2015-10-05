package com.nelson.karl.popularmovies.data.model.provider;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by Karl on 5/10/2015.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.nelson.karl.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIE_PATH = "movies";
    public static final String REVIEW_PATH = "reviews";

    public static class MovieEntry {
        public static final String TABLE_NAME = "Movie";
        public static final String _ID = "id";
        public static final String TITLE = "title";
        public static final String POSTER_PATH = "poster_path";
        public static final String USER_RATING = "user_rating";
        public static final String RELEASE_DATE = "release_date";
        public static final String POPULARITY = "popularity";
        public static final String VOTE_AVERAGE = "vote_average";

        public static final String[] ENTRY_COLUMNS = {
                TABLE_NAME+"."+_ID,
                TITLE,
                POSTER_PATH,
                USER_RATING,
                RELEASE_DATE,
                POPULARITY
        };

        public static final int COL_ID = 0;
        public static final int COL_TITLE = 1;
        public static final int COL_POSTER_PATH = 2;
        public static final int COL_USER_RATING = 3;
        public static final int COL_RELEASE_DATE = 4;
        public static final int COL_POPULARITY = 5;
        public static final int COL_VOTE_AVG = 6;

        public static Uri buildMovieUri(long id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(MOVIE_PATH)
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static Uri buildDiscoverMovieUri() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(MOVIE_PATH)
                    .build();
        }

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/"+CONTENT_AUTHORITY + "/" + MOVIE_PATH;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
        + "/"+CONTENT_AUTHORITY + "/" + MOVIE_PATH;
    }

    public static class ReviewEntry {

    }
}
