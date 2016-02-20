package com.nelson.karl.popularmovies.data.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.nelson.karl.popularmovies.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Karl on 4/10/2015.
 */
public class Utility {

    public static String getFriendlyDetailReleaseDate( Context context, Date date ) {
        SimpleDateFormat sdf =
                new SimpleDateFormat(context.getString(R.string.friendly_release_date_format));
        return sdf.format(date);
    }

    public static String getFriendlyTrailerName( Context context, int number ) {
        return String.format(
                context.getString(R.string.friendly_trailer_text_format),
                number
        );
    }

    public static String getFriendlyUserRating( Context context, double userRating ) {
        return String.format(
                context.getString(R.string.friendly_user_rating_format),
                userRating
        );
    }

    public static String getFriendlyDuration(Context context, int duration) {
        return String.format(
                context.getString(R.string.friendly_movie_duration),
                duration
        );
    }

    public static String getFriendlyAuthorText(Context context, String author) {
        return String.format(
                context.getString(R.string.friendly_author_text),
                author
        );
    }

    public static int getIsFavouriteColour( Context context, boolean isFavourite) {
        return isFavourite ?
                context.getResources().getColor(R.color.favourite_on) :
                context.getResources().getColor(android.R.color.darker_gray);
    }

    public static String[] prepend( String prefix, String[] strings ) {
        String[] ret = new String[strings.length];
        for (int i=0; i<strings.length; i++) { ret[i] = prefix+strings[i]; }
        return ret;
    }

    public static <T> T[] extend(T[] as1, T[] as2, T[] ret) {
        int index=0;
        for ( T anAs1 : as1 ) { ret[index++] = anAs1; }
        for ( T anAs2 : as2 ) { ret[index++] = anAs2; }
        return ret;
    }
}
