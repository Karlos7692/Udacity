package com.sam_chordas.android.stockhawk.rest.model.soap.quote.multi;

import com.sam_chordas.android.stockhawk.rest.model.soap.quote.Quote;

import java.util.List;

/**
 * Created by Karl on 4/09/2016.
 */

public class Results {

    private final List<Quote> quote;
    public Results( final List<Quote> quote )
    {
        this.quote = quote;
    }

    public List<Quote> getQuotes() {
        return quote;
    }
}
