package org.fossasia.openevent;

import android.app.Application;

import org.fossasia.openevent.dbutils.DbSingleton;

/**
 * Created by MananWason on 02-06-2015.
 */
public class OpenEventApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DbSingleton.init(this);
    }
}
