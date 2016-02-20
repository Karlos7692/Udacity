package com.nelson.karl.popularmovies.data.parsers;

import android.net.Uri;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.orm.ObjectModelList;
import com.nelson.karl.popularmovies.data.model.Review;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Karl on 5/10/2015.
 */
public class ReviewsJsonParser implements JsonParser<ObjectModelList<Review>> {
    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String CONTENT = "content";
    private static final String AUTHOR = "author";

    private final Movie mMovieToUpdate;
    public ReviewsJsonParser(Movie movie) {
        mMovieToUpdate = movie;
    }

    @Override
    public ObjectModelList<Review> parse(String result) throws JSONException {
        JSONObject object = new JSONObject(result);
        JSONArray jsonReviews = object.getJSONArray(RESULTS);

        ObjectModelList<Review> reviews = new ObjectModelList<>(
                MovieContract.ReviewEntry.buildReviewsByMovieUri(mMovieToUpdate.getId()));

        for ( int i=0; i<jsonReviews.length(); i++ ) {
            Review review = new Review();

            JSONObject jsonReview = jsonReviews.getJSONObject(i);
            review.setId( jsonReview.getString(ID) );
            review.setAuthor(jsonReview.getString(AUTHOR));
            review.setContent(jsonReview.getString(CONTENT));
            review.setMovieRef(mMovieToUpdate.getId());
            reviews.add(review);
        }

        mMovieToUpdate.setReviews(reviews);
        return reviews;
    }

}
