package sunshine.karlnelson.app;

/**
 * Created by Karl on 21/04/15.
 */

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;

import sunshine.karlnelson.app.data.WeatherContract;
import sunshine.karlnelson.app.sync.SunshineSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private static final String LOG_TAG = "FORECAST FRAGMENT:";

    public static final String TAG = "FORECAST FRAGMENT";
    public static final String IS_TODAY_VIEW_LARGE = "TVL";

    private static final int FORECAST_LOADER_ID = 0;
    private static final String SELECTION_KEY = "selection position";

    private ForecastAdapter mAdapter;
    private ListView mListView;
    private int mPosition = 0;
    private boolean mLargeTodayView;

    //Listeners
    private OnSharedPreferencesChangedListener mPreferencesChangedListener;

    public ForecastFragment() {
    }

    public void useLargeTodayView( boolean isTwoPane ) {
        mLargeTodayView = isTwoPane;
        if ( mAdapter != null ) {
            mAdapter.setUseTodayView(mLargeTodayView);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new ForecastAdapter(getActivity(), null, 0);

        mAdapter.setUseTodayView(mLargeTodayView);

        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        View noWeatherInformation = rootView.findViewById(R.id.no_weather_information);
        mListView.setEmptyView(noWeatherInformation);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    DetailFragment.Callback callback = (DetailFragment.Callback) getActivity();
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    callback.onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                            locationSetting, cursor.getLong(COL_WEATHER_DATE)));

                }
                mPosition = i;
            }
        });

        if ( savedInstanceState != null && savedInstanceState.containsKey(SELECTION_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTION_KEY);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if ( mPosition != ListView.INVALID_POSITION ) {
            outState.putInt(SELECTION_KEY, mPosition);
            outState.putBoolean(IS_TODAY_VIEW_LARGE, mLargeTodayView);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.registerOnSharedPreferenceChangeListener(
                mPreferencesChangedListener = new OnSharedPreferencesChangedListener());
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.unregisterOnSharedPreferenceChangeListener(mPreferencesChangedListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//
//        if ( id == R.id.action_refresh ) {
//            updateWeather();
//            return true;
//        }

        if ( id == R.id.action_view_location ) {
            viewLocation();
            return true;
        }
            
        return false;
    }

    public void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
    }

    private void viewLocation() {
       if ( mAdapter != null ) {
           Cursor cursor = mAdapter.getCursor();
           if ( cursor != null ) {
               cursor.moveToFirst();
               String lat = cursor.getString(COL_COORD_LAT);
               String lng = cursor.getString(COL_COORD_LONG);
               Uri geoLocation = Uri.parse("geo: "+ lat +"," + lng);

               Intent viewLocation = new Intent(Intent.ACTION_VIEW, geoLocation);

               //See if the intent can be resolved
               if ( viewLocation.resolveActivity(getActivity().getPackageManager()) != null ) {
                   startActivity(viewLocation);
               } else {
                   Log.d(LOG_TAG, "Could not resolve " + geoLocation  + "no maps app installed!");
               }
           }
       }
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    private void updateEmptyView() {
        if (mAdapter.getCount() == 0 ) {
            TextView tv = (TextView) getActivity().findViewById(R.id.no_weather_information);
            if ( tv != null ) {

                if ( !Utility.isNetworkAvailable(getActivity()) ) {
                    tv.setText(R.string.empty_forecast_list_no_network_connection);
                    return;
                }
                //There is a connection.
                tv.setText(Utility.getLocationStatusMessage(getActivity()));
            }
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        String sortOrder =  WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri uri = WeatherContract.WeatherEntry
                .buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());

        return new CursorLoader( getActivity(),
                uri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if ( mPosition != ListView.INVALID_POSITION ) {
            mListView.smoothScrollToPosition(mPosition);
        }
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public class OnSharedPreferencesChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_location_status_key))) {
                updateEmptyView();
            }
        }
    }
}
