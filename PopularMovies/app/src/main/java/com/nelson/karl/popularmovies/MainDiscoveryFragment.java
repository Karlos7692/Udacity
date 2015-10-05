package com.nelson.karl.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.utils.MovieDownloadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainDiscoveryFragment extends Fragment {

    public static final String TAG = "Main Discovery Fragment";
    private static final String MOVIE_DATA = "Movie Data";

    private static final String LOG_TAG = TAG;
    private static final CharSequence NO_NETWORK_CONNECTION_MESSAGE = "You don't have an active network"
            + " connection. Please connect to the internet.";

    private MovieAdapter mDataAdapter;
    private boolean mTwoPane;

    public MainDiscoveryFragment() {
    }

    public void setIsTwoPane( boolean largeDevice ) {
        mTwoPane = largeDevice;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( savedInstanceState == null ) {
            mDataAdapter = new MovieAdapter(
                    getActivity(),
                    R.layout.view_movie_thumb,
                    new ArrayList<Movie>()
            );
            updateMovies();
        } else {
            restoreMovieDate( savedInstanceState );
        }
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
                if ( mTwoPane ) {
                    MainDiscoveryActivity mdf = (MainDiscoveryActivity) getActivity();
                    mdf.onMovieSelected(movie);
                } else {
                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.MOVIE_DETAILS, movie);
                    startActivity(intent);
                }
            }
        });

        return root;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if ( savedInstanceState==null ) {
            updateMovies();
        } else {
            restoreMovieDate(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( !isNetworkAvailable() ) {
            Log.d(LOG_TAG, "Should be here");
            Toast.makeText(getActivity(), NO_NETWORK_CONNECTION_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    private void updateMovies() {
        if ( isNetworkAvailable() ) {
            MovieDownloadTask downloadTask = new MovieDownloadTask(getActivity(), mDataAdapter);
            downloadTask.execute();
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

    private void restoreMovieDate( Bundle inBundle ) {
        List<Movie> savedMovies =
                Arrays.asList( ( Movie[]) inBundle.getParcelableArray(MOVIE_DATA));
        mDataAdapter = new MovieAdapter(
                getActivity(),
                R.layout.view_movie_thumb,
                savedMovies
        );
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
