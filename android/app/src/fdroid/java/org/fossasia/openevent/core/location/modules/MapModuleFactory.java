package org.fossasia.openevent.core.location.modules;

public class MapModuleFactory implements MapModuleProvider {

    /**
     * We can cache here to only initialize the module during startup
     */
    private static OSMapModule instance;

    @Override
    public MapModule provideMapModule() {
        if (instance == null) {
            instance = new OSMapModule();
        }
        return instance;
    }
}
