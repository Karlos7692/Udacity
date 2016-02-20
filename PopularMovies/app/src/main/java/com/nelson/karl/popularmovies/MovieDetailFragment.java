package com.nelson.karl.popularmovies;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.Review;
import com.nelson.karl.popularmovies.data.model.Trailer;
import com.nelson.karl.popularmovies.data.model.provider.MovieContract;
import com.nelson.karl.popularmovies.data.web.APIUtil;
import com.nelson.karl.popularmovies.data.utils.Utility;
import com.nelson.karl.popularmovies.data.web.tasks.DownloadTask;
import com.squareup.picasso.Picasso;


public class MovieDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String MOVIE_DETAILS = "movie details";

    public static final String TAG = "MOVIE DETAIL FRAGMENT";

    private static final int MOVIE_DETAIL_LOADER_ID = 1200;

    private final String LOG_TAG = this.getClass().getSimpleName();

    //Model
    private Movie mMovie;
    private Uri mMovieUri;

    //View
    private View mRootView;

    public interface MovieChangedCallback {
        public void onMovieSelected(Movie movie);
    }

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void setArguments(Bundle args) {
        mMovieUri = args.getParcelable(MOVIE_DETAILS);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Not the first time this fragment has been loaded, restart loader.
        if ( savedInstanceState != null ) {
            mMovieUri = savedInstanceState.getParcelable(MOVIE_DETAILS);
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER_ID, null, this);
            return;
        }

        // 1 pane mode uri retrieval.
        final Intent intent = getActivity().getIntent();
        if ( intent != null && intent.getData() != null ) {
            mMovieUri = intent.getData();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        // Check if loader thread returns first.
        if ( mMovie != null ) {
            updateUI();
        }

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if ( mMovie != null ) {
            outState.putParcelable(MOVIE_DETAILS, mMovieUri);
        }
    }

    /**
     *  Loader Callbacks
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // When it is first initiated, we do not know what the we are fetching is at this point.
        if ( mMovieUri == null ) { return null; }

        // Note: Default observer is registered on construction.
        return new CursorLoader(getActivity(), mMovieUri, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovie = Movie.get(new Movie.Retriever(), data);
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Do nothing
    }

    /**
     * Two pane functions.
     */

    public boolean movieLoaded() {
        return mMovieUri != null;
    }
    /**
     * Called on Two pane mode, iff fragment exists
     * @param movie
     */
    public void onMovieChanged(Movie movie) {
        long movieId = movie.getId();
        mMovieUri = MovieContract.MovieEntry.buildMovieUri(movieId);
        getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER_ID, null, this);
    }


    /**
     * User Interface Functions
     */

    private void updateUI() {
        updateMovieUI();
        updateTrailersUI();
        updateReviewsUI();
    }

    private void updateMovieUI() {
        if ( mMovie != null && mRootView != null ) {
            TextView banner = (TextView) mRootView.findViewById(R.id.movie_detail_banner);
            banner.setText(mMovie.getTitle());

            ImageView poster = (ImageView) mRootView.findViewById(R.id.movie_detail_poster);
            Uri imageUri = APIUtil.getImage(mMovie.getPosterPath());
            //Picasso caches previously loaded images.
            Picasso.with(getActivity()).load(imageUri.toString()).into(poster);

            TextView releaseTextView = (TextView) mRootView.findViewById(R.id.movie_detail_release_date);
            releaseTextView.setText(
                    Utility.getFriendlyDetailReleaseDate(getActivity(), mMovie.getReleaseDate()) );

            TextView durationTextView = (TextView) mRootView.findViewById(R.id.movie_detail_duration);
            if ( mMovie.getDuration() != Movie.INVALID_DURATION ) {
                durationTextView.setText(Utility.getFriendlyDuration(getActivity(),
                        mMovie.getDuration()));
            }

            TextView userRatingView = (TextView) mRootView.findViewById(R.id.movie_detail_user_rating);
            if ( mMovie.getUserRating() != Movie.INVALID_RATING ) {
                userRatingView.setText(
                        Utility.getFriendlyUserRating(getActivity(), mMovie.getUserRating())
                );
            }
            final Button markFavouriteButton = (Button) mRootView
                    .findViewById(R.id.movie_detail_favourite_button);
            markFavouriteButton.setTextColor(
                    Utility.getIsFavouriteColour(getActivity(), mMovie.isFavourite())
            );
            markFavouriteButton.setVisibility(View.VISIBLE);
            markFavouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMovie.setIsFavourite(!mMovie.isFavourite());
                    markFavouriteButton.setTextColor(
                            Utility.getIsFavouriteColour(getActivity(), mMovie.isFavourite())
                    );
                    mMovie.update(getActivity());
                }
            });

            TextView synopsisTextView = (TextView) mRootView.findViewById(R.id.movie_detail_synopsis);
            synopsisTextView.setText(mMovie.getSynopsis());

            //Set all titles to visible
            View divider = mRootView.findViewById(R.id.movie_detail_divider);
            divider.setVisibility(View.VISIBLE);

            TextView overviewTitle = (TextView) mRootView.findViewById(R.id.movie_detail_overview_title);
            overviewTitle.setVisibility(View.VISIBLE);

            TextView trailersTitle = (TextView) mRootView.findViewById(R.id.movie_detail_trailers_title);
            trailersTitle.setVisibility(View.VISIBLE);

            TextView reviewsTitle = (TextView) mRootView.findViewById(R.id.movie_detail_reviews_title);
            reviewsTitle.setVisibility(View.VISIBLE);

        }
    }

    private void updateTrailersUI() {
        LinearLayout ll = (LinearLayout) getActivity()
                .findViewById(R.id.movie_detail_trailers_container);

        if ( ll != null  && mMovie != null && !mMovie.getTrailers().isEmpty() ) {
            ll.removeAllViews();
            int count = 1;
            for (final Trailer trailer : mMovie.getTrailers()) {
                View root = View.inflate(getActivity(), R.layout.trailer_view, null);
                TextView trailerView = (TextView) root.findViewById(R.id.trailer_name);
                trailerView.setText(Utility.getFriendlyTrailerName(getActivity(), count));
                trailerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent watch = new Intent(Intent.ACTION_VIEW, trailer.getWatchUri());
                        startActivity(watch);
                    }
                });
                ll.addView(root);
                count++;
            }

        }
    }

    private void updateReviewsUI() {
        LinearLayout ll = (LinearLayout) getActivity()
                .findViewById(R.id.movie_detail_reviews_container);

        if ( ll != null  && mMovie != null && !mMovie.getReviews().isEmpty() ) {
            ll.removeAllViews();
            for (final Review review : mMovie.getReviews()) {
                View root = View.inflate(getActivity(), R.layout.review_view, null);
                TextView authorTextView = (TextView) root.findViewById(R.id.author_textview);

                authorTextView.setText(
                        Utility.getFriendlyAuthorText(getActivity(), review.getAuthor())
                );

                TextView contentTextView = (TextView) root.findViewById(R.id.content_textview);
                contentTextView.setText(review.getContent());

                ll.addView(root);
            }
        }
    }
}
