package org.fossasia.openevent.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.fossasia.openevent.Services.BookmarkAlarmService;
import org.fossasia.openevent.utils.IntentStrings;

/**
 * Created by Manan Wason on 21/08/15.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int SessionId = intent.getIntExtra(IntentStrings.SESSION, 0);
        Intent service1 = new Intent(context, BookmarkAlarmService.class);
        service1.putExtra(IntentStrings.SESSION, SessionId);
        context.startService(service1);

    }
}