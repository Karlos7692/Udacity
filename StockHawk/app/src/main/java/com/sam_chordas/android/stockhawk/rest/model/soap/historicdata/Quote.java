package com.sam_chordas.android.stockhawk.rest.model.soap.historicdata;

/**
 * Created by Karl on 28/08/2016.
 */

public class Quote {

    private String Date;
    private double Close;

    public Quote(final String Date, final double close )
    {
        this.Date = Date;
        this.Close = close;
    }
    public String getDate() {
        return Date;
    }

    public double getClose() {
        return Close;
    }

    @Override
    public String toString() {
        return "{ SingleQuote: " + Date + Close + "}";
    }

    public void setSymbol(String symbol) {
        this.Date = symbol;
    }

    public void setClose(double close) {
        this.Close = close;
    }
}
