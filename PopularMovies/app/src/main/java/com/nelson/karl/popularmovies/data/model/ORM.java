package com.nelson.karl.popularmovies.data.model;

import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Karl on 5/10/2015.
 */
public interface ORM<T> {

    public abstract T get(Cursor cursor);

}
