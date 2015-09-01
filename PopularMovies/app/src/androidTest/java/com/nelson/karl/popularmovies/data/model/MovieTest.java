package com.nelson.karl.popularmovies.data.model;

import android.os.Bundle;
import android.os.Parcel;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by Karl on 19/08/2015.
 */
public class MovieTest extends TestCase {

    public static final String TEST1 = "Conventional Test";
    public static final String TEST2 = "Case w/ Release Date";

    public void testWriteToParcel() throws Exception {
        Bundle testBundle = new Bundle();

        //Conventional movie test
        Movie testMovie1 = new Movie();
        testMovie1.setId(0);
        testMovie1.setTitle("Some Title");
        testMovie1.setPosterPath("/some_path");
        testMovie1.setSynopsis("Lorem Ipsum");
        testMovie1.setUserRating(5.5);
        testMovie1.setReleaseDate(new Date());

        testBundle.putParcelable(TEST1, testMovie1);

        //Movie without release date.
        Movie testMovie2 = new Movie();
        testMovie1.setId(1);
        testMovie1.setTitle("Some Movie The Sequal");
        testMovie1.setPosterPath("/some_path_2");
        testMovie1.setSynopsis("Lorem Ipsum 2");
        testMovie1.setUserRating(4.5);
        testMovie1.setReleaseDate(null);

        testBundle.putParcelable(TEST2, testMovie2);

        Movie restored1 = testBundle.getParcelable(TEST1);
        assertEquals(restored1, testMovie1);
        assertEquals(restored1.getReleaseDate(), testMovie1.getReleaseDate());
        assertEquals(restored1.getSynopsis(), testMovie1.getSynopsis());
        assertEquals(restored1.getUserRating(), restored1.getUserRating());

        Movie restored2 = testBundle.getParcelable(TEST2);
        assertEquals(restored2, testMovie2);
        assertEquals(restored2.getReleaseDate(), testMovie2.getReleaseDate());
        assertEquals(restored2.getSynopsis(), testMovie2.getSynopsis());
        assertEquals(restored2.getUserRating(), restored2.getUserRating());

    }
}