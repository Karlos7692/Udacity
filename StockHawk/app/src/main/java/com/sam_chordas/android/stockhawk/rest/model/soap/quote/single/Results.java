package com.sam_chordas.android.stockhawk.rest.model.soap.quote.single;

import com.sam_chordas.android.stockhawk.rest.model.soap.quote.Quote;

/**
 * Created by Karl on 4/09/2016.
 */

public class Results {

    private final Quote quote;
    public Results( final Quote quote )
    {
        this.quote = quote;
    }

    public Quote getQuote() {
        return quote;
    }
}
