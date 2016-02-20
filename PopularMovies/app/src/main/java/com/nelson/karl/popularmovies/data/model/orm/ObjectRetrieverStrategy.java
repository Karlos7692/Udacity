package com.nelson.karl.popularmovies.data.model.orm;

import android.database.Cursor;

/**
 * Created by Karl on 12/01/2016.
 */
public interface ObjectRetrieverStrategy<T> {

    public T apply( Cursor cursor );

}
