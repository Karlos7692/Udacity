package com.nelson.karl.popularmovies;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract;

public class MainDiscoveryActivity extends AppCompatActivity implements
        MovieDetailFragment.MovieChangedCallback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery);

        if ( findViewById(R.id.movie_detail_fragment_container) != null ) {
            mTwoPane = true;
            if ( savedInstanceState == null ) {
                MovieDetailFragment mdf = new MovieDetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_fragment_container, mdf, mdf.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        MainDiscoveryFragment mainDiscFrag = (MainDiscoveryFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_discovery_fragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_discovery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if ( mTwoPane ) {

            final FragmentManager manager = getSupportFragmentManager();
            final MovieDetailFragment currentMDF = (MovieDetailFragment) manager.
                    findFragmentByTag(MainDiscoveryFragment.TAG);

            if ( currentMDF != null && currentMDF.movieLoaded() ) {
                currentMDF.onMovieChanged(movie);
            } else {
                // Create a new fragment with a new loader.
                final Bundle args = new Bundle();
                args.putParcelable(MovieDetailFragment.MOVIE_DETAILS, movie.getUri());
                MovieDetailFragment newMDF = new MovieDetailFragment();
                newMDF.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_fragment_container, newMDF, newMDF.TAG)
                        .commit();
            }
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.setData(movie.getUri());
            startActivity(intent);
        }


    }
}
