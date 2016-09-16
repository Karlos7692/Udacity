package com.sam_chordas.android.stockhawk.rest.model.soap.quote;

import android.content.ContentProviderOperation;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by Karl on 4/09/2016.
 */

public class Quote {
    private final String Symbol;
    private final String Name;
    private final Float Bid;
    private final String Change;
    private final String ChangeinPercent;
    private final String StockExchange;

    public Quote( final String Symbol, final String Name, final Float Bid, final String Change,
                  final String ChangeInPercent, final String stockExchange )
    {
        this.Symbol = Symbol;
        this.Name = Name;
        this.Bid = Bid;
        this.Change = Change;
        this.ChangeinPercent = ChangeInPercent;
        this.StockExchange = stockExchange;
    }

    public String getSymbol() {
        return Symbol;
    }

    public String getName() {
        return Name;
    }

    public String getStockExchange() {
        return StockExchange;
    }

    public Float getBid() {
        return Bid;
    }

    public String getChangeInPercent() {
        return ChangeinPercent;
    }

    public String getChange() {
        return Change;
    }

    public ContentProviderOperation toInsertOperation()
    {
        final ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);

        final String bid = Bid == null ? "-" : Utils.truncateBidPrice( Bid.toString() );
        final String change = Change == null ? "0.0" : Change;
        final String percentChange = ChangeinPercent == null ? "-"
                : Utils.truncateChange( ChangeinPercent, true );
        final String exchange = StockExchange == null ? "" : StockExchange;
        builder.withValue(QuoteColumns.SYMBOL, Symbol.toUpperCase())
                .withValue(QuoteColumns.COMPANY_NAME, Name)
                .withValue(QuoteColumns.STOCK_EXCHANGE, exchange)
                .withValue(QuoteColumns.BIDPRICE, bid)
                .withValue(QuoteColumns.PERCENT_CHANGE, percentChange)
                .withValue(QuoteColumns.CHANGE, Utils.truncateChange(change, false))
                .withValue(QuoteColumns.ISUP, change.charAt(0) == '-' ? 0 : 1)
                .withValue(QuoteColumns.ISCURRENT, 1);
        return builder.build();
    }

    public boolean isValid() {
        return getName() != null;
    }
}
