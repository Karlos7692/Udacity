package com.sam_chordas.android.stockhawk.rest.model.soap.historicdata;

/**
 * Created by Karl on 3/09/2016.
 */

public class Query {
    private final Results results;
    public Query(final Results results )
    {
        this.results = results;
    }

    public Results getResults() {
        return results;
    }
}
