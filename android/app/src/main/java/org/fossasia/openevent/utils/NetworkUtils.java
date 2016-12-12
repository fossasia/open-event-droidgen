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
                //Internet is working
                OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
            }else
            {
                //Device is connected to WI-FI or Mobile Data but Internet is not working
                //show toast
                //will be useful if user have blocked notification for this app
                Toast.makeText(context, R.string.waiting_for_network, Toast.LENGTH_LONG).show();
            }
        }
    }
}
