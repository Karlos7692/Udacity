package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.db.chart.model.LineSet;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.db.chart.model.Point;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.rest.model.soap.historicdata.HistoricalDataAPI;
import com.sam_chordas.android.stockhawk.rest.model.soap.historicdata.Quote;
import com.sam_chordas.android.stockhawk.rest.model.soap.historicdata.Result;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Karl on 26/08/2016.
 */

public class HistoricalChartActivity extends AppCompatActivity {

    public static final String SYMBOL_ARG = "symbol";
    private static final DateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US );
    public static final String EXCHANGE_ARG = "exchange";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        final Context currentContext = this;
        String symbol = null;
        String exchange = null;
        if ( savedInstanceState != null )
        {
            return;
        }

        final Intent startingIntent = getIntent();
        if ( startingIntent != null )
        {
            symbol = startingIntent.getStringExtra( SYMBOL_ARG );
            //exchange = startingIntent.getStringExtra( EXCHANGE_ARG );
        }
        assert  symbol != null;

        ActionBar actionBar = getSupportActionBar();
        if ( actionBar != null )
        {
            actionBar.setTitle( symbol );
        }


        final int white = Utils.getColor( this, android.R.color.white );

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://query.yahooapis.com/")
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        final Calendar calendar = new GregorianCalendar();
        final Date now = calendar.getTime();

        calendar.add(Calendar.YEAR, -1);
        final Date m1Yr = calendar.getTime();

        final HistoricalDataAPI historicalDataService = retrofit.create( HistoricalDataAPI.class );
        final String query = "select * from yahoo.finance.historicaldata where symbol"
                + "= \""+ symbol + "\" and startDate = \"" + sDateFormat.format(m1Yr)
                + "\" and endDate = \""+ sDateFormat.format(now) + "\"";

        final LineChartView lineChart = (LineChartView) findViewById( R.id.linechart );
        assert lineChart != null;

        final ProgressBar loadingSpinner = (ProgressBar) findViewById( R.id.loadingchart );
        assert loadingSpinner != null;

        loadingSpinner.setVisibility( View.VISIBLE );

        final Call<Result> getHistoricalData = historicalDataService.getHistoricalData( query );
        getHistoricalData.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if ( response.isSuccessful() )
                {
                    final LineSet lineset = new LineSet();

                    float min = Float.MAX_VALUE;
                    float max = Float.MIN_VALUE;
                    List<Quote> quotes = response.body().getQuery().getResults().getQuotes();
                    Collections.reverse( quotes );
                    for (int i = 0; i< quotes.size(); i++)
                    {
                        final Quote quote = quotes.get(i);
                        float price = (float) quote.getClose();
                        min = Math.min( min, price );
                        max = Math.max( max, price );
                        boolean addLabel = i==0 || i == quotes.size() - 1 || i == quotes.size()/2;
                        lineset.addPoint( new Point( addLabel ? quote.getDate() : "",  price ) );
                    }
                    lineset.setDotsColor( white );
                    lineset.setColor( white );
                    lineset.setSmooth( true );
                    lineset.setDotsRadius(1.0f);

                    min = Math.max( min - 1, 0.0f );
                    max = (max + 1);
                    float range = max - min;

                    int minTick = roundMinTick( min, range );
                    int maxTick = roundMaxTick( max, range );
                    int tick = getTick( range );

                    lineChart.setAxisBorderValues( minTick, maxTick, tick );
                    lineChart.addData( lineset );
                    lineChart.setLabelsColor( white );
                    lineChart.setAxisColor( white );
                    loadingSpinner.setVisibility(View.GONE);
                    lineChart.show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText( currentContext, "There has been a problem loading the chart, please try again another time.",
                        Toast.LENGTH_LONG ).show();
                loadingSpinner.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    private int roundMinTick( float min, float range )
    {

        return (int) ( min - ( min % getTick( range ) ) );
    }

    private int roundMaxTick( float max, float range )
    {
        final int tick = getTick( range );
        return (int) ( max + tick - (max % tick) );
    }

    private int getTick( final float range )
    {
        if ( range < 10f ) return 1;
        if ( range < 20f ) return 2;
        if ( range < 50f ) return 5;
        return 10;
    }
}
