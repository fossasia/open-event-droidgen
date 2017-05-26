package org.fossasia.openevent.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by free4murad on 3/9/17.
 */

public class CompressImage {
    public static String compressImage(String file) {
        URL url;
        String newURL = "";
        int indexOfPath = -1;
        try {
            url = new URL(file);
        } catch (MalformedURLException e) {
            return file;
        }
        indexOfPath = file.indexOf(url.getPath());
        if(!url.getHost().endsWith(".rsz.io")) {
            newURL = file.substring(0, indexOfPath) + ".rsz.io" + file.substring(indexOfPath) + "?width=300";
        }
        if(url.getProtocol().equals("https")){
            newURL = newURL.replace("https", "http");
        }
        return newURL;
    }
}
