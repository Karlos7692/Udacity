package com.sam_chordas.android.stockhawk.rest.model.soap.quote;

import com.sam_chordas.android.stockhawk.rest.model.soap.quote.multi.Result;
import com.sam_chordas.android.stockhawk.rest.model.soap.quote.single.SingletonResult;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by Karl on 4/09/2016.
 */

public interface QuoteAPI {

    @GET("/v1/public/yql?&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    public Call<Result> getQuotes(@retrofit2.http.Query("q") String query );

    @GET("/v1/public/yql?&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    public Call<SingletonResult> getQuote(@retrofit2.http.Query("q") String query );


}
