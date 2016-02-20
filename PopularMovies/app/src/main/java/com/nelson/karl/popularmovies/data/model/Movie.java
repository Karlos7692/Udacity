package com.nelson.karl.popularmovies.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.nelson.karl.popularmovies.data.model.orm.ORM;
import com.nelson.karl.popularmovies.data.model.orm.ObjectModel;
import com.nelson.karl.popularmovies.data.model.orm.ObjectModelList;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract;
import com.nelson.karl.popularmovies.data.model.orm.ObjectRetrieverStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Karl on 17/08/2015.
 */
public class Movie extends ObjectModel<Movie> implements Parcelable {

    public static final double INVALID_RATING = -1;
    public static final int INVALID_DURATION = -1;

    private long mId;
    private String mTitle;

    private String mPosterPath;

    private String mSynopsis;
    private double mUserRating;
    private Date mReleaseDate;

    private double mPopularity;
    private double mVoteAvg;

    // Downloaded on movie details event
    private int mDuration;
    private ObjectModelList<Trailer> mTrailers;
    private ObjectModelList<Review> mReviews;

    // Updated on marked as favourite
    private boolean mIsFavourite;


    public Movie() {
        //Empty not null stub. //TODO change.
        mTrailers = new ObjectModelList<>( null );
        mReviews = new ObjectModelList<>( null );
        mDuration = INVALID_DURATION;
    }

    public long getId() {
        return mId;
    }

    public void setId(  long mId  ) {
        this.mId = mId;
        mTrailers.setUri(MovieContract.TrailerEntry.buildTrailersUriFromMovie(mId));
        mReviews.setUri(MovieContract.ReviewEntry.buildReviewsByMovieUri(mId));
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

    //Trailer Information
    public List<Trailer> getTrailers() {
        return mTrailers;
    }

    public void setTrailers(ObjectModelList<Trailer> trailers) {
        mTrailers = trailers;
    }

    //Reviews Information
    public List<Review> getReviews() {
        return mReviews;
    }

    public void setReviews(ObjectModelList<Review> reviews) {
        mReviews = reviews;
    }

    //Movie Details
    public int getDuration() {
        return mDuration;
    }

    public void setDuration( int duration ) {
        mDuration = duration;
    }

    public boolean isFavourite() {
        return mIsFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        mIsFavourite = isFavourite;
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

        //Foreign Tables
        if ( mTrailers == null ) {
            mTrailers = new ObjectModelList<>( Uri.parse("") );
        }
        in.readTypedList(mTrailers, Trailer.CREATOR);

        if ( mReviews == null ) {
            mReviews = new ObjectModelList<>( Uri.parse("") );
        }
        in.readTypedList(mReviews, Review.CREATOR);

        //Movie details
        mDuration = in.readInt();

        //Additional table information
        mIsFavourite = in.readByte() != 0;
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
        dest.writeValue(mReleaseDate);

        //Foreign Information
        dest.writeTypedList(mTrailers);
        dest.writeTypedList(mReviews);

        //Movie details
        dest.writeInt(mDuration);

        dest.writeByte( (byte) (mIsFavourite ? 1 : 0 ) );
    }

    /**
     * Protocol to merge missing details from movie details query.
     */

    /**
     * DB related functions.
     */

    @Override
    public Uri getUri() {
        return MovieContract.MovieEntry.buildMovieUri(mId);
    }

    @Override
    public String[] getDBIdentifiers() {
        return new String[] { MovieContract.MovieEntry.ID };
    }

    @Override
    public String[] getDBIdentifierValues() {
        return new String[] { Long.toString(mId) };
    }

    @Override
    public void merge(Context context, Movie external) {
        mSynopsis = external.getSynopsis();
        mPopularity = external.getPopularity();
        mVoteAvg = external.getVoteAvg();
        mDuration = external.getDuration();
        mUserRating = external.getUserRating();
        mTrailers.merge(context, (ObjectModelList<Trailer>) external.getTrailers());
        mReviews.merge(context, (ObjectModelList<Review>) external.getReviews());

        this.update(context);
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.ID, mId);
        values.put(MovieContract.MovieEntry.TITLE, mTitle);
        values.put(MovieContract.MovieEntry.SYNOPSIS, mSynopsis);
        values.put(MovieContract.MovieEntry.POSTER_PATH, mPosterPath);
        values.put(MovieContract.MovieEntry.USER_RATING, mUserRating);
        values.put(MovieContract.MovieEntry.RELEASE_DATE, mReleaseDate.getTime());
        values.put(MovieContract.MovieEntry.POPULARITY, mPopularity);
        values.put(MovieContract.MovieEntry.VOTE_AVERAGE, mVoteAvg);
        values.put(MovieContract.MovieEntry.DURATION, mDuration);
        values.put(MovieContract.MovieEntry.IS_FAVOURITE, mIsFavourite ? 1 : 0);
        return values;
    }

    @Override
    public List<ORM> getHasRelations() {
        List<ORM> hasRelationships = new ArrayList<>();
        hasRelationships.add(mTrailers);
        hasRelationships.add(mReviews);
        return hasRelationships;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(double popularity) {
        mPopularity = popularity;
    }

    public double getVoteAvg() {
        return mVoteAvg;
    }

    public void setVoteAvg(double voteAvg) {
        mVoteAvg = voteAvg;
    }


    public static class Retriever implements ObjectRetrieverStrategy<Movie> {

        @Override
        public Movie apply(Cursor cursor) {

            if ( cursor == null || cursor.isAfterLast() || cursor.isClosed() ) {
                return null;
            }

            if ( cursor.isBeforeFirst() ) {
                cursor.moveToFirst();
            }

            final Movie movie = new Movie();
            final long id = cursor.getLong(MovieContract.MovieEntry.COL_ID);
            movie.setId(id);
            movie.setTitle(cursor.getString(MovieContract.MovieEntry.COL_TITLE));
            movie.setSynopsis(cursor.getString(MovieContract.MovieEntry.COL_SYNOPSIS));
            movie.setPosterPath(cursor.getString(MovieContract.MovieEntry.COL_POSTER_PATH));
            movie.setUserRating(cursor.getDouble(MovieContract.MovieEntry.COL_USER_RATING));
            movie.setReleaseDate(new Date(cursor.getLong(MovieContract.MovieEntry.COL_RELEASE_DATE)));
            movie.setPopularity(cursor.getDouble(MovieContract.MovieEntry.COL_POPULARITY));
            movie.setVoteAvg(cursor.getDouble(MovieContract.MovieEntry.COL_VOTE_AVG));
            movie.setDuration(cursor.getInt(MovieContract.MovieEntry.COL_DURATION));
            movie.setIsFavourite(cursor.getInt(MovieContract.MovieEntry.COL_IS_FAVOURITE) == 1);

            movie.setTrailers(getTrailers(cursor, id));

            cursor.moveToFirst();
            movie.setReviews(getReviews(cursor, id));

            cursor.close();
            return movie;
        }

        private ObjectModelList<Trailer> getTrailers(Cursor cursor, long movieId ) {

            Trailer.Retriever retriever = new Trailer.Retriever(MovieContract.MovieEntry.COL_TRAILER_OFFSET);
            Uri uri = MovieContract.TrailerEntry.buildTrailersUriFromMovie(movieId);
            return ObjectModelList.get(retriever, uri, cursor);
        }

        private ObjectModelList<Review> getReviews(Cursor cursor, long movieId) {
            Review.Retriever retriever = new Review.Retriever(MovieContract.MovieEntry.COL_REVIEW_OFFSET);
            Uri uri = MovieContract.ReviewEntry.buildReviewsByMovieUri(movieId);
            return ObjectModelList.get(retriever, uri, cursor);
        }
    }
}
