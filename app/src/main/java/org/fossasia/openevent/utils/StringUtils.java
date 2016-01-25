package org.fossasia.openevent.utils;

import android.text.TextUtils;

/**
 * User: mohit
 * Date: 25/1/16
 */
public final class StringUtils {
    private StringUtils() {
    }

    public static String optionalString(String string) {
        return TextUtils.isEmpty(string) ? "" : string;
    }
}
