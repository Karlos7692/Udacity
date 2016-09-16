package com.sam_chordas.android.stockhawk.rest.model.soap.historicdata;

/**
 * Created by Karl on 3/09/2016.
 */

public class Result {
    private Query query;

    public Result(final Query query )
    {
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }
}
