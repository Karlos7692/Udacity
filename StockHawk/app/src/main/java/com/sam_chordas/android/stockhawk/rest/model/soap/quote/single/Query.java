package com.sam_chordas.android.stockhawk.rest.model.soap.quote.single;

/**
 * Created by Karl on 4/09/2016.
 */

public class Query {

    private final int count;
    private final Results results;
    public Query( final int count, final Results results )
    {
        this.count = count;
        this.results = results;
    }

    public Results getResults() {
        return results;
    }

    public int getCount() {
        return count;
    }
}
