package com.nelson.karl.popularmovies.data.model.orm;

/**
 * Created by Karl on 21/01/2016.
 */
public abstract class ObjectRetriever<T> implements ObjectRetrieverStrategy<T> {

    private int mEntryOffset = 0;

    public ObjectRetriever()
    {

    }

    public ObjectRetriever(int offset)
    {
        mEntryOffset = offset;
    }

    public int getOffset()
    {
        return mEntryOffset;
    }
}
