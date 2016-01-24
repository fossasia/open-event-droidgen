package org.fossasia.openevent.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.IntentStrings;

/**
 * Created by Manan Wason on 21/08/15.
 */
public class BookmarkAlarmService extends IntentService {

    public BookmarkAlarmService() {
        super(null);
    }

    public BookmarkAlarmService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_NOT_STICKY;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void handleStart(Intent intent, int startId) {
        NotificationManager mManager = (NotificationManager) this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(IntentStrings.SESSION, 0);
        String session_timings = intent.getStringExtra(IntentStrings.SESSION_TIMING);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        Session session = dbSingleton.getSessionById(id);
        Intent intent1 = new Intent(this.getApplicationContext(), SessionDetailActivity.class);
        intent1.putExtra(IntentStrings.SESSION, session.getTitle());
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(session.getTitle())
                .setContentText(session_timings)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(session_timings))
                .setContentIntent(pendingNotificationIntent);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mManager.notify(1, mBuilder.build());
    }

}