package com.nelson.karl.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract;
import com.nelson.karl.popularmovies.data.web.tasks.concrete.DiscoverMoviesDownloadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainDiscoveryFragment extends Fragment {

    private static final int MAIN_DISCOVERY_LOADER_ID = 0;

    public static final String TAG = "Main Discovery Fragment";
    private static final String MOVIE_DATA = "Movie Data";

    private static final String LOG_TAG = TAG;
    private static final CharSequence NO_NETWORK_CONNECTION_MESSAGE =
            "You don't have an active network connection. Please connect to the internet.";

    private MovieAdapter mDataAdapter;

    public MainDiscoveryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( savedInstanceState != null ) {
            restoreMovieData(savedInstanceState);
            return;
        }

        // No instance state saved, Get Movies
        mDataAdapter = new MovieAdapter( getActivity(), R.layout.view_movie_thumb,
                    new ArrayList<Movie>() );
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate root element.
        View root = inflater.inflate(R.layout.fragment_main_discovery, container, false);
        GridView gridView = (GridView) root.findViewById(R.id.discovery_fragment_move_grid);

        //Bind data to view
        gridView.setAdapter(mDataAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                MainDiscoveryActivity mdf = (MainDiscoveryActivity) getActivity();
                mdf.onMovieSelected(movie);
            }
        });

        return root;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if ( savedInstanceState != null ) {
            restoreMovieData(savedInstanceState);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if ( !isNetworkAvailable() ) {
            Toast.makeText(getActivity(), NO_NETWORK_CONNECTION_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    private void updateMovies() {
        if ( isNetworkAvailable() ) {
            DiscoverMoviesDownloadTask movieDownloadTask = new DiscoverMoviesDownloadTask(getActivity(), mDataAdapter);
            movieDownloadTask.execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Movie[] movies = new Movie[mDataAdapter.getCount()];
        for ( int i=0; i<movies.length; i++ ) {
            movies[i] = mDataAdapter.getItem(i);
        }
        outState.putParcelableArray(MOVIE_DATA, movies);

    }

    private void restoreMovieData(Bundle inBundle) {
        Parcelable[] movieData = inBundle.getParcelableArray(MOVIE_DATA);
        if ( movieData != null ) {
            List<Movie> savedMovies = new ArrayList<>();
            for ( Parcelable data : movieData ) {
                savedMovies.add((Movie) data );
            }
            mDataAdapter = new MovieAdapter(getActivity(), R.layout.view_movie_thumb, savedMovies );
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
