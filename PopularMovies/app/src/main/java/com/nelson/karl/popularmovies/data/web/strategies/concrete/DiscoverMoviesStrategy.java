package com.nelson.karl.popularmovies.data.web.strategies.concrete;

import android.content.Context;
import android.net.Uri;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.parsers.JsonParser;
import com.nelson.karl.popularmovies.data.parsers.MovieDetailsParser;
import com.nelson.karl.popularmovies.data.parsers.ReviewsJsonParser;
import com.nelson.karl.popularmovies.data.parsers.TrailerJsonParser;
import com.nelson.karl.popularmovies.data.web.APIUtil;
import com.nelson.karl.popularmovies.data.web.strategies.DownloadStrategy;

import java.util.List;

/**
 * Created by Karl on 23/01/2016.
 */
public class DiscoverMoviesStrategy extends DownloadStrategy<Void, List<Movie>> {

    public DiscoverMoviesStrategy(Context context, JsonParser<List<Movie>> parser) {
        super(context, parser);
    }

    // TODO Simplify the relationships between the parsers and strategies.
    @Override
    public void doAdditionalStrategies( List<Movie> movies ) {
        for ( Movie movie : movies ) {
            MovieDetailsStrategy detailsStrategy = new MovieDetailsStrategy(getContext(), new MovieDetailsParser(movie));
            detailsStrategy.apply(movie.getId());

            TrailersStrategy trailersStrategy = new TrailersStrategy(getContext(), new TrailerJsonParser(movie));
            trailersStrategy.apply(movie.getId());

            ReviewsStrategy reviewsStrategy = new ReviewsStrategy(getContext(), new ReviewsJsonParser(movie));
            reviewsStrategy.apply(movie.getId());
        }
    }

    @Override
    public void insertResultIntoDB( List<Movie> movies ) {
//        for ( Movie movie : movies ) {
//            movie.insert(getContext());
//        }
    }

    @Override
    public Uri getDownloadUri(Void... nothing) {
        return APIUtil.discoverMovies(getContext());
    }
}
