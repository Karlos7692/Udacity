package com.nelson.karl.popularmovies.data.web.strategies.concrete;

import android.content.Context;
import android.net.Uri;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.orm.QueryModel;
import com.nelson.karl.popularmovies.data.parsers.JsonParser;
import com.nelson.karl.popularmovies.data.parsers.MovieDetailsParser;
import com.nelson.karl.popularmovies.data.parsers.ReviewsJsonParser;
import com.nelson.karl.popularmovies.data.parsers.TrailerJsonParser;
import com.nelson.karl.popularmovies.data.web.APIUtil;
import com.nelson.karl.popularmovies.data.web.strategies.DownloadStrategy;

import java.util.Collection;

/**
 * Created by Karl on 23/01/2016.
 */
public class DiscoverMoviesStrategy extends DownloadStrategy<Void, Collection<Movie>> {

    public DiscoverMoviesStrategy(Context context, JsonParser<Collection<Movie>> parser) {
        super(context, parser);
    }

    @Override
    public void updateDB(Collection<Movie> downloadedMovies) {
        final Context context = getContext();
        for ( Movie downloadedMovie : downloadedMovies ) {
            final QueryModel<Movie> queryModel = new QueryModel<>(new Movie.Retriever())
                    .find(context, downloadedMovie.getUri());
            if ( !queryModel.exists() ) {
                downloadedMovie.insert(context);
            }
            queryModel.close();
        }

    }

    @Override
    public Uri getDownloadUri(Void... nothing) {
        return APIUtil.discoverMovies(getContext());
    }

    @Override
    public String getLogTag() {
        return "Discover Movies Download Strategy";
    }
}
