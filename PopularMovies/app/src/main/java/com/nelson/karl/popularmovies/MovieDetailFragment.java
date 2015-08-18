package com.nelson.karl.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.mock.MockDialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.utils.APIUtil;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;


public class MovieDetailFragment extends Fragment {

    public static final String MOVIE_DETAILS = "movie details";

    public static final String USER_RATING_FORMAT = "%.1f/10 rating.";

    private static final String DATE_FORMAT_STR = "dd/mm/yyyy";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR);

    private Movie mMovie;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( savedInstanceState == null ) {
            mMovie = getActivity().getIntent().getParcelableExtra(MOVIE_DETAILS);
        } else {
            mMovie = savedInstanceState.getParcelable(MOVIE_DETAILS);
        }

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if ( savedInstanceState != null ) {
            mMovie = savedInstanceState.getParcelable(MOVIE_DETAILS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        TextView banner = (TextView) root.findViewById(R.id.movie_detail_banner);
        banner.setText(mMovie.getTitle());

        ImageView poster = (ImageView) root.findViewById(R.id.movie_detail_poster);
        Uri imageUri = APIUtil.getImage( mMovie.getPosterPath() );
        //Picasso caches previously loaded images.
        Picasso.with(getActivity()).load(imageUri.toString()).into(poster);

        TextView releaseTextView = (TextView) root.findViewById(R.id.movie_detail_release_date);
        releaseTextView.setText(formatReleaseDate());

        TextView userRatingView = (TextView) root.findViewById(R.id.movie_detail_user_rating);
        userRatingView.setText( formatUserRating() );

        TextView synopsisTextView = (TextView) root.findViewById(R.id.movie_detail_synopsis);
        synopsisTextView.setText( mMovie.getSynopsis() );
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_DETAILS, mMovie);
    }

    private String formatReleaseDate() {
        return DATE_FORMAT.format(mMovie.getReleaseDate());
    }

    private String formatUserRating() {
        return String.format( USER_RATING_FORMAT, mMovie.getUserRating());
    }

}
