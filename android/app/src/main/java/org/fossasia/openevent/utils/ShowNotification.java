package org.fossasia.openevent.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import org.fossasia.openevent.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by vishwesh3 on 6/12/16.
 */

public class ShowNotification {
    private  Context context;
    public  ShowNotification(Context context) {
        this.context =context;
        buildNotification();
    }

    private  void buildNotification(){
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
