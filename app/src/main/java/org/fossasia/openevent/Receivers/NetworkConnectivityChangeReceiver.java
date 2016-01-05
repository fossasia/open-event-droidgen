package org.fossasia.openevent.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.otto.Bus;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.events.ConnectionCheckEvent;
import org.fossasia.openevent.events.DataDownloadEvent;
import org.fossasia.openevent.events.ShowNetworkDialogEvent;

/**
 * Created by shivenmian on 02/01/16.
 */
public class NetworkConnectivityChangeReceiver extends BroadcastReceiver {

    public boolean isConnected = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NetNotif", "Received network information");
        ConnectionCheckEvent event = new ConnectionCheckEvent(isConnected);
        if (isNetworkAvailable(context)) {
                if (!event.connState()) {
                    event.isConnected = true;
                }
        } else {
            event.isConnected=false;
        }
        OpenEventApp.postEventOnUIThread(event);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }
}
