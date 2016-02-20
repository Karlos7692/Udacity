package com.nelson.karl.popularmovies.data.web.tasks.concrete;

import android.content.Context;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.parsers.MoviesJsonParser;
import com.nelson.karl.popularmovies.data.web.strategies.DownloadStrategy;
import com.nelson.karl.popularmovies.data.web.strategies.concrete.DiscoverMoviesStrategy;
import com.nelson.karl.popularmovies.data.web.tasks.DownloadTask;

import java.util.List;

/**
 * Created by Karl on 23/01/2016.
 */
public class DiscoverMoviesDownloadTask extends DownloadTask<String, List<Movie>> {

    public DiscoverMoviesDownloadTask(Context context) {
        super(context);
    }

    @Override
    protected DownloadStrategy getStrategy() {
        return new DiscoverMoviesStrategy(getContext(), new MoviesJsonParser());
    }
}
