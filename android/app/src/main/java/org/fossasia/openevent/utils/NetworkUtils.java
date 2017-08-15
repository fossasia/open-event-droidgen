package org.fossasia.openevent.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

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
public class NetworkUtils {

    public static final String TYPE_WIFI = "WIFI";

    public static final String TYPE_MOBILE = "MOBILE";

    public static Single<Boolean> haveNetworkConnectionObservable(final Context context) {
        return Single.fromCallable(() -> haveNetworkConnection(context));
    }

    public static boolean haveNetworkConnection(Context context) {
        return haveWifiConnection(context) || haveMobileConnection(context);
    }

    public static boolean haveWifiConnection(Context context) {
        return haveConnection(context, TYPE_WIFI);
    }

    public static boolean haveMobileConnection(Context context) {
        return haveConnection(context, TYPE_MOBILE);
    }

    public static boolean haveConnection(Context context, String NETWORK_TYPE) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ArrayList<NetworkInfo> networkInfos = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (Network network : connectivityManager.getAllNetworks()) {
                networkInfos.add(connectivityManager.getNetworkInfo(network));
            }
        } else {
            networkInfos = new ArrayList<>(Arrays.asList(connectivityManager.getAllNetworkInfo()));
        }
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo != null && networkInfo.getTypeName().equalsIgnoreCase(NETWORK_TYPE) && networkInfo.isConnected())
                return true;
        }
        return false;
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
                    } else {
                        listener.networkUnavailable();
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                    Timber.e("Network Determination Error : %s", throwable.getMessage());
                });
    }

    public interface NetworkStateReceiverListener {

        void networkAvailable();

        void networkUnavailable();
    }
}