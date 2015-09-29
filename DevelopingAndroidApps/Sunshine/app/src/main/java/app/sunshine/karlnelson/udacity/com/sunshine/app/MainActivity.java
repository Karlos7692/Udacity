package app.sunshine.karlnelson.udacity.com.sunshine.app;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    private static final String LOG_TAG = "MAIN ACTIVITY:";
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocation = Utility.getPreferredLocation(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), ForecastFragment.TAG)
                    .commit();
        }

        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ForecastFragment frag = (ForecastFragment) getSupportFragmentManager()
                .findFragmentByTag(ForecastFragment.TAG);

        frag.onLocationChanged();

        mLocation = Utility.getPreferredLocation(this);
    }

    private void viewLocation() {
        String location = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_location_key),
                        getString(R.string.pref_location_default_value));

        Uri mapURI = Uri.parse("geo:0:0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent viewLocation = new Intent(Intent.ACTION_VIEW, mapURI);

        //See if the intent can be resolved
        if ( viewLocation.resolveActivity(getPackageManager()) != null ) {
            startActivity(viewLocation);
        } else {
            Log.d(LOG_TAG, "Could not resolve " + location + "no maps app installed!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if ( id == R.id.action_view_location ) {
            viewLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
