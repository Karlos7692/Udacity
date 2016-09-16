package com.sam_chordas.android.stockhawk.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.PeriodicTask;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.QuoteTaskService;
import com.sam_chordas.android.stockhawk.ui.HistoricalChartActivity;

/**
 * Created by Karl on 14/09/2016.
 */
public class WidgetQuoteProvider extends AppWidgetProvider {

    private boolean mIsConnected;
    private PeriodicTask mPeriodicUpdate;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        mIsConnected = Utils.isConnectingOrConnected( context );
        if ( mIsConnected )
        {
            Utils.scheduleDataSync( context );
            mPeriodicUpdate = Utils.setupPeriodicTask( context );
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive( context, intent );
        if ( intent.getAction().equals( QuoteTaskService.ACTION_UPDATE_BROADCAST ) )
        {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            onUpdate( context, manager, manager.getAppWidgetIds(
                    new ComponentName( context, WidgetQuoteProvider.class ) ) );
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for ( int id : appWidgetIds )
        {
            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_stocks );

            widget.setRemoteAdapter(R.id.list_view, new Intent( context, WidgetQuoteService.class ) );

            final Intent clickable = new Intent( context, HistoricalChartActivity.class );
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, 0, clickable, PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate( R.id.list_view, pendingIntent );

            widget.setEmptyView( R.id.list_view, R.id.widget_empty_view );

            appWidgetManager.updateAppWidget( id, widget );
        }
    }

}
