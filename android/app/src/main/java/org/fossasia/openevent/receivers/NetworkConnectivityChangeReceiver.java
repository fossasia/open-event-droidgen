package org.fossasia.openevent.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.events.ConnectionCheckEvent;

import timber.log.Timber;

/**
 * User: shivenmian
 * Date: 02/01/16
 * <p/>
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
        OpenEventApp.postEventOnUIThread(event);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
