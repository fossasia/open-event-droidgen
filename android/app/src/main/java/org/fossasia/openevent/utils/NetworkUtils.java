package org.fossasia.openevent.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.events.DataDownloadEvent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by championswimmer on 21/6/16.
 */
public class NetworkUtils extends BroadcastReceiver {

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
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    return true;
        }
        return false;

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (haveNetworkConnection(context)) {
            OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
        }
    }
}
