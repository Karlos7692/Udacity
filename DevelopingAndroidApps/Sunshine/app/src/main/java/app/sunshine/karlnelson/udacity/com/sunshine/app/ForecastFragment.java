package app.sunshine.karlnelson.udacity.com.sunshine.app;

/**
 * Created by Karl on 21/04/15.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = "FORECAST FRAGMENT:";
    private ArrayAdapter<String> mAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new ArrayAdapter<>(
                getActivity(),
                //Layout  of the list
                R.layout.list_item_forcast,
                //How the view is rendered
                R.id.list_item_forecast_textview);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String detail = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(DetailActivity.DETAIL_FIELD, detail);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if ( id == R.id.action_refresh ) {
            updateWeather();
            return true;
        }
            
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    public void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String location = preferences.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default_value));
        String units = preferences.getString(getString(R.string.pref_temperature_units_key),
                getString(R.string.pref_temperature_units_metric));

        FetchWeatherTask fetcherTask = new FetchWeatherTask(getActivity(), mAdapter);
        fetcherTask.execute(location, units);
    }

}
