package app.sunshine.karlnelson.udacity.com.sunshine.app;

/**
 * Created by Karl on 21/04/15.
 */

import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

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

        String[] forecastsArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 75/46",
                "Mon - Cloudy - 70/46",
                "Tue - Asteroids - 72/63",
                "Wed - Heavy Rain - 64/51",
                "Thu - HELP TRAPPED IN WEATHER STATION - 70/46",
                "Fri - Sunny - 80/68",
        };

        List<String> weeksForecast = new ArrayList<>(Arrays.asList(forecastsArray));

        mAdapter = new ArrayAdapter<String>(
                getActivity(),
                //Layout  of the list
                R.layout.list_item_forcast,
                //How the view is rendered
                R.id.list_item_forecast_textview,
                //The data
                weeksForecast);

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

            final String location = PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getString(getString(R.string.pref_location_key),
                            getString(R.string.pref_location_default_value));
            
            item.setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    WeatherFetcherTask fetcherTask = new WeatherFetcherTask();


                    fetcherTask.execute(location);

                    Toast.makeText(getActivity(), "Refresh the weather =) ", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        return false;
    }

    public class WeatherFetcherTask extends AsyncTask<String,Void,String[]> {

        private static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
        private static final String QUERY_PARAM = "q";
        private static final String FORMAT_PARAM = "mode";
        public static final String UNITS_PARAM = "units";
        public static final String DAYS_PARAM = "cnt";
        private String LOG_TAG = WeatherFetcherTask.class.getSimpleName();



        @Override
        protected String[] doInBackground(String... params) {

            String postcode = params[0];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {


                Uri weatherResource= Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, postcode)
                        .appendQueryParameter(FORMAT_PARAM, "json")
                        .appendQueryParameter(UNITS_PARAM, "metric")
                        .appendQueryParameter(DAYS_PARAM, "7")
                        .build();

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(weatherResource.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");

                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                try {
                    return WeatherDataParser.getWeatherDataFromJson(forecastJsonStr, 7);
                } catch ( JSONException e )
                {
                    Log.e(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.e("ForecastFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if ( strings != null ) {
                mAdapter.clear();
                mAdapter.addAll(strings);
            }
        }
    }

}
