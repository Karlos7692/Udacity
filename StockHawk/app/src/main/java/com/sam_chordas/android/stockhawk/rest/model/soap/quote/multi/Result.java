package com.sam_chordas.android.stockhawk.rest.model.soap.quote.multi;

/**
 * Created by Karl on 4/09/2016.
 */

public class Result {

    private final Query query;
    public Result(final Query query )
    {
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }
}
