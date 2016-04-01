package org.fossasia.openevent.utils;

import timber.log.Timber;

/**
 * Created by MananWason on 8/5/2015.
 */
public class DownloadCounter {
    private int counter = 0;

    public void incrementValue() {
        counter++;
        Timber.tag("Counter increment val").d(counter + "");

    }

    public int getValue() {
        return counter;
    }
}
