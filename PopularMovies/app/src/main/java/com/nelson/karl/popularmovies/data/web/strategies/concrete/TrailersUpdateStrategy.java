package com.nelson.karl.popularmovies.data.web.strategies.concrete;

import android.content.Context;
import android.net.Uri;

import com.nelson.karl.popularmovies.data.model.Trailer;
import com.nelson.karl.popularmovies.data.model.orm.ObjectModelList;
import com.nelson.karl.popularmovies.data.parsers.JsonParser;
import com.nelson.karl.popularmovies.data.web.APIUtil;
import com.nelson.karl.popularmovies.data.web.strategies.DownloadStrategy;

/**
 * Created by Karl on 23/01/2016.
 */
public class TrailersUpdateStrategy extends DownloadStrategy<Long, ObjectModelList<Trailer>> {

    public TrailersUpdateStrategy(Context context, JsonParser<ObjectModelList<Trailer>> parser) {
        super(context, parser);
    }

    @Override
    public Uri getDownloadUri(Long... params) {
        return APIUtil.getTrailers(params[0]);
    }

    @Override
    public String getLogTag() {
        return "Trailers Download Strategy";
    }
}
