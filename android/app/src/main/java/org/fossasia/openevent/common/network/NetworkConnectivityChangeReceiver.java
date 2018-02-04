package org.fossasia.openevent.common.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.fossasia.openevent.common.events.ConnectionCheckEvent;
import org.fossasia.openevent.config.StrategyRegistry;

import timber.log.Timber;

/**
 * Network connectivity change receiver to detect changes in network
 */
public class NetworkConnectivityChangeReceiver extends BroadcastReceiver {

    public boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.i("Network information received %s", intent);
        ConnectionCheckEvent event = new ConnectionCheckEvent(isConnected);
        if (isNetworkAvailable(context)) {
            if (!event.connState()) {
                event.isConnected = true;
            }
        } else {
            event.isConnected = false;
        }
        Timber.i("Network connected %s", event.isConnected);
        StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(event);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return (networkInfo != null && networkInfo.isConnected());
    }
}
