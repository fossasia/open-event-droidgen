package org.fossasia.openevent.config.strategies;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import org.fossasia.openevent.config.ConfigStrategy;

/**
 * Configures and provides EventBus singleton for use throughout app
 *
 * To be used via {@link org.fossasia.openevent.config.StrategyRegistry}
 *
 * TODO: Remove event bus from project
 */
public class EventBusStrategy implements ConfigStrategy {

    private Bus eventBus;
    private Handler handler;

    public EventBusStrategy() {
        handler = new Handler(Looper.getMainLooper());
    }

    public Bus getEventBus() {
        if (eventBus == null) {
            eventBus = new Bus();
        }
        return eventBus;
    }

    @Override
    public boolean configure(Context context) {
        getEventBus().register(context);
        return false;
    }

    public void postEventOnUIThread(final Object event) {
        handler.post(() -> getEventBus().post(event));
    }

}
