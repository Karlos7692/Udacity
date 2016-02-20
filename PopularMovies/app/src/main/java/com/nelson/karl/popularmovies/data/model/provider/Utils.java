package com.nelson.karl.popularmovies.data.model.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Karl on 23/01/2016.
 */
public class Utils {

    public static String[] prepend( String prefix, String[] strings ) {
        String[] ret = new String[strings.length];
        for (int i=0; i<strings.length; i++) { ret[i] = prefix+strings[i]; }
        return ret;
    }

    public static <T> T[] append(T[] as1, T[] as2, T[] ret) {
        int index=0;
        for (int i = 0; i < as1.length; i++) { ret[index++] = as1[i]; }
        for (int i = 0; i < as2.length; i++) { ret[index++] = as2[i]; }
        return ret;
    }
}
