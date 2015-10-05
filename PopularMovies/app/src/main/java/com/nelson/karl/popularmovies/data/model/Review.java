package com.nelson.karl.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Karl on 5/10/2015.
 */
public class Review implements Parcelable {

    public static class TABLE {
        public static final String _ID = "id";
        public static final String AUTHOR = "author";
        public static final String CONTENT = "content";
    }

    private long mId;
    private String mAuthor;
    private String mContent;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
