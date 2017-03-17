package org.fossasia.openevent.utils;

/**
 * @author free4murad
 * Since 3/9/17
 */

public class CompressImage {

    private static String BASE_URL = "http://compressor-head.appspot.com/compressorhead?img=";
    private static String WIDTH = "&wid=300";

    public static String compressImage(String file) {
        String newURL = "";
        newURL = BASE_URL + file + WIDTH;
        return newURL;
    }
}
