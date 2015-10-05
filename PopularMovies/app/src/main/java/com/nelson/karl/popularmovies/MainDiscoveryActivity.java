package com.nelson.karl.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nelson.karl.popularmovies.data.model.Movie;

public class MainDiscoveryActivity extends AppCompatActivity implements
        MovieDetailFragment.MovieChangedCallback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery);

        if ( findViewById(R.id.movie_detail_fragment_container) != null ) {
            mTwoPane = true;

            MovieDetailFragment mdf = new MovieDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_fragment_container, mdf, mdf.TAG)
                    .commit();
        } else {
            mTwoPane = false;
        }

        MainDiscoveryFragment mainDiscFrag = (MainDiscoveryFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_discovery_fragment);
        mainDiscFrag.setIsTwoPane(mTwoPane);

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
        MovieDetailFragment mdf = (MovieDetailFragment) getSupportFragmentManager()
                .findFragmentByTag(MovieDetailFragment.TAG);

        mdf.onMovieChanged(movie);
    }
}
