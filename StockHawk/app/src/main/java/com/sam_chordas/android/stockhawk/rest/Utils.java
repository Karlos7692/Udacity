package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    public static boolean showPercent = true;

    @NonNull
    public static ArrayList<ContentProviderOperation> quoteJsonToContent(final Context context, final JSONObject jsonObject) throws JSONException {
        final JSONObject query = jsonObject.getJSONObject("query");
        final JSONObject results = query.getJSONObject("results");
        final int resultsCount = query.getInt("count");
        if ( resultsCount== 0 ) return emptyArrayList();
        if ( resultsCount == 1 ) return parseUniqueResult( context, results.getJSONObject( "quote" ) );
        return parseResults( context, results.getJSONArray( "quote" ) );
    }

    @NonNull
    private static ArrayList<ContentProviderOperation> parseUniqueResult(final Context context, final JSONObject quote )
            throws JSONException {
        final ContentProviderOperation operation = buildBatchOperation( context, quote);
        if (operation == null) {
            return emptyArrayList();
        }
        return singletonArrayList(operation);
    }

    @NonNull
    private static ArrayList<ContentProviderOperation> parseResults(final Context context, final JSONArray quote )
            throws JSONException {
        final ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        for (int i = 0; i < quote.length(); i++) {
            final ContentProviderOperation operation = buildBatchOperation( context, quote.getJSONObject(i) );
            if (operation == null) continue;
            operations.add(operation);
        }
        return operations;
    }

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    @Nullable
    public static ContentProviderOperation buildBatchOperation( final Context context, final JSONObject jsonObject)
            throws JSONException {

        final ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);

        if (!StockJsonParser.isValidStockJson(jsonObject)) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
            SharedPreferences.Editor preferenceEditor = preferences.edit();
            preferenceEditor.putString( context.getString( R.string.invalid_stock_symbol_key ),
                    jsonObject.getString( StockJsonParser.JSON_STOCK_SYMBOL ) );
            preferenceEditor.commit();
            return null;
        }

        builder.withValue(QuoteColumns.SYMBOL, StockJsonParser.parseSymbol(jsonObject));
        builder.withValue(QuoteColumns.BIDPRICE,
                truncateBidPrice(StockJsonParser.parseBid(jsonObject)));
        builder.withValue(QuoteColumns.PERCENT_CHANGE,
                truncateChange(StockJsonParser.parseChangePercent(jsonObject), true));

        final String change = StockJsonParser.parseChange(jsonObject);
        assert change != null;

        builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
        builder.withValue(QuoteColumns.ISCURRENT, 1);
        if (change.charAt(0) == '-') {
            builder.withValue(QuoteColumns.ISUP, 0);
        } else {
            builder.withValue(QuoteColumns.ISUP, 1);
        }

        return builder.build();
    }


    public static int getColor( final Context context, final int colorId )
    {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            return context.getColor( colorId );
        }
        return ContextCompat.getColor( context, colorId );
    }

    public static boolean isConnectingOrConnected( final Context context )
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static Intent scheduleDataSync( final Context context )
    {
        final Intent syncIntent = new Intent( context, StockIntentService.class);
        syncIntent.putExtra("tag", "init");
        context.startService( syncIntent );
        return syncIntent;
    }

    public static PeriodicTask setupPeriodicTask( final Context context )
    {
        long period = 3600L;
        long flex = 10L;
        String periodicTag = "periodic";

        // create a periodic task to pull stocks once every hour after the app has been opened. This
        // is so Widget data stays up to date.
        final PeriodicTask task = new PeriodicTask.Builder()
                    .setService( StockTaskService.class )
                    .setPeriod( period )
                    .setFlex( flex )
                    .setTag( periodicTag )
                    .setRequiredNetwork( Task.NETWORK_STATE_CONNECTED )
                    .setRequiresCharging( false )
                    .build();

            // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
            // are updated.
            GcmNetworkManager.getInstance( context ).schedule( task );
        return task;
    }

    public static String[] getStockListSelection()
    {
        return new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                QuoteColumns.PERCENT_CHANGE, QuoteColumns.STOCK_EXCHANGE,
                QuoteColumns.CHANGE, QuoteColumns.ISUP };
    }

    public static CursorLoader createStockListLoader( final Context context )
    {
        return new CursorLoader(context, QuoteProvider.Quotes.CONTENT_URI, getStockListSelection(),
                QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null);
    }

    public Drawable getDrawable(final Context context, final int drawableId )
    {
        if ( Build.VERSION.SDK_INT  >= 21 )
        {
            return context.getDrawable( drawableId );
        }
        return ContextCompat.getDrawable( context, drawableId );
    }

    public static <Type> ArrayList<Type> emptyArrayList() {
        return new ArrayList<>();
    }

    public static <Type> ArrayList<Type> singletonArrayList(final Type type) {
        return new ArrayList<>(Collections.singletonList(type));
    }
}
