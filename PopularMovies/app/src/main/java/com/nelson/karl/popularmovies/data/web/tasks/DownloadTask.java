package com.nelson.karl.popularmovies.data.web.tasks;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Karl on 4/02/2016.
 */
public abstract class DownloadTask<Params, Result> extends AsyncTask<Params, Void, Result> {

    private Context mContext;

    public DownloadTask( Context context ) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
}
