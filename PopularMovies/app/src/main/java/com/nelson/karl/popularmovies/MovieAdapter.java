package com.nelson.karl.popularmovies;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.utils.APIUtil;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Karl on 16/08/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = "Movie Adapter";

    private int mResource;
    public MovieAdapter(Context context, int resource, List<Movie> movies ) {
        super( context, resource, movies );
        mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView movieThumbView;
        if ( convertView == null ) {
            movieThumbView = (ImageView) LayoutInflater.from(getContext())
                    .inflate(mResource, parent, false);
        } else {
            movieThumbView = (ImageView) convertView;
        }

        Movie movie = getItem(position);
        Uri imageUri = APIUtil.getImage( movie.getPosterPath() );
        Picasso.with(parent.getContext()).load(imageUri.toString()).into(movieThumbView);

        return movieThumbView;
    }


}
