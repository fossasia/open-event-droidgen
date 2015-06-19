package org.fossasia.openevent.utils;

import android.database.DatabaseUtils;

/**
 * Created by MananWason on 20-06-2015.
 */
public class SqlEscapeString {
    public String sqlString(String string) {
        return DatabaseUtils.sqlEscapeString(string);
    }
}
