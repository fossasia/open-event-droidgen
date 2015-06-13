package org.fossasia.openevent.modules;

import android.support.v4.app.Fragment;

import org.fossasia.openevent.fragments.OSMapFragment;


/**
 * User: mohit
 * Date: 13/6/15
 */
public class OSMapModule implements MapModule {
    @Override
    public Fragment provideMapFragment() {
        return new OSMapFragment();
    }
}
