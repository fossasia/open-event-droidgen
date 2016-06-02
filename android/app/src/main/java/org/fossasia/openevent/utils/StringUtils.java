package org.fossasia.openevent.utils;

import android.text.TextUtils;

/**
 * User: mohit
 * Date: 25/1/16
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Return an empty string if the input is null
     *
     * @param string the string which can be null
     * @return an empty string if input is null
     */
    public static String optionalString(String string) {
        return TextUtils.isEmpty(string) ? "" : string;
    }
}
