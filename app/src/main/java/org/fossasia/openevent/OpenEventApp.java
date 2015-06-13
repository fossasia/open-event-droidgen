package org.fossasia.openevent;

import android.app.Application;

import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.modules.MapModuleFactory;

/**
 * User: MananWason
 * Date: 02-06-2015
 */
public class OpenEventApp extends Application {

    MapModuleFactory mapModuleFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        DbSingleton.init(this);
        mapModuleFactory = new MapModuleFactory();
    }

    public MapModuleFactory getMapModuleFactory() {
        return mapModuleFactory;
    }
}
