package com.sam_chordas.android.stockhawk.rest.model.soap.historicdata;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Karl on 3/09/2016.
 */

public interface HistoricalDataAPI {

    @GET("v1/public/yql?&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    public Call<Result> getHistoricalData(@retrofit2.http.Query("q") final String query );

}
