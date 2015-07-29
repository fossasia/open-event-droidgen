package org.fossasia.openevent;

import android.app.Application;

import com.squareup.otto.Bus;

import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.modules.MapModuleFactory;

/**
 * User: MananWason
 * Date: 02-06-2015
 */
public class OpenEventApp extends Application {

    private static Bus sEventBus;
    MapModuleFactory mapModuleFactory;

    public static Bus getEventBus() {
        if (sEventBus == null) {
            sEventBus = new com.squareup.otto.Bus();
        }
        return sEventBus;
    }

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
