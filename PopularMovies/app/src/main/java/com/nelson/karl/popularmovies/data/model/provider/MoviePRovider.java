package com.nelson.karl.popularmovies.data.model.provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract.*;
import com.nelson.karl.popularmovies.data.utils.Utility;

import android.net.Uri;

public class MovieProvider extends ContentProvider {

    private static final int MOVIE = 100;
    private static final int TRAILER = 200;
    private static final int REVIEW = 300;

    private static final int DISCOVER_MOVIES = 1000;
    public static final int REVIEWS = 1100;
    public static final int TRAILERS = 1200;

    private MovieDBHelper mHelper;
    private UriMatcher mUriMatcher;

    private final static SQLiteQueryBuilder sMovieQuery = new SQLiteQueryBuilder();
    static {
        sMovieQuery.setDistinct(true);
        sMovieQuery.setTables(
                MovieEntry.TABLE_NAME + " m1 LEFT OUTER JOIN " +
                TrailerEntry.TABLE_NAME + " t ON " +
                "m1." + MovieEntry.ID + " = t." + TrailerEntry.MOVIE +
                ", " + MovieEntry.TABLE_NAME + " m2 LEFT OUTER JOIN " +
                ReviewEntry.TABLE_NAME + " r ON " +
                "m2." + MovieEntry.ID + "= r." + ReviewEntry.MOVIE
        );
    }

    @Override
    public boolean onCreate() {
        mHelper = new MovieDBHelper(getContext());
        mUriMatcher = buildUriMatcher();
        return true;
    }

    public UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Match a singular movie by unique api id.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIE_PATH+"/#", MOVIE);

        //Match a list of trailers referring to a particular movie with id #.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.TRAILER_PATH+"/#", TRAILERS);
        //Match a singular trailer by id.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.TRAILER_PATH+"/*", TRAILER);
        //Match all reviews given a movie id #.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.REVIEW_PATH+"/#", REVIEWS);

        //Match a singular review by id.
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.REVIEW_PATH+"/*", REVIEW);

        //Discover movies
        matcher.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.MOVIE_PATH, DISCOVER_MOVIES);
        return matcher;

    }

    private final static String[] movieTrailerOuterProjection = Utility.extend(
            Utility.prepend("m1.", MovieEntry.ENTRY_COLUMNS),
            Utility.prepend("t.", TrailerEntry.ENTRY_COLUMNS),
            new String[MovieEntry.ENTRY_COLUMNS.length + TrailerEntry.ENTRY_COLUMNS.length]);

    private final static String[] movieReviewOuterProjection = Utility.prepend("r.", ReviewEntry.ENTRY_COLUMNS);

    private final static String[] sMovieProjection = Utility.extend(movieTrailerOuterProjection,
            movieReviewOuterProjection,
            new String[movieTrailerOuterProjection.length + movieReviewOuterProjection.length]);

    private final static String sMovieSelection = String.format("%s = ? AND %s = ?",
            "m1."+MovieEntry.ID, "m2."+ MovieEntry.ID);

    private Cursor queryMovie(Uri uri) {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        final String movieRef = Long.toString(MovieEntry.getIdFromUri(uri));

        return sMovieQuery.query(db, sMovieProjection, sMovieSelection, new String[]{ movieRef, movieRef },
                null, null, null);
    }

    private static final String sTrailerSelection = String.format("%s = ?", TrailerEntry._ID);

    private Cursor queryTrailer(Uri uri, String[] projection, String sortOrder) {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        final String id = TrailerEntry.getIdFromURI(uri);

        return db.query( TrailerEntry.TABLE_NAME, projection, sTrailerSelection, new String[] { id },
                null, null, sortOrder );
    }

    private static final String sReviewSelection = new StringBuilder()
            .append(ReviewEntry._ID)
            .append(" = ?").toString();

    private Cursor queryReview(Uri uri, String sortOrder) {
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        final String id = ReviewEntry.getIdFromUri(uri);

        return db.query(ReviewEntry.TABLE_NAME, ReviewEntry.ENTRY_COLUMNS, sReviewSelection,
                new String[]{id}, null, null, sortOrder);
    }


    private Cursor queryTrailers(Uri uri, String sortOrder) {
        final SQLiteDatabase db = mHelper.getReadableDatabase();

        return db.query(TrailerEntry.TABLE_NAME, TrailerEntry.ENTRY_COLUMNS,
                String.format("%s = ?", TrailerEntry.MOVIE),
                new String[] { Long.toString(TrailerEntry.getMovieIdFromUri(uri)) },
                null, null, null, sortOrder);
    }


    private Cursor queryReviews(Uri uri, String sortOrder ) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        return db.query(ReviewEntry.TABLE_NAME,
                ReviewEntry.ENTRY_COLUMNS,
                String.format("%s = ?", ReviewEntry.MOVIE),
                new String[] { Long.toString(ReviewEntry.getMovieIdFromUri(uri)) },
                null, null, null, sortOrder );
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final Cursor ret;
        switch(mUriMatcher.match(uri)) {
            case MOVIE:
                ret = queryMovie(uri);
                break;
            case TRAILER:
                ret = queryTrailer(uri, projection, sortOrder);
                break;
            case TRAILERS:
                ret = queryTrailers(uri, sortOrder);
                break;
            case REVIEW:
                ret = queryReview(uri, sortOrder);
                break;
            case REVIEWS:
                ret = queryReviews(uri, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }


    @Override
    public String getType(Uri uri) {

        switch ( mUriMatcher.match(uri) ) {
            case MOVIE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return TrailerEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return ReviewEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return ReviewEntry.CONTENT_TYPE;
            case DISCOVER_MOVIES:
                return MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                db.insert(MovieEntry.TABLE_NAME, null, values);
                break;
            case TRAILER:
                db.insert(TrailerEntry.TABLE_NAME, null, values);
                break;
            case REVIEW:
                db.insert(ReviewEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    private int insertObjectModels(Uri uri, String tableName, ContentValues[] values ) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int nInserted = 0;
        try {
            db.beginTransaction();
            for ( ContentValues value : values ) {
                long _id = db.insert(tableName, null, value );
                if ( _id != -1 ) {
                    nInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return nInserted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        switch(mUriMatcher.match(uri)) {
            case TRAILERS:
                return insertObjectModels( uri, TrailerEntry.TABLE_NAME, values );
            case REVIEWS:
                return insertObjectModels( uri, ReviewEntry.TABLE_NAME, values );
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        final int rowsDeleted;
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(TrailerEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(ReviewEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int rowsUpdated;
        switch (mUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mHelper.close();
        super.shutdown();
    }
}
