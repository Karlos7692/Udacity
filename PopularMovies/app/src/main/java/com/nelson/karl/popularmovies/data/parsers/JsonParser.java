package com.nelson.karl.popularmovies.data.parsers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Karl on 5/10/2015.
 */
public interface JsonParser<R> {
    public R parse( String result ) throws JSONException;
}
