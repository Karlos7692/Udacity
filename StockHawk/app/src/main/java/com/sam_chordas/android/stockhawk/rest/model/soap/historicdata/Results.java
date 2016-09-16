package com.sam_chordas.android.stockhawk.rest.model.soap.historicdata;

import java.util.List;

/**
 * Created by Karl on 3/09/2016.
 */

public class Results {

    private final List<Quote> quote;
    public Results(List<Quote> quote )
    {
        this.quote = quote;
    }

    public List<Quote> getQuotes() {
        return this.quote;
    }
}
