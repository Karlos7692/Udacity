package com.nelson.karl.popularmovies.data.web.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.util.Collection;

/**
 * Created by Karl on 23/01/2016.
 */
public abstract class DiscoverDownloadTask<Params, Result> extends AsyncTask<Params, Void, Collection<Result>> {

    private Context mContext;
    private ArrayAdapter<Result> mAdapter;
    public DiscoverDownloadTask(Context context, ArrayAdapter<Result> movieAdapter) {
        mContext = context;
        mAdapter = movieAdapter;
    }

    @Override
    protected void onPostExecute(Collection<Result> results) {
        if ( mAdapter != null && results != null ) {
            mAdapter.clear();
            for ( Result r : results ) {
                mAdapter.add(r);
            }
            mAdapter.notifyDataSetChanged();
            launchSubTasks(results);
        }
    }

    public abstract void launchSubTasks(Collection<Result> result);

    public Context getContext() {
        return mContext;
    }
}
