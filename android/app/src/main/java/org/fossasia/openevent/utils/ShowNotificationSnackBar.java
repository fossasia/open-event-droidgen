package org.fossasia.openevent.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import org.fossasia.openevent.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by vishwesh3 on 8/12/16.
 */

public abstract class ShowNotificationSnackBar {
    private  Context context;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;
    public ShowNotificationSnackBar(Context context, View view, SwipeRefreshLayout swipeRefreshLayout) {
        this.context =context;
        this.view = view;
        this.swipeRefreshLayout = swipeRefreshLayout;
        buildNotification();
        showSnackBar();
    }

    public abstract void refreshClicked();

    public Snackbar showSnackBar(){
        snackbar = Snackbar.make(view, R.string.waiting_for_network, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_refresh_action, v -> {
                    if (swipeRefreshLayout!=null)
                        swipeRefreshLayout.setRefreshing(true);
                    snackbar.dismiss();
                    refreshClicked();
                });
        snackbar.show();
        return snackbar;
    }



    public void buildNotification(){
        if(context == null)
            return;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo isWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (isWifi.isConnectedOrConnecting()){
            NotificationManager mManager = (NotificationManager) context.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(context.getString(R.string.check_connection))
                    .setContentText(context.getString(R.string.open_your_web_brower_to_finish_connection_setting))
                    .setAutoCancel(true)
                    .setContentIntent(pendingNotificationIntent);

            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mManager.notify(1, mBuilder.build());
        }
    }
}
