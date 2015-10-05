package com.nelson.karl.popularmovies.data.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karl on 5/10/2015.
 */
public class TrailerJsonParser {

    private static final String RESULTS = "results";
    private static final String KEY = "key";

    public static List<String> parseTrailers( String resultStr ) throws JSONException {
        JSONObject resultObject = new JSONObject(resultStr);
        JSONArray results = resultObject.getJSONArray(RESULTS);


        List<String> trailerValues = new ArrayList<>();

        // API returns youtube uri video values with confusing title key.
        for ( int i=0; i<results.length(); i++) {
            JSONObject trailerObject = results.getJSONObject(i);
            trailerValues.add(trailerObject.getString(KEY));
        }

        return trailerValues;
    }

}
