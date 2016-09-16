package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.google.android.gms.gcm.PeriodicTask;
import com.melnykov.fab.FloatingActionButton;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;

public class MyStocksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CONNECTIVITY_CHANGED_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Intent mServiceIntent;
    private ItemTouchHelper mItemTouchHelper;
    private static final int CURSOR_LOADER_ID = 0;
    private QuoteCursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mCursor;
    private boolean mIsConnected;
    private TextView mNotConnectedTextView;


    /** Network Connection fields **/
    private ConnectivityReceiver mConnectivityReceiver;
    private PeriodicTask mPeriodicUpdate = null;

    /** Invalid State Listeners **/
    private final OnInvalidSymbolListener mInvalidInputListener = new OnInvalidSymbolListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stocks);
        mContext = this;


        mIsConnected = Utils.isConnectingOrConnected( this );
        mNotConnectedTextView = (TextView) findViewById(R.id.not_connected_text_view);

        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        if ( savedInstanceState == null && mIsConnected ) {
            mServiceIntent = Utils.scheduleDataSync( this );
            if ( mPeriodicUpdate == null ) mPeriodicUpdate = Utils.setupPeriodicTask( this );
            showNoConnectionText( false );
        }
        else if ( savedInstanceState == null && isNoDataLoadedNoConnection() )
        {
            // Nothing to be shown and not connected.
            showNoConnectionText( true );
        }
        else if ( savedInstanceState == null && isDataLoadedNoConnection() )
        {
            showNoConnectionToast();
        }
        else if ( mIsConnected )
        {
            if ( mPeriodicUpdate == null ) mPeriodicUpdate = Utils.setupPeriodicTask( this );
            showNoConnectionText( false );
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        mCursorAdapter = new QuoteCursorAdapter(this, null);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final Intent historicalChartIntent = new Intent(mContext, HistoricalChartActivity.class);
                        final TextView symbolText = (TextView) view.findViewById(R.id.stock_symbol);
                        //final Quote quote = (Quote) symbolText.getTag();
                        historicalChartIntent.putExtra( HistoricalChartActivity.SYMBOL_ARG, symbolText.getText() );
                        //historicalChartIntent.putExtra( HistoricalChartActivity.EXCHANGE_ARG, quote.getStockExchange());

                        startActivity( historicalChartIntent );
                    }
                }));
        recyclerView.setAdapter(mCursorAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( Utils.isConnectingOrConnected( mContext ) ) {
                    new MaterialDialog.Builder(mContext).title(R.string.symbol_search)
                            .content(R.string.content_test)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // On FAB click, receive user input. Make sure the stock doesn't already exist
                                    // in the DB and proceed accordingly
                                    Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                                            new String[]{QuoteColumns.SYMBOL}, QuoteColumns.SYMBOL + "= ?",
                                            new String[]{input.toString()}, null);
                                    if (c.getCount() != 0) {
                                        Toast toast =
                                                Toast.makeText(MyStocksActivity.this, "This stock is already saved!",
                                                        Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                                        toast.show();
                                        return;
                                    } else {
                                        // Add the stock to DB
                                        mServiceIntent.putExtra("tag", "add");
                                        mServiceIntent.putExtra("symbol", input.toString());
                                        startService(mServiceIntent);
                                    }
                                }
                            })
                            .show();
                } else {
                    networkToast();
                }
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        mTitle = getTitle();
    }

    private void showNoConnectionToast( ) {
        Toast.makeText( this, getString( R.string.no_internet_connection_text ), Toast.LENGTH_LONG )
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register receiver
        mConnectivityReceiver = new ConnectivityReceiver();
        registerReceiver(mConnectivityReceiver, new IntentFilter(CONNECTIVITY_CHANGED_ACTION));

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener( mInvalidInputListener );
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener( mInvalidInputListener );
        unregisterReceiver(mConnectivityReceiver);
    }

    public void networkToast() {
        Toast.makeText(mContext, getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_stocks, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_change_units) {
            // this is for changing stock changes from percent value to dollar value
            Utils.showPercent = !Utils.showPercent;
            this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.STOCK_EXCHANGE,
                        QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        mCursor = data;
        if ( isDataLoadedNoConnection() )
        {
            showNoConnectionText( false );
            showNoConnectionToast();
        }
        else if ( isNoDataLoadedNoConnection() )
        {
            showNoConnectionText( true );
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private class OnInvalidSymbolListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(mContext.getString(R.string.invalid_stock_symbol_key))) {
                final String invalidTicker = sharedPreferences.getString(key, "TODO");
                Toast.makeText( mContext, "Invalid stock symbol " + invalidTicker, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isDataLoadedNoConnection()
    {
        return isDataLoaded() && !mIsConnected;
    }

    private boolean isNoDataLoadedNoConnection()
    {
        return !isDataLoaded() && !mIsConnected;
    }

    private boolean isDataLoaded()
    {
        return mCursor != null && mCursor.getCount() > 0;
    }

    private class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean nowConnected = Utils.isConnectingOrConnected( context );

            // Network is now connected from no connection
            if ( !mIsConnected && nowConnected ) {
                mIsConnected = true;
                mServiceIntent = Utils.scheduleDataSync( context );
                if ( mPeriodicUpdate == null ) mPeriodicUpdate = Utils.setupPeriodicTask( context );
                showNoConnectionText( false );
                return;
            }

            // Now do not have connection, but we have loaded some items.
            if ( mIsConnected && !nowConnected && isDataLoaded() )
            {
                showNoConnectionText( false );
                showNoConnectionToast();
                return;
            }

            showNoConnectionText( false );
        }
    }

    private void showNoConnectionText( final boolean notConnected )
    {
        if ( mNotConnectedTextView != null && notConnected )
        {
            mNotConnectedTextView.setVisibility( View.VISIBLE );
            return;
        }

        if ( mNotConnectedTextView != null )
        {
            mNotConnectedTextView.setVisibility( View.GONE );
        }
    }
}
