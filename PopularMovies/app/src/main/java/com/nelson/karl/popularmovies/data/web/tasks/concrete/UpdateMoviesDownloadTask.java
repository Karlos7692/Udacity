package com.nelson.karl.popularmovies.data.web.tasks.concrete;

import android.content.Context;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.parsers.MovieDetailsParser;
import com.nelson.karl.popularmovies.data.web.strategies.concrete.MovieDetailsUpdateStrategy;
import com.nelson.karl.popularmovies.data.web.tasks.DownloadTask;

/**
 * Created by Karl on 4/02/2016.
 */
public class UpdateMoviesDownloadTask extends DownloadTask<Movie, Void> {

    public UpdateMoviesDownloadTask(Context context) {
        super(context);
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        for ( Movie movie : movies ) {
            MovieDetailsUpdateStrategy strategy = new MovieDetailsUpdateStrategy(getContext(),
                    new MovieDetailsParser(movie));
            strategy.apply(movie);
            strategy.updateDB(movie);
        }
        return null;
    }
}
