package org.fossasia.openevent.core.location.modules;

import android.support.v4.app.Fragment;

import org.fossasia.openevent.core.location.MapsFragment;

public class GoogleMapModule implements MapModule {
    /**
     * This guy should not really cache anything
     */
    @Override
    public Fragment provideMapFragment() {
        return new MapsFragment();
    }
}
