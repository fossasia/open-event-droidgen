package org.fossasia.openevent.utils;

/**
 * Created by MananWason on 10-06-2015.
 */
public class Progress {
    public static int progress = 0;

    public int increaseProgress() {
        progress++;
        return progress;
    }
}
