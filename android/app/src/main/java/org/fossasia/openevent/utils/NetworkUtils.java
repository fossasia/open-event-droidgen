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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by championswimmer on 21/6/16.
 */
public class NetworkUtils extends BroadcastReceiver {

    public static boolean haveNetworkConnection(Context ctx) {
        return haveWifiConnection(ctx) || haveMobileConnection(ctx);
    }

    public static Single<Boolean> haveNetworkConnectionObservable(final Context context) {
        return Single.fromCallable(() -> haveNetworkConnection(context));
    }

    public static boolean haveWifiConnection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        ArrayList<NetworkInfo> networkInfos = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network net : cm.getAllNetworks()) {
                networkInfos.add(cm.getNetworkInfo(net));
            }
        } else {
            networkInfos = new ArrayList<>(Arrays.asList(cm.getAllNetworkInfo()));
        }
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo != null && networkInfo.getTypeName().equalsIgnoreCase("WIFI") && networkInfo.isConnected())
                return true;
        }
        return false;

    }

    public static boolean haveMobileConnection(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        ArrayList<NetworkInfo> networkInfos = new ArrayList<>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network network : cm.getAllNetworks()) {
                networkInfos.add(cm.getNetworkInfo(network));
            }
        } else {
            networkInfos = new ArrayList<>(Arrays.asList(cm.getAllNetworkInfo()));
        }
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo != null && networkInfo.getTypeName().equalsIgnoreCase("MOBILE") && networkInfo.isConnected())
                return true;
        }
        return false;

    }

    public static boolean isActiveInternetPresent() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Single<Boolean> isActiveInternetPresentObservable() {
        return Single.fromCallable(() -> isActiveInternetPresent());
    }

    public static void checkConnection(WeakReference<Context> reference, final NetworkStateReceiverListener listener) {
        if (reference.get() == null || listener == null)
            return;

        haveNetworkConnectionObservable(reference.get())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hasConnection -> {

                    if (hasConnection) {
                        listener.networkAvailable();
                        isActiveInternetPresentObservable()
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(isActive -> {
                                    if (isActive) {
                                        listener.activeConnection();
                                    } else {
                                        listener.inactiveConnection();
                                    }
                                });
                    } else {
                        listener.networkUnavailable();
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                    Timber.e("Network Determination Error : %s", throwable.getMessage());
                });
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        checkConnection(new WeakReference<>(context), new NetworkStateReceiverListener() {
            @Override
            public void activeConnection() {
                //internet is working
                OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
            }

            @Override
            public void inactiveConnection() {
                //Device is connection to WI-FI or Mobile Data but Internet is not working
                //show toast
                //will be useful if user have blocked notification for this app
                Toast.makeText(context, R.string.waiting_for_network, Toast.LENGTH_LONG).show();
            }

            @Override
            public void networkAvailable() {
                // Waiting for network activity
            }

            @Override
            public void networkUnavailable() {
                // Network unavailable
            }
        });

    }

    public interface NetworkStateReceiverListener {
        void activeConnection();

        void inactiveConnection();

        void networkAvailable();

        void networkUnavailable();
    }
}