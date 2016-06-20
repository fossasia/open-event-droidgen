package org.fossasia.openevent.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by championswimmer on 21/6/16.
 */
public class NetworkUtils {

    public static boolean haveNetworkConnection(Context ctx) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

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
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
