package org.fossasia.openevent.core.location.modules;

import android.support.v4.app.Fragment;

import org.fossasia.openevent.fragments.OSMapFragment;

public class OSMapModule implements MapModule {
    @Override
    public Fragment provideMapFragment() {
        return new OSMapFragment();
    }
}
