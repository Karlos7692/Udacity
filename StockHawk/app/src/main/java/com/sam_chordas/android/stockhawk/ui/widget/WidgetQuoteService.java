package com.sam_chordas.android.stockhawk.ui.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.HistoricalChartActivity;

/**
 * Created by Karl on 15/09/2016.
 */

public class WidgetQuoteService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, Utils.getStockListSelection(),
                QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null);
        return new ListViewRemoteFactory(this, cursor);
    }

    private static class ListViewRemoteFactory implements RemoteViewsFactory
    {

        private Context mContext;
        private Cursor mCursor;
        public ListViewRemoteFactory( final Context context, final Cursor cursor )
        {
            mContext = context;
            mCursor = cursor;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if ( mCursor != null )
            {
                mCursor.close();
            }

            final long identityToken = Binder.clearCallingIdentity();
            mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    Utils.getStockListSelection(),
                    QuoteColumns.ISCURRENT + " = ?", new String[]{"1"}, null);
            Binder.restoreCallingIdentity( identityToken );
        }

        @Override
        public void onDestroy() {
            if ( mCursor != null )
            {
                mCursor.close();
                mCursor = null;
            }
        }

        @Override
        public int getCount() {
            return mCursor == null ? 0 : mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt( int position ) {
            if ( position == AdapterView.INVALID_POSITION || mCursor == null
                    || !mCursor.moveToPosition( position ) )
            {
                return null;
            }

            RemoteViews row = new RemoteViews( mContext.getPackageName(), R.layout.list_item_quote );
            final String symbol = getSymbol();
            row.setTextViewText( R.id.stock_symbol, symbol );
            row.setFloat( R.id.stock_symbol, "setTextSize", 16f);
            row.setTextViewText( R.id.bid_price, getBid() );
            row.setFloat( R.id.bid_price, "setTextSize", 16f);
            row.setTextViewText( R.id.change, getChange() );
            row.setFloat( R.id.change, "setTextSize", 16f);
            row.setInt( R.id.change, "setBackgroundResource", isUp()
                    ? R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red);
            final Intent launch = new Intent();
            launch.putExtra( HistoricalChartActivity.SYMBOL_ARG, symbol );
            row.setOnClickFillInIntent( R.id.list_item_quote, launch );
            return row;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews( mContext.getPackageName(), R.layout.list_item_quote );
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return mCursor.getLong( mCursor.getColumnIndex( QuoteColumns._ID ) );
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private String getSymbol()
        {
            return mCursor != null
                    ? mCursor.getString( mCursor.getColumnIndex( QuoteColumns.SYMBOL ) )
                    : "";
        }

        private String getBid()
        {
            return mCursor != null
                    ? mCursor.getString( mCursor.getColumnIndex( QuoteColumns.BIDPRICE ) )
                    : "";
        }

        private String getChange()
        {
            return mCursor != null
                    ? mCursor.getString( mCursor.getColumnIndex( QuoteColumns.CHANGE ) )
                    : "";
        }

        private boolean isUp()
        {
            return mCursor.getInt( mCursor.getColumnIndex( QuoteColumns.ISUP ) ) == 1;
        }
    }

}
