package org.fossasia.openevent.core.notifications.repository;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.api.JWTUtils;
import org.fossasia.openevent.core.auth.AuthUtil;
import org.fossasia.openevent.data.Notification;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.json.JSONException;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NotificationsRepository {

    public Observable<List<Notification>> downloadNotifications() {
        try {
            int id = JWTUtils.getIdentity(AuthUtil.getAuthorization());
            return APIClient.getOpenEventAPI().getNotifications(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(notifications -> RealmDataRepository.getDefaultInstance().saveNotifications(notifications).subscribe());
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }
}
