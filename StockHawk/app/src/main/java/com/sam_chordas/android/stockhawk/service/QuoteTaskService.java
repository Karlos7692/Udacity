package com.sam_chordas.android.stockhawk.service;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.rest.model.soap.quote.Quote;
import com.sam_chordas.android.stockhawk.rest.model.soap.quote.QuoteAPI;
import com.sam_chordas.android.stockhawk.rest.model.soap.quote.multi.Result;
import com.sam_chordas.android.stockhawk.rest.model.soap.quote.single.SingletonResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Karl on 4/09/2016.
 */

public class QuoteTaskService extends GcmTaskService {

    public static final String ACTION_UPDATE_BROADCAST = "com.sam_chordas.android.stockhawk.app.ACTION_DATA_UPDATED";

    private Context mContext;
    private String LOG_TAG = QuoteTaskService.class.getSimpleName();
    private static final String BASE_URL = "https://query.yahooapis.com/";
    // Tasks
    private static final String INITALIZE = "init";
    private static final String PERIODIC = "periodic";
    private static final String ADD_STOCK = "add";
    // Task Params
    private static final String SYMBOL_PARAM = "symbol";
    private static final String[] INITIAL_STOCKS = new String[] { "YHOO", "AAPL", "GOOG", "MSFT" };

    public QuoteTaskService()
    {
    }

    public QuoteTaskService(Context context) {
        mContext = context;
    }

    @Override
    public int onRunTask(TaskParams taskParams) {

        // Initialize context if have not done so already.
        if ( mContext == null ) mContext = this;
        final String task = taskParams.getTag();

        // Grab symbols from database.
        final String[] symbols = getSymbols( taskParams );

        // Sync data base.
        final Retrofit retrofit = new Retrofit.Builder().baseUrl( BASE_URL )
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final QuoteAPI quoteAPI = retrofit.create( QuoteAPI.class );

        try {
            final String query = buildQuery( symbols );
            Call<?> getQuoteInfo = task.equals( INITALIZE ) || task.equals( PERIODIC )
                    ? quoteAPI.getQuotes( query )
                    : quoteAPI.getQuote( query );

            Log.d( LOG_TAG, getQuoteInfo.request().url().toString() );

            Response<?> response = getQuoteInfo.execute();

            // Network operations have succeeded, update whether it is current information
            updateIsCurrent( task );

            ArrayList<ContentProviderOperation> syncOperations = getSyncOperations(response);
            mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY, syncOperations );

            final Intent broadcast = new Intent(ACTION_UPDATE_BROADCAST);
            mContext.sendBroadcast( broadcast );

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error, problems with the network ", e );
            e.printStackTrace();
            return GcmNetworkManager.RESULT_FAILURE;
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
            e.printStackTrace();
            return GcmNetworkManager.RESULT_FAILURE;
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private String buildQuery( final String... symbols ) throws UnsupportedEncodingException {
        String query = "select * from yahoo.finance.quotes where symbol in ( ";
        for ( int i=0; i<symbols.length; i++)
        {
            query += "\"" + symbols[i] + "\"";
            query += i < symbols.length - 1 ? ", " : "";
        }
        return query + ")";
    }

    private String[] getSymbols( final TaskParams params )
    {
        final String task = params.getTag();
        if ( task.equals( ADD_STOCK ) )
        {
            return new String[]{params.getExtras().getString(SYMBOL_PARAM)};
        }

        if ( !task.equals( INITALIZE ) && !task.equals( PERIODIC ) )
        {
            throw new UnsupportedOperationException( "Sorry: Task " + task + " is not supported" );
        }

        // Must be INITIALIZE or PERIODIC
        final Cursor initCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{"Distinct " + QuoteColumns.SYMBOL}, null, null, null );

        // No cache
        if ( initCursor == null || initCursor.getCount() == 0 )
        {
            return INITIAL_STOCKS;
        }

        // Symbols have been cached.
        final String[] symbols = new String[initCursor.getCount()];
        initCursor.moveToFirst();
        for ( int i=0; i<symbols.length; i++ )
        {
            symbols[i] = initCursor.getString(initCursor.getColumnIndex(QuoteColumns.SYMBOL));
            initCursor.moveToNext();
        }
        initCursor.close();
        return symbols;
    }

    @NonNull
    private ArrayList<ContentProviderOperation> getSyncOperations( final Response<?> response )
    {
        if ( !response.isSuccessful() )
        {
            Log.e(LOG_TAG, "Response was not successful!" );
            return Utils.emptyArrayList();
        }

        final Object body = response.body();
        if ( body instanceof SingletonResult ) return getSyncOperations( (SingletonResult) body );
        return getSyncOperations( (Result) body );
    }

    private ArrayList<ContentProviderOperation> getSyncOperations( final SingletonResult result )
    {
        final Quote quote = result.getQuery().getResults().getQuote();
        if ( !quote.isValid() )
        {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( mContext );
            SharedPreferences.Editor preferenceEditor = preferences.edit();
            preferenceEditor.putString( mContext.getString( R.string.invalid_stock_symbol_key ),
                    quote.getSymbol() );
            preferenceEditor.commit();
            return Utils.emptyArrayList();
        }
        return Utils.singletonArrayList( quote.toInsertOperation() );
    }

    ArrayList<ContentProviderOperation> getSyncOperations( final Result result )
    {
        final ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        for ( Quote quote : result.getQuery().getResults().getQuotes() ) {
            batchOperations.add( quote.toInsertOperation() );
        }
        return batchOperations;
    }

    private void updateIsCurrent( final String task )
    {
        final boolean updateCurrent = task.equals( INITALIZE ) || task.equals( PERIODIC );
        if ( updateCurrent )
        {
            // update ISCURRENT to 0 (false) so new data is current
            ContentValues contentValues = new ContentValues();
            contentValues.put(QuoteColumns.ISCURRENT, 0);
            mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                    null, null);
        }
    }
}
