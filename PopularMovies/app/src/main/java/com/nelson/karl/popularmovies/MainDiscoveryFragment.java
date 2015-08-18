package com.nelson.karl.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.utils.MovieDownloadTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainDiscoveryFragment extends Fragment {

    public static final String TAG = "Main Discovery Fragment";
    private static final String LOG_TAG = TAG;

    private MovieAdapter mDataAdapter;

    public MainDiscoveryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate root element.
        View root = inflater.inflate(R.layout.fragment_main_discovery, container, false);

        //Bind data with grid view.
        mDataAdapter = new MovieAdapter(
                getActivity(),
                R.layout.view_movie_thumb,
                new ArrayList<Movie>()
        );

        GridView gridView = (GridView) root.findViewById(R.id.discovery_fragment_move_grid);
        gridView.setAdapter(mDataAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition( position );
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.MOVIE_DETAILS, movie);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        MovieDownloadTask downloadTask = new MovieDownloadTask( getActivity(), mDataAdapter );
        downloadTask.execute();
        Log.d(LOG_TAG, "Executed!!!");
    }
}
