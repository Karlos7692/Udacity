package com.nelson.karl.popularmovies.data.model.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.nelson.karl.popularmovies.data.model.Trailer;

import java.net.URI;

/**
 * Created by Karl on 5/10/2015.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.nelson.karl.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIE_PATH = "movies";
    public static final String REVIEW_PATH = "reviews";
    public static final String TRAILER_PATH = "trailers";

    public static class MovieEntry {
        public static final String TABLE_NAME = "movie";
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String SYNOPSIS = "synopsis";
        public static final String POSTER_PATH = "poster_path";
        public static final String USER_RATING = "user_rating";
        public static final String RELEASE_DATE = "release_date";
        public static final String POPULARITY = "popularity";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String DURATION = "duration";
        public static final String IS_FAVOURITE = "is_favourite";

        public static final String[] ENTRY_COLUMNS = {
                ID,
                TITLE,
                SYNOPSIS,
                POSTER_PATH,
                USER_RATING,
                RELEASE_DATE,
                POPULARITY,
                VOTE_AVERAGE,
                DURATION,
                IS_FAVOURITE
        };

        public static final int COL_ID = 0;
        public static final int COL_TITLE = 1;
        public static final int COL_SYNOPSIS = 2;
        public static final int COL_POSTER_PATH = 3;
        public static final int COL_USER_RATING = 4;
        public static final int COL_RELEASE_DATE = 5;
        public static final int COL_POPULARITY = 6;
        public static final int COL_VOTE_AVG = 7;
        public static final int COL_DURATION = 8;
        public static final int COL_IS_FAVOURITE = 9;

        public static final int COL_TRAILER_OFFSET = COL_IS_FAVOURITE + 1;
        public static final int COL_REVIEW_OFFSET = COL_TRAILER_OFFSET + TrailerEntry.ENTRY_COLUMNS.length;

        public static Uri buildDiscoverMoviesUri() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(MOVIE_PATH)
                    .build();
        }

        public static Uri buildMovieUri(long id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(MOVIE_PATH)
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
    }

    public static class TrailerEntry {
        public static final String TABLE_NAME = "trailer";
        public static final String _ID = "id";
        public static final String URI = "uri";
        public static final String MOVIE = "movie";

        public static final String[] ENTRY_COLUMNS = {
                _ID,
                URI,
                MOVIE
        };

        public static final int COL_ID = 0;
        public static final int COL_URI = 1;
        public static final int COL_MOVIE = 2;

        public static final Uri buildTrailerUri(String id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(TRAILER_PATH)
                    .appendPath(id)
                    .build();
        }

        public static final Uri buildTrailersUriFromMovie(long id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(TRAILER_PATH)
                    .appendPath(Long.toString(id))
                    .build();
        }

        private static final int TRAILER_ID_SEGMENT_POSITION = 1;
        public static final String getIdFromURI(Uri uri) {
            return uri.getPathSegments().get(TRAILER_ID_SEGMENT_POSITION);
        }

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + TRAILER_PATH;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + TRAILER_PATH;

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static class ReviewEntry {
        public static final String TABLE_NAME = "review";
        public static final String _ID = "id";
        public static final String AUTHOR = "author";
        public static final String CONTENT = "content";
        public static final String MOVIE = "movie";

        public static final String[] ENTRY_COLUMNS = {
                _ID,
                AUTHOR,
                CONTENT,
                MOVIE
        };

        public static final int COL_ID = 0;
        public static final int COL_AUTHOR = 1;
        public static final int COL_CONTENT = 2;
        public static final int COL_MOVIE = 3;

        public static Uri buildReviewsByMovieUri(long id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(REVIEW_PATH)
                    .appendPath(Long.toString(id))
                    .build();
        }

        public static final Uri buildReviewUri(String id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(REVIEW_PATH)
                    .appendPath(id)
                    .build();
        }

        public static final long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + REVIEW_PATH;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + REVIEW_PATH;

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
