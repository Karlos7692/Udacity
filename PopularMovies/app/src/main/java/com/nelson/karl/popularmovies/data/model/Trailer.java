package com.nelson.karl.popularmovies.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.nelson.karl.popularmovies.data.model.orm.ORM;
import com.nelson.karl.popularmovies.data.model.orm.ObjectModel;
import com.nelson.karl.popularmovies.data.model.orm.ObjectRetrieverStrategy;
import com.nelson.karl.popularmovies.data.model.orm.QueryModel;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract;
import com.nelson.karl.popularmovies.data.model.orm.ObjectRetriever;

import java.util.List;

/**
 * Created by Karl on 5/10/2015.
 */
public class Trailer extends ObjectModel<Trailer> implements Parcelable {

    private String mId;
    private Uri mWatchUri;
    private long mMovieRef;

    public Trailer() {

    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public Uri getWatchUri() {
        return mWatchUri;
    }

    public void setWatchUri(Uri mWatchUri) {
        this.mWatchUri = mWatchUri;
    }

    public void setWatchUri(String uri) {
        this.mWatchUri = Uri.parse(uri);
    }
    public long getMovie() {
        return mMovieRef;
    }

    public void setMovie(long movieRef) {
        mMovieRef = movieRef;
    }

    public static final Parcelable.Creator<Trailer> CREATOR
            = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    private Trailer( Parcel in ) {
        mId = in.readString();
        mWatchUri = in.readParcelable(ClassLoader.getSystemClassLoader());
        mMovieRef = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeParcelable(mWatchUri, flags);
        dest.writeLong(mMovieRef);
    }


    @Override
    public void merge(Context context, Trailer model) {
        this.mWatchUri = model.getWatchUri();

        // Update the DB.
        this.update(context);
    }

    // DB related functions.
    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.TrailerEntry._ID, mId);
        values.put(MovieContract.TrailerEntry.URI, mWatchUri.toString());
        values.put(MovieContract.TrailerEntry.MOVIE, mMovieRef);
        return values;
    }

    @Override
    public Uri getUri() {
        return MovieContract.TrailerEntry.buildTrailerUri(mId);
    }

    @Override
    public String[] getDBIdentifiers() {
        return new String[] { MovieContract.TrailerEntry._ID };
    }

    @Override
    public String[] getDBIdentifierValues() {
        return new String[] { mId };
    }

    @Override
    public List<ORM> getHasRelations() {
        return null;
    }

    // From Cursor back to trailer strategies.
    public static class Retriever extends ObjectRetriever<Trailer> {

        public Retriever() {
        }

        public Retriever(int offset) {
            super(offset);
        }

        @Override
        public Trailer apply(Cursor cursor) {

            if ( cursor == null || cursor.isClosed() || cursor.isAfterLast() ) {
                return null;
            }

            if ( cursor.isBeforeFirst() ) {
                cursor.moveToFirst();
            }

            if ( cursor.isNull(getOffset() + MovieContract.TrailerEntry.COL_MOVIE) ) {
                return null;
            }

            Trailer trailer = new Trailer();
            trailer.setId(cursor.getString(getOffset() + MovieContract.TrailerEntry.COL_ID));
            trailer.setWatchUri(Uri.parse(cursor.getString(getOffset() + MovieContract.TrailerEntry.COL_URI)));
            trailer.setMovie(cursor.getLong(getOffset() + MovieContract.TrailerEntry.COL_MOVIE));

            return trailer;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trailer trailer = (Trailer) o;

        return mId.equals(trailer.mId);

    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Trailer{%s %s %d}", mId, mWatchUri.toString(), mMovieRef);
    }
}
