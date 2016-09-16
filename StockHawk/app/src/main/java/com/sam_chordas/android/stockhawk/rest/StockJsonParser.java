package com.sam_chordas.android.stockhawk.rest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Karl on 25/08/2016.
 */

public class StockJsonParser {
    private static final String JSON_CHANGE = "Change";
    public static final String JSON_STOCK_SYMBOL = "symbol";
    private static final String JSON_BID_PRICE = "Bid";
    private static final String JSON_CHANGE_PERCENT = "ChangeinPercent";

    static boolean isValidStockJson(final JSONObject stock) {
        return !stock.isNull(JSON_STOCK_SYMBOL) && !stock.isNull(JSON_BID_PRICE)
                && !stock.isNull(JSON_CHANGE_PERCENT) && !stock.isNull( JSON_CHANGE );
    }


    static String parseSymbol(final JSONObject stock) {
        try {
            return stock.getString(JSON_STOCK_SYMBOL);
        } catch (JSONException ignored) {
            return null;
        }
    }

    static String parseChange(final JSONObject stock) {
        try {
            return stock.getString(JSON_CHANGE);
        } catch (JSONException ignored) {
            return null;
        }
    }

    static String parseBid(final JSONObject stock) {
        try {
            return stock.getString(JSON_BID_PRICE);
        } catch (JSONException ignored) {
            return null;
        }
    }

    static String parseChangePercent(final JSONObject stock) {

        try {
            return stock.getString(JSON_CHANGE_PERCENT);
        } catch (JSONException ignored) {
            return null;
        }
    }
}
