package com.nelson.karl.popularmovies.data.model.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Karl on 13/01/2016.
 * We are not supporting many to many relationship. ObjectModelList -> ObjectModelList.
 */
public class ObjectModelList<T extends ObjectModel> extends ArrayList<T> implements ORM {

    private Uri mQueryUri;

    // Force user to give bulk insert uri.
    // We also want a simplistic insert for any "has" relationship
    // TODO refactor if passing uri as parameter is cleaner.
    private ObjectModelList() {

    }

    public ObjectModelList( Uri queryUri ) {
        mQueryUri = queryUri;
    }

    public static <T extends ObjectModel> ObjectModelList<T> get( ObjectRetrieverStrategy<T> strategy, Uri queryUri, Cursor cursor )
    {
        ObjectModelList<T> list = new ObjectModelList<>(queryUri);
        T model;
        while ( ( model = strategy.apply(cursor)) != null ) {
            if ( !list.contains(model) ) {
                list.add(model);
            }
            cursor.moveToNext();
        }
        return list;
    }

    @Override
    public void insert(Context context) {
        ContentValues[] values = new ContentValues[this.size()];
        for ( int i=0; i < this.size(); i++ ) {
            values[i] = this.get(i).toContentValues();
        }
        context.getContentResolver().bulkInsert(mQueryUri, values);
    }

    public void update(Context context) {
        for ( int i=0; i < this.size(); i++ ) {
            T model = this.get(i);
            context.getContentResolver().update(model.getUri(), model.toContentValues(),
                    model.getSelection(), model.getDBIdentifierValues());
        }

    }
    @Override
    public void delete( Context context ) {
        for ( T model : this ) {
            model.delete( context );
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public Uri getUri() {
        return mQueryUri;
    }

}
