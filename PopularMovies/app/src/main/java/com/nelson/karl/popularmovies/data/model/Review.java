package com.nelson.karl.popularmovies.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.nelson.karl.popularmovies.data.model.orm.ORM;
import com.nelson.karl.popularmovies.data.model.orm.ObjectModel;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract;
import com.nelson.karl.popularmovies.data.model.orm.ObjectRetriever;

import java.util.List;

/**
 * Created by Karl on 5/10/2015.
 */
public class Review extends ObjectModel<Review> implements Parcelable {

    private String mId;
    private String mAuthor;
    private String mContent;
    private long mMovieRef;

    public Review() {

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    private Review( Parcel in ) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mMovieRef = in.readLong();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeLong(mMovieRef);
    }

    public long getMovieRef() {
        return mMovieRef;
    }

    public void setMovieRef(long movieRef) {
        mMovieRef = movieRef;
    }

    @Override
    public void merge(Context context, Review model) {

        setAuthor(model.getAuthor());
        setContent(model.getContent());

        // DB update code.
        this.update(context);
    }

    // DB Related functions.
    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.ReviewEntry._ID, mId);
        values.put(MovieContract.ReviewEntry.AUTHOR, mAuthor);
        values.put(MovieContract.ReviewEntry.CONTENT, mContent);
        values.put(MovieContract.ReviewEntry.MOVIE, mMovieRef);
        return values;
    }

    @Override
    public Uri getUri() {
        return MovieContract.ReviewEntry.buildReviewUri(mId);
    }

    @Override
    public String[] getDBIdentifiers() {
        return new String[] { MovieContract.ReviewEntry._ID };
    }

    @Override
    public String[] getDBIdentifierValues() {
        return new String[] { mId };
    }

    @Override
    public List<ORM> getHasRelations() {
        return null;
    }

    // Data Retrieval strategy.
    public static class Retriever extends ObjectRetriever<Review> {

        public Retriever() {

        }

        public Retriever(int offset) {
            super(offset);
        }

        @Override
        public Review apply(Cursor cursor) {
            if ( cursor == null || cursor.isClosed() || cursor.isAfterLast() ) {
                return null;
            }

            if ( cursor.isBeforeFirst() ) {
                cursor.moveToFirst();
            }

            if ( cursor.isNull(getOffset() + MovieContract.ReviewEntry.COL_MOVIE) ) {
                return null;
            }

            Review review = new Review();
            review.setId(cursor.getString(getOffset() + MovieContract.ReviewEntry.COL_ID));
            review.setAuthor(cursor.getString(getOffset() + MovieContract.ReviewEntry.COL_AUTHOR));
            review.setContent(cursor.getString(getOffset() + MovieContract.ReviewEntry.COL_CONTENT));
            review.setMovieRef(cursor.getLong(getOffset() + MovieContract.ReviewEntry.COL_MOVIE));

            return review;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (mMovieRef != review.mMovieRef) return false;
        return mId.equals(review.mId);

    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + (int) (mMovieRef ^ (mMovieRef >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Review{"+mId+","+mAuthor+","+mContent+","+mMovieRef+"}";
    }
}
