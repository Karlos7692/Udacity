package com.nelson.karl.popularmovies.data.model.orm;

import android.content.Context;

/**
 * Created by Karl on 13/01/2016.
 */
public interface ORM {

    public void insert( Context context );

    public void update( Context context );

    public void delete( Context context );

    //public void shutdown( Context context );

}
