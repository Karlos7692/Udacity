package com.nelson.karl.popularmovies.data.model.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.Review;

/**
 * Created by Karl on 5/10/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movies.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ("
                + MovieContract.MovieEntry.ID + " INTEGER PRIMARY KEY, "
                + MovieContract.MovieEntry.TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.SYNOPSIS + " TEXT, "
                + MovieContract.MovieEntry.POSTER_PATH + " TEXT UNIQUE NOT NULL, "
                + MovieContract.MovieEntry.USER_RATING + " REAL NOT NULL, "
                + MovieContract.MovieEntry.RELEASE_DATE + " INTEGER NOT NULL, "
                + MovieContract.MovieEntry.POPULARITY + " REAL NOT NULL, "
                + MovieContract.MovieEntry.VOTE_AVERAGE + " REAL NOT NULL, "
                + MovieContract.MovieEntry.DURATION + " INTEGER NOT NULL,"
                + MovieContract.MovieEntry.IS_FAVOURITE + " INTEGER NOT NULL );";

        db.execSQL(CREATE_MOVIE_TABLE);

        final String CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME
                + " ( " + MovieContract.TrailerEntry._ID + " TEXT PRIMARY KEY, "
                + MovieContract.TrailerEntry.URI + " TEXT UNIQUE NOT NULL,"
                + MovieContract.TrailerEntry.MOVIE + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + MovieContract.TrailerEntry.MOVIE + ") REFERENCES "
                + MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.ID + ") );";
        ;

        db.execSQL(CREATE_TRAILER_TABLE);

        final String CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME
                + " ( " + MovieContract.ReviewEntry._ID + " TEXT PRIMARY KEY, "
                + MovieContract.ReviewEntry.AUTHOR + " TEXT NOT NULL, "
                + MovieContract.ReviewEntry.CONTENT + " TEXT, "
                + MovieContract.ReviewEntry.MOVIE + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + MovieContract.ReviewEntry.MOVIE + " )  REFERENCES "
                + MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry.ID + ") );";


        db.execSQL(CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Online movie cache. Discard data if we change the database and reinitialize.
        //Should thus be version independent.
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(db);
    }

}
