package org.fossasia.openevent.utils;

import timber.log.Timber;

/**
 * Created by MananWason on 8/5/2015.
 */
public class DownloadCounter {
    public int counter = 0;

    public void incrementValue() {
        counter++;
        Timber.tag("Counter increment val").d(counter + "");

    }
}
