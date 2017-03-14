package org.fossasia.openevent.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.events.DataDownloadEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by championswimmer on 21/6/16.
 */
public class NetworkUtils extends BroadcastReceiver {

    protected List<NetworkUtils.NetworkStateReceiverListener> listeners;
    protected Boolean connected;

    public NetworkUtils() {
        listeners = new ArrayList<NetworkUtils.NetworkStateReceiverListener>();
        connected = null;
    }


    public static boolean haveNetworkConnection(Context ctx) {

        return haveWifiConnection(ctx) || haveMobileConnection(ctx);
    }

    public static boolean haveWifiConnection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        ArrayList<NetworkInfo> netInfos = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network net : cm.getAllNetworks()) {
                netInfos.add(cm.getNetworkInfo(net));
            }
        } else {
            netInfos = new ArrayList<>(Arrays.asList(cm.getAllNetworkInfo()));
        }
        for (NetworkInfo ni : netInfos) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    return true;
        }
        return false;

    }


    public static boolean haveMobileConnection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        ArrayList<NetworkInfo> netInfos = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network net : cm.getAllNetworks()) {
                netInfos.add(cm.getNetworkInfo(net));
            }
        } else {
            netInfos = new ArrayList<>(Arrays.asList(cm.getAllNetworkInfo()));
        }
        for (NetworkInfo ni : netInfos) {
            if (ni.getTypeName().equalsIgnoreCase("MOBILE") && ni.isConnected())
                    return true;
        }
        return false;

    }

    public static boolean isActiveInternetPresent(){
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal==0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (haveNetworkConnection(context)) {
            if (isActiveInternetPresent())
            {
                //internet is working
                connected = true;
                OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
            }else
            {
                //Device is connected to WI-FI or Mobile Data but Internet is not working
                //show toast
                //will be useful if user have blocked notification for this app
                connected = false;
                Toast.makeText(context, R.string.waiting_for_network, Toast.LENGTH_LONG).show();
            }
        }
        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for(NetworkUtils.NetworkStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(NetworkUtils.NetworkStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;

        if(connected)
            listener.networkAvailable();
        else
            listener.networkUnavailable();
    }

    public void addListener(NetworkUtils.NetworkStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkUtils.NetworkStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface NetworkStateReceiverListener {
        public void networkAvailable();
        public void networkUnavailable();
    }
}