package com.nelson.karl.popularmovies.data.model.orm;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Karl on 24/01/2016.
 */
public class QueryModel<T extends ObjectModel<T>> {

    private Cursor mCursor;
    private final ObjectRetrieverStrategy<T> mRetrieverStrategy;
    private Uri mQueryUri;
    public QueryModel(ObjectRetrieverStrategy<T> retrieverStrategy) {
        mRetrieverStrategy = retrieverStrategy;
    }

    /**
     * We have implemented most of the query models in the content provider.
     * So this class makes basic assumptions, for example; the projection, selection
     * etc. In that matter we only need the Uri.
     * @param context context we are querying on.
     * @param uri query uri of the model you wish to find.
     * @return the updated interface for the query.
     */
    public QueryModel<T> find( Context context, Uri uri ) {
        if ( mCursor == null ) {
            mQueryUri = uri;
            mCursor = context.getContentResolver().query(mQueryUri, null, null, null, null);
        }
        return this;
    }

    public boolean exists(Cursor cursor) {
        return cursor.getCount() != 0;
    }

    public boolean exists() {
        return mCursor != null && exists(mCursor);
    }

    public T get() {
        if ( mCursor == null ) {
            return null;
        }
        return T.get(mRetrieverStrategy, mCursor);
    }

    public ObjectModelList<T> getObjects() {
        if ( mCursor == null ) {
            return null;
        }
        return ObjectModelList.get(mRetrieverStrategy, mQueryUri, mCursor);
    }

    public QueryModel<T> requery( Context context ) {
        if ( mQueryUri != null ) {
            mCursor = context.getContentResolver().query(mQueryUri, null, null, null, null);
        }
        return this;
    }

    public void close() {
        mCursor.close();
        mCursor = null;
    }
}
