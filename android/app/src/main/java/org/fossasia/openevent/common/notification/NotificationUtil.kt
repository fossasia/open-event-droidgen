package org.fossasia.openevent.common.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import org.fossasia.openevent.common.ConstantStrings
import org.fossasia.openevent.common.date.DateConverter
import org.fossasia.openevent.data.Session
import org.fossasia.openevent.common.utils.SharedPreferencesUtil
import org.threeten.bp.ZonedDateTime

import io.reactivex.Completable
import timber.log.Timber

object NotificationUtil {

    private fun onSuccess(session: Session) {
        Timber.d("Created notification for Session ${session.id} ${session.title} at time ${session.startsAt}")
    }

    private fun onError(throwable: Throwable, session: Session) {
        Timber.e(throwable)
        Timber.e("Error creating Date for Session ${session.id} ${session.title} at time ${session.startsAt}")
    }

    @JvmStatic
    fun createNotification(session: Session, context: Context): Completable {
        return Completable.fromAction {
            val zonedDateTime = DateConverter.getDate(session.startsAt)

            val prefResult = Integer.parseInt(SharedPreferencesUtil.getString("notification", "10 mins")?.substring(0, 2)?.trim { it <= ' ' })
            when (prefResult) {
                1 -> zonedDateTime.minusHours(-1)
                12 -> zonedDateTime.minusHours(12)
                else -> zonedDateTime.minusMinutes(10)
            }
            // Checking if the event time is after the current time
            if (zonedDateTime.isAfter(ZonedDateTime.now())) {
                val myIntent = Intent(context, NotificationAlarmReceiver::class.java)
                myIntent.putExtra(ConstantStrings.SESSION, session.id)
                val pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.set(AlarmManager.RTC, zonedDateTime.toInstant().toEpochMilli(), pendingIntent)
            } else {
                Timber.d("Session is finished. Skipping showing notification")
            }
        }.doOnComplete { onSuccess(session) }.doOnError { throwable -> onError(throwable, session) }
    }
}
