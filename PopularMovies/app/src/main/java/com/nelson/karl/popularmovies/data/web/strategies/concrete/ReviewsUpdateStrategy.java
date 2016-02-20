package com.nelson.karl.popularmovies.data.web.strategies.concrete;

import android.content.Context;
import android.net.Uri;

import com.nelson.karl.popularmovies.data.model.Review;
import com.nelson.karl.popularmovies.data.model.orm.ObjectModelList;
import com.nelson.karl.popularmovies.data.parsers.JsonParser;
import com.nelson.karl.popularmovies.data.web.APIUtil;
import com.nelson.karl.popularmovies.data.web.strategies.DownloadStrategy;

/**
 * Created by Karl on 23/01/2016.
 */
public class ReviewsUpdateStrategy extends DownloadStrategy<Long, ObjectModelList<Review>> {


    public ReviewsUpdateStrategy(Context context, JsonParser<ObjectModelList<Review>> parser) {
        super(context, parser);
    }

    /**
     * Movie argument exits in the first argument.
     * @param params
     * @return uri to download the reviews for a particular movie.
     */
    @Override
    public Uri getDownloadUri(Long... params) {
        return APIUtil.getMovieReviews(params[0]);
    }

    @Override
    public String getLogTag() {
        return "Reviews Download Strategy";
    }
}
