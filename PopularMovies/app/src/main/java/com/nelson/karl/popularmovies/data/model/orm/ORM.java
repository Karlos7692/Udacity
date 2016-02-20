package com.nelson.karl.popularmovies.data.model.orm;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.nelson.karl.popularmovies.data.model.Trailer;

/**
 * Created by Karl on 13/01/2016.
 */
public interface ORM {

    public void insert( Context context );

    public void update( Context context );

    public void delete( Context context );

    public Uri getUri();

    //public void shutdown( Context context );

}
