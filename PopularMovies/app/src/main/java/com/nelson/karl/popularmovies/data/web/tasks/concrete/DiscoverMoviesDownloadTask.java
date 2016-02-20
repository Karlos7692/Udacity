package com.nelson.karl.popularmovies.data.web.tasks.concrete;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.parsers.MoviesJsonParser;
import com.nelson.karl.popularmovies.data.web.strategies.DownloadStrategy;
import com.nelson.karl.popularmovies.data.web.strategies.concrete.DiscoverMoviesStrategy;
import com.nelson.karl.popularmovies.data.web.tasks.DiscoverDownloadTask;
import java.util.Collection;

/**
 * Created by Karl on 23/01/2016.
 * Downloads discover movies.
 */
public class DiscoverMoviesDownloadTask extends DiscoverDownloadTask<Void, Movie> {

    public DiscoverMoviesDownloadTask(Context context, ArrayAdapter<Movie> movieAdapter) {
        super(context, movieAdapter);
    }

    @Override
    protected Collection<Movie> doInBackground(Void... params) {
        DownloadStrategy<Void, Collection<Movie>> strategy = new DiscoverMoviesStrategy(
                getContext(), new MoviesJsonParser());
        Collection<Movie> results =  strategy.apply(params);
        if ( results != null ) {
            strategy.updateDB(results);
        }
        return results;
    }

    @Override
    public void launchSubTasks(Collection<Movie> result) {
        if (result == null ) { return; }
        UpdateMoviesDownloadTask updateMoviesDownloadTask = new UpdateMoviesDownloadTask(getContext());
        updateMoviesDownloadTask.execute(result.toArray(new Movie[result.size()]));
    }

}
