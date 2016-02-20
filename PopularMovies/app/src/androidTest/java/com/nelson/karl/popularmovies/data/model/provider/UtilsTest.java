package com.nelson.karl.popularmovies.data.model.provider;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by Karl on 23/01/2016.
 */
public class UtilsTest extends TestCase {

    public void testArrayCombine() {
        String[] as1 = new String[] {"a", "b", "c" };
        String[] as2 = new String[] {"d", "e", "f" };
        String[] ret = new String[as1.length+as2.length];
        ret = Utils.append(as1, as2, ret);

        assertEquals(Arrays.asList(new String[]{"a", "b", "c","d", "e", "f"}), Arrays.asList(ret));
    }
}