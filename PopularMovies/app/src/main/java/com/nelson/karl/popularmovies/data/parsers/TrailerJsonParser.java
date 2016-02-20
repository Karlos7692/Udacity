package com.nelson.karl.popularmovies.data.parsers;

import android.net.Uri;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.orm.ObjectModelList;
import com.nelson.karl.popularmovies.data.model.Trailer;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract;
import com.nelson.karl.popularmovies.data.web.APIUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Karl on 5/10/2015.
 */
public class TrailerJsonParser implements JsonParser<ObjectModelList<Trailer>> {

    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String KEY = "key";

    private final Movie mMovieToUpdate;
    public TrailerJsonParser(Movie movie) {
        mMovieToUpdate = movie;
    }
    @Override
    public ObjectModelList<Trailer> parse(String resultStr) throws JSONException {
        JSONObject resultObject = new JSONObject(resultStr);
        JSONArray results = resultObject.getJSONArray(RESULTS);

        ObjectModelList<Trailer> trailers = new ObjectModelList<>(
                MovieContract.TrailerEntry.buildTrailersUriFromMovie(mMovieToUpdate.getId()));

        // API returns youtube uri video values with confusing title key.
        for ( int i=0; i<results.length(); i++) {
            JSONObject trailerObject = results.getJSONObject(i);

            Trailer trailer = new Trailer();
            trailer.setId(trailerObject.getString(ID));
            Uri uri = APIUtil.getTrailer(trailerObject.getString(KEY));
            trailer.setWatchUri(uri);
            trailer.setMovie(mMovieToUpdate.getId());
            trailers.add(trailer);
        }

        mMovieToUpdate.setTrailers(trailers);
        return trailers;
    }
}
