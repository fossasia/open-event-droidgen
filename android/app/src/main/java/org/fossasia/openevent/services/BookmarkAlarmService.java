package org.fossasia.openevent.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;

public class BookmarkAlarmService extends IntentService {

    public BookmarkAlarmService(String name) {
        super(name);
    }

    public BookmarkAlarmService(){
        super("BookMarkAlarmService");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //This method is invoked on the worker thread with a request to process intent
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleStart(Intent intent) {
        NotificationManager mManager = (NotificationManager) this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(ConstantStrings.SESSION, 0);
        String session_date;
        DbSingleton dbSingleton = DbSingleton.getInstance();
        Session session = dbSingleton.getSessionById(id);
        Intent intent1 = new Intent(this.getApplicationContext(), SessionDetailActivity.class);
        intent1.putExtra(ConstantStrings.SESSION, session.getTitle());
        intent1.putExtra(ConstantStrings.ID, session.getId());
        intent1.putExtra(ConstantStrings.TRACK,session.getTrack().getName());
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        int smallIcon = R.drawable.ic_bookmark_white_24dp;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) smallIcon = R.drawable.ic_noti_bookmark;

        String start = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(session.getStartTime()));
        String end = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(session.getEndTime()));
        String session_timings = start + " - " + end;
        session_date = ISO8601Date.getDate(ISO8601Date.getDateObject(session.getStartTime()));


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setContentTitle(session.getTitle())
                .setContentText(session_date + "\n" + session_timings)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(session_date + "\n" + session_timings))
                .setContentIntent(pendingNotificationIntent);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mManager.notify(session.getId(), mBuilder.build());
    }

}