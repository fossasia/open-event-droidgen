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
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
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
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return haveNetworkConnection(context);
            }
        });
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
            if (ni != null && ni.getTypeName().equalsIgnoreCase("WIFI"))
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
            if (ni != null && ni.getTypeName().equalsIgnoreCase("MOBILE") && ni.isConnected())
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
            e.printStackTrace();
        }
        return false;
    }

    public static Single<Boolean> isActiveInternetPresentObservable() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isActiveInternetPresent();
            }
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

    public static void checkConnection(WeakReference<Context> reference, final NetworkStateReceiverListener listener) {
        if(reference.get() == null || listener == null)
            return;

        haveNetworkConnectionObservable(reference.get())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean hasConnection) throws Exception {
                        if (hasConnection) {
                            listener.networkAvailable();
                            isActiveInternetPresentObservable()
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Boolean>() {
                                        @Override
                                        public void accept(@NonNull Boolean isActive) throws Exception {
                                            if (isActive) {
                                                listener.activeConnection();
                                            } else {
                                                listener.inactiveConnection();
                                            }
                                        }
                                    });
                        } else {
                            listener.networkUnavailable();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Timber.e("Network Determination Error : %s", throwable.getMessage());
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