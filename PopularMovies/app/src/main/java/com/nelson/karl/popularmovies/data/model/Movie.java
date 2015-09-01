package com.nelson.karl.popularmovies.data.model;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Karl on 17/08/2015.
 */
public class Movie implements Parcelable {

    public static final double INVALID_RATING = -1;

    private long mId;
    private String mTitle;

    private String mPosterPath;

    private String mSynopsis;
    private double mUserRating;
    private Date mReleaseDate;

    public Movie() {
    }

    public long getId() {
        return mId;
    }

    public void setId(  long mId  ) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle( String mTitle ) {
        this.mTitle = mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath( String mPosterId ) {
        this.mPosterPath = mPosterId;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis( String mSynopsis ) {
        this.mSynopsis = mSynopsis;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public void setUserRating( double mUserRating ) {
        this.mUserRating = mUserRating;
    }

    @Nullable
    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate( Date mReleaseDate ) {
        this.mReleaseDate = mReleaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return mId == movie.mId;

    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie( Parcel in ) {
        mId = in.readLong();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mSynopsis = in.readString();
        mUserRating = in.readDouble();
        mReleaseDate = (Date) in.readValue(ClassLoader.getSystemClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mSynopsis);
        dest.writeDouble(mUserRating);
        dest.writeValue( mReleaseDate );
    }
}
