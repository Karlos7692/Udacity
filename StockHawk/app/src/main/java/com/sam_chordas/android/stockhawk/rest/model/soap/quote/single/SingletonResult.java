package com.sam_chordas.android.stockhawk.rest.model.soap.quote.single;

/**
 * Created by Karl on 4/09/2016.
 */

public class SingletonResult {

    private final Query query;
    public SingletonResult(final Query query )
    {
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }
}
