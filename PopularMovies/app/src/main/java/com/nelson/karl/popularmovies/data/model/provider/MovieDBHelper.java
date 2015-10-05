package com.nelson.karl.popularmovies.data.model.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nelson.karl.popularmovies.data.model.Movie;

/**
 * Created by Karl on 5/10/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movies.db";

    public MovieDBHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_MOVIE_TABLE = "CREATE TABLE" + MovieContract.MovieEntry.TABLE_NAME
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.POSTER_PATH + " TEXT UNIQUE NOT NULL, "
                + MovieContract.MovieEntry.USER_RATING + " REAL NOT NULL, "
                + MovieContract.MovieEntry.RELEASE_DATE + " INTEGER NOT NULL";

        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Online movie cache. Discard data if we change the database and reinitialize.
        //Should thus be version independent.
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
