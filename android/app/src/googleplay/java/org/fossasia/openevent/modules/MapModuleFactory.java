package org.fossasia.openevent.modules;

/**
 * User: mohit
 * Date: 13/6/15
 */
public class MapModuleFactory implements MapModuleProvider {

    /**
     * We can cache here to only initialize the module during startup
     */
    private static GoogleMapModule instance;

    @Override
    public MapModule provideMapModule() {
        if (instance == null) {
            instance = new GoogleMapModule();
        }
        return instance;
    }
}
