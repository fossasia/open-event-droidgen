package org.fossasia.openevent.config;

import android.content.Context;

/**
 * Generic configuration strategy to setup application
 */
public interface ConfigStrategy {

    /**
     * Configures application and returns false if configuration should continue, and true if it
     * should halt
     * @param context Context of the app
     * @return Should configuration be halted
     */
    boolean configure(Context context);

}
