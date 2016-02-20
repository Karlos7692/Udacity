package com.nelson.karl.popularmovies.data.model.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.nelson.karl.popularmovies.data.model.Movie;
import com.nelson.karl.popularmovies.data.model.ObjectModelList;
import com.nelson.karl.popularmovies.data.model.Review;
import com.nelson.karl.popularmovies.data.model.Trailer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Karl on 4/01/2016.
 */
public class TestProvider extends AndroidTestCase {
    
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    private List<Movie> mMovies;
    private ObjectModelList<Trailer> mTrailers;
    private ObjectModelList<Review> mReviews;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // If app has run before on emulator, database might exist.
        deleteAllRecords();
        setupData();
    }


    public void testInsertTrailers() {
        Trailer.Retriever retriever = new Trailer.Retriever();

        // Insert both trailers.
        for ( Trailer trailer : mTrailers ) {
            trailer.insert(mContext);
        }

        // Get all trailers.
        Uri trailerUri1 = MovieContract.TrailerEntry.buildTrailerUri(mTrailers.get(0).getId());


        Cursor cursor1 = mContext.getContentResolver().query(
                trailerUri1,
                MovieContract.TrailerEntry.ENTRY_COLUMNS,
                null,
                null,
                null);

        Uri trailerUri2 = MovieContract.TrailerEntry.buildTrailerUri(mTrailers.get(1).getId());
        Cursor cursor2 = mContext.getContentResolver().query(
                trailerUri2,
                MovieContract.TrailerEntry.ENTRY_COLUMNS,
                null,
                null,
                null);

        Uri trailerUri3 = MovieContract.TrailerEntry.buildTrailerUri(mTrailers.get(2).getId());
        Cursor cursor3 = mContext.getContentResolver().query(
                trailerUri3,
                MovieContract.TrailerEntry.ENTRY_COLUMNS,
                null,
                null,
                null);

        List<Trailer> results = new ArrayList<>();
        results.add(Trailer.get(retriever, cursor1));
        results.add(Trailer.get(retriever, cursor2));
        results.add(Trailer.get(retriever, cursor3));

        assertEquals(mTrailers, results);
        cursor1.close();
        cursor2.close();
        cursor3.close();

    }

    public void testInsertReviews() {
        final Review.Retriever retriever = new Review.Retriever();
        // Insert both reviews.
        for ( Review review : mReviews ) {
            review.insert(mContext);
        }

        // Get all trailers.
        Uri reviewUri1 = MovieContract.ReviewEntry.buildReviewUri(mReviews.get(0).getId());
        Uri reviewUri2 = MovieContract.ReviewEntry.buildReviewUri(mReviews.get(1).getId());

        Cursor cursor1 = mContext.getContentResolver().query(
                reviewUri1,
                MovieContract.ReviewEntry.ENTRY_COLUMNS,
                null,
                null,
                null);

        Cursor cursor2 = mContext.getContentResolver().query(
                reviewUri2,
                MovieContract.ReviewEntry.ENTRY_COLUMNS,
                null,
                null,
                null);

        List<Review> results = new ArrayList<>();
        results.add(Review.get(retriever, cursor1));
        results.add(Review.get(retriever, cursor2));
        cursor1.close();
        cursor2.close();
        assertEquals(mReviews, results);
    }

    public void testTrailersBulkInsert() {
        final Trailer.Retriever retriever = new Trailer.Retriever();
        final Uri queryUri = MovieContract.TrailerEntry.buildTrailersUriFromMovie(2);
        mTrailers.insert(mContext);
        Cursor cursor = mContext.getContentResolver().query(queryUri, null, null, null, null);
        ObjectModelList<Trailer> result = ObjectModelList.get(retriever, MovieContract.TrailerEntry
                .buildTrailersUriFromMovie(2), cursor);
        cursor.close();
        assertEquals(mTrailers, result);
    }

    public void testReviewsBulkInsert() {
        final Review.Retriever retriever = new Review.Retriever();
        final Uri queryUri = MovieContract.ReviewEntry.buildReviewsByMovieUri(2);
        mReviews.insert(mContext);
        Cursor cursor = mContext.getContentResolver().query(queryUri, null, null, null, null);
        ObjectModelList<Review> result = ObjectModelList.get(retriever,
                MovieContract.ReviewEntry.buildReviewsByMovieUri(2), cursor);
        assertEquals(mReviews, result);
    }

    public void testInsertMovies() {
        ContentResolver cr = getContext().getContentResolver();

        Movie.Retriever movieRetriever = new Movie.Retriever();

        for ( Movie movie : mMovies ) {
            movie.insert(mContext);
        }

        Uri uri1 = MovieContract.MovieEntry.buildMovieUri(mMovies.get(0).getId());
        Cursor cm1 = cr.query(uri1, null, null, null, null);
        Movie m1 = Movie.get(movieRetriever, cm1);
        cm1.close();

        Uri uri2 = MovieContract.MovieEntry.buildMovieUri(mMovies.get(1).getId());
        Cursor cm2 = cr.query(uri2, null, null, null, null);
        Movie m2 = Movie.get(movieRetriever, cm2);
        cm2.close();

        assertEquals(mMovies.get(0).getTrailers(), m1.getTrailers());
        assertEquals(mMovies.get(0).getReviews(), m1.getReviews());
        assertEquals(mMovies.get(0), m1);

        assertEquals(mMovies.get(1).getTrailers(), m2.getTrailers());
        assertEquals(mMovies.get(1).getReviews(), m2.getReviews());
        assertEquals(mMovies.get(1), m2);

    }

    public void testUpdateTrailers() {
        Trailer trailer = mTrailers.get(0);
        trailer.insert(mContext);
        trailer.setWatchUri(Uri.parse("content://something_that_has_changed"));
        trailer.update(mContext);

        Cursor c = mContext.getContentResolver().query(trailer.getInsertUri(), null, null, null, null);
        Trailer result = Trailer.get(new Trailer.Retriever(), c);
        c.close();

        //Assert that the identifiers are equal.
        assertEquals(trailer, result);

        //Assert that the change is equal.
        assertEquals(trailer.getWatchUri(), result.getWatchUri());

    }

    public void testUpdateReviews() {
        Review review = mReviews.get(0);
        review.insert(mContext);
        review.setAuthor("Mark Twain");
        review.update(mContext);

        Cursor c = mContext.getContentResolver().query(review.getInsertUri(), null, null, null, null);
        Review result = Review.get(new Review.Retriever(), c);
        c.close();

        assertEquals(review.getAuthor(), result.getAuthor());
        assertEquals(review, result);
    }

    public void testUpdateMovie() {
        Movie m2 = mMovies.get(1);
        m2.insert(mContext);

        boolean expectedFavourite = !m2.isFavourite();
        m2.setIsFavourite(expectedFavourite);

        //Update Trailers
        for (int i=0; i<mTrailers.size(); i++) {
            mTrailers.get(i).setWatchUri(Uri.parse(String.format("content://%d", i)));
        }

        for(int i=0; i<mReviews.size(); i++) {
            mReviews.get(i).setAuthor("George Orwell");
        }

        m2.update(getContext());
        Cursor c = mContext.getContentResolver().query(m2.getInsertUri(), null, null, null, null);
        Movie result = Movie.get(new Movie.Retriever(), c);
        c.close();

        //Assert all uris the same
        assertEquals(mTrailers.size(), result.getTrailers().size());
        for ( int i=0; i<mTrailers.size(); i++) {
            final Trailer expected = mTrailers.get(i);
            final Trailer resultTrailer = result.getTrailers().get(i);
            assertEquals(expected, resultTrailer);
            assertEquals(expected.getWatchUri(), resultTrailer.getWatchUri());
        }

        assertEquals(mReviews.size(), result.getReviews().size());
        for ( int i=0; i<mReviews.size(); i++) {
            final Review expected = mReviews.get(i);
            final Review resultReview = result.getReviews().get(i);
            assertEquals(expected, resultReview);
            assertEquals(expected.getAuthor(), resultReview.getAuthor());
        }

        //Check other movie fields that changed.
        assertEquals(expectedFavourite, result.isFavourite());
        assertEquals(m2, result);

    }

    public void testAddTrailerRefreshMovie() {
        for( Movie movie : mMovies ) {
            movie.insert(mContext);
        }

        Trailer t4 = new Trailer();
        t4.setId("t4");
        t4.setWatchUri(Uri.parse("content://t4_uri"));
        t4.setMovie(2);
        mTrailers.add(t4);

        Movie m2 = mMovies.get(1);
        assertEquals(4, m2.getTrailers().size());

        //Update m2
        m2.update(mContext);

        //Test movie db matches objects.
        ContentResolver cr = mContext.getContentResolver();
        Uri uri1 = MovieContract.MovieEntry.buildMovieUri(mMovies.get(0).getId());
        Cursor cm1 = cr.query(uri1, null, null, null, null);
        Movie movieResult1 = Movie.get(new Movie.Retriever(), cm1);
        cm1.close();

        Uri uri2 = MovieContract.MovieEntry.buildMovieUri(mMovies.get(1).getId());
        Cursor cm2 = cr.query(uri2, null, null, null, null);
        Movie movieResult2 = Movie.get(new Movie.Retriever(), cm2);
        cm2.close();

        assertEquals(mMovies.get(0).getTrailers(), movieResult1.getTrailers());
        assertEquals(mMovies.get(0).getReviews(), movieResult1.getReviews());
        assertEquals(mMovies.get(0), movieResult1);

        assertEquals(mMovies.get(1).getTrailers(), movieResult2.getTrailers());
        assertEquals(mMovies.get(1).getReviews(), movieResult2.getReviews());
        assertEquals(mMovies.get(1), movieResult2);

    }

    public void testAddReviewRefreshMovie() {

    }

    public void deleteTrailerRefreshMovie() {

    }

    public void deleteReviewRefreshMovie() {

    }

    public void testDeleteRecords() {

    }

    /**
     * Setup functions
     */

    private void setupData() {
        mTrailers = new ObjectModelList<>(MovieContract.TrailerEntry.buildTrailersUriFromMovie(2));
        Trailer t1 = new Trailer();
        t1.setId("t1");
        t1.setWatchUri(Uri.parse("file://content/t1"));
        t1.setMovie(2);

        Trailer t2 = new Trailer();
        t2.setId("t2");
        t2.setWatchUri(Uri.parse("file://content/t2"));
        t2.setMovie(2);

        Trailer t3 = new Trailer();
        t3.setId("t3");
        t3.setWatchUri(Uri.parse("file://content/t3"));
        t3.setMovie(2);

        mTrailers.add(t1);
        mTrailers.add(t2);
        mTrailers.add(t3);

        mReviews = new ObjectModelList<>(MovieContract.ReviewEntry.buildReviewsByMovieUri(2));
        Review r1 = new Review();
        r1.setId("r1");
        r1.setAuthor("Cats");
        r1.setContent("This movie was terrible!");
        r1.setMovieRef(2);

        Review r2 = new Review();
        r2.setId("r2");
        r2.setAuthor("Dogs");
        r2.setContent("This was the best movie I have ever seen! Squirrel!");
        r2.setMovieRef(2);
        mReviews.add(r1);
        mReviews.add(r2);

        mMovies = new ArrayList<>();
        Movie m1 = new Movie();
        m1.setId(1);
        m1.setTitle("Movie 1");
        m1.setPosterPath("path/to/posters1");
        m1.setIsFavourite(true);
        m1.setDuration(0);
        m1.setReleaseDate(new Date());
        m1.setTrailers(new ObjectModelList<Trailer>(MovieContract.TrailerEntry.buildTrailersUriFromMovie(1)));
        m1.setReviews(new ObjectModelList<Review>(MovieContract.ReviewEntry.buildReviewsByMovieUri(1)));
        m1.setPopularity(5.0);
        m1.setVoteAvg(4.0);


        Movie m2 = new Movie();
        m2.setId(2);
        m2.setTitle("Movie 2");
        m2.setIsFavourite(true);
        m2.setDuration(0);
        m2.setPosterPath("path/to/posters2");
        m2.setReleaseDate(new Date());
        m2.setTrailers(mTrailers);
        m2.setReviews(mReviews);
        m2.setPopularity(6.0);
        m2.setVoteAvg(7.0);

        mMovies.add(m1);
        mMovies.add(m2);
    }

    private void deleteAllRecords() {
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.TrailerEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);

        db.close();
    }

}