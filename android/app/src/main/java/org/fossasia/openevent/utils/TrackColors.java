package org.fossasia.openevent.utils;

import android.util.SparseIntArray;

public class TrackColors {
    private static SparseIntArray colorMap = new SparseIntArray();

    public static int getColor(int trackId) {
        return colorMap.get(trackId, -1);
    }

    public static void storeColor(int trackId, int color) {
        colorMap.put(trackId, color);
    }
}
