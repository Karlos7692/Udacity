package com.nelson.karl.popularmovies.data.model.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.nelson.karl.popularmovies.R;

/**
 * Created by Karl on 5/10/2015.
 */
public class MovieProvider extends ContentProvider {

    public static final int MOVIE = 100;
    public static final int DISCOVER_MOVIES=200;

    private MovieDBHelper mHelper;
    private UriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mHelper = new MovieDBHelper(getContext());
        mUriMatcher = buildUriMatcher();
        return true;
    }

    public UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Match a singular movie
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIE_PATH+"/#", MOVIE);

        //Discover movies
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.MOVIE_PATH, DISCOVER_MOVIES );

        return matcher;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor ret;
        switch(mUriMatcher.match(uri)) {
            case MOVIE:
                ret = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.ENTRY_COLUMNS,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            case DISCOVER_MOVIES:
                ret = discoverMovies(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }

    public static final String DISCOVER_MOVIES_LIMIT = "20";

    public Cursor discoverMovies( SQLiteDatabase db ) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String sortBy = preferences.getString(
                getContext().getString(R.string.pref_discover_movies_key),
                getContext().getString(R.string.discover_popular_movies)
        );

        return db.query(
                MovieContract.MovieEntry.TABLE_NAME,
                MovieContract.MovieEntry.ENTRY_COLUMNS,
                null,
                null,
                null,
                null,
                sortBy,
                DISCOVER_MOVIES_LIMIT
        );
    }


    @Override
    public String getType(Uri uri) {

        switch ( mUriMatcher.match(uri) ) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case DISCOVER_MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unkown uri:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        //TODO Change for reviews and trailers.
        long _id = -1;
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return MovieContract.MovieEntry.buildMovieUri(_id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        //TODO Change for reviews and trailers.
        int rowsDeleted = 0;
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
