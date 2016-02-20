package com.nelson.karl.popularmovies.data.web.strategies.concrete;

import android.content.Context;
import android.net.Uri;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.orm.QueryModel;
import com.nelson.karl.popularmovies.data.parsers.JsonParser;
import com.nelson.karl.popularmovies.data.parsers.ReviewsJsonParser;
import com.nelson.karl.popularmovies.data.parsers.TrailerJsonParser;
import com.nelson.karl.popularmovies.data.web.APIUtil;
import com.nelson.karl.popularmovies.data.web.strategies.DownloadStrategy;

/**
 * Created by Karl on 23/01/2016.
 */
public class MovieDetailsUpdateStrategy extends DownloadStrategy<Movie, Movie> {

    public MovieDetailsUpdateStrategy(Context context, JsonParser<Movie> parser) {
        super(context, parser);
    }

    /**
     * Movie Params exist in the first argument.
     */
    @Override
    public Uri getDownloadUri(Movie... params) {
        return APIUtil.getMovieDetails(params[0].getId());
    }

    @Override
    public void doAdditionalStrategies(Movie movie) {
        TrailersUpdateStrategy trailersUpdateStrategy = new TrailersUpdateStrategy(getContext(),
                new TrailerJsonParser(movie));
        trailersUpdateStrategy.apply(movie.getId());
        ReviewsUpdateStrategy reviewsUpdateStrategy = new ReviewsUpdateStrategy(getContext(),
                new ReviewsJsonParser(movie));
        reviewsUpdateStrategy.apply(movie.getId());

    }

    @Override
    public void updateDB(Movie downloadedMovie) {
        final Context context = getContext();

        final QueryModel<Movie> queryModel = new QueryModel<>(new Movie.Retriever())
                .find(context, downloadedMovie.getUri());

        if ( queryModel.exists() ) {
            queryModel.get().merge(context, downloadedMovie);
        } else {
            downloadedMovie.insert(context);
        }
        queryModel.close();
    }

    @Override
    public String getLogTag() {
        return "Movie Details Download Strategy";
    }
}
