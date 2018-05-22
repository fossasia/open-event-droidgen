package org.fossasia.openevent.core.notifications;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.core.notifications.repository.NotificationsRepository;
import org.fossasia.openevent.data.Notification;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class NotificationsFragmentViewModel extends ViewModel {

    private final RealmDataRepository realmRepo;

    private LiveRealmData<Notification> liveNotificationsRealmData;
    private MutableLiveData<Boolean> notificationDownloadResponse;
    private final CompositeDisposable compositeDisposable;
    private final NotificationsRepository notificationsRepository;

    public NotificationsFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
        notificationsRepository = new NotificationsRepository();
    }

    public LiveRealmData<Notification> getNotificationsData() {
        if (liveNotificationsRealmData == null) {
            liveNotificationsRealmData = RealmDataRepository.asLiveData(realmRepo.getNotifications());
        }
        return liveNotificationsRealmData;
    }

    public LiveData<Boolean> downloadNotifications() {
        if (notificationDownloadResponse == null) {
            notificationDownloadResponse = new MutableLiveData<>();
        }
        Observable<List<Notification>> notificationsObservable = notificationsRepository.downloadNotifications();
        if (notificationsObservable != null) {
            compositeDisposable.add(notificationsObservable.subscribe(notifications -> {
                Timber.i("Downloaded notifications");
                notificationDownloadResponse.setValue(true);
            }, throwable -> {
                Timber.e("Notification download failed");
                notificationDownloadResponse.setValue(false);
            }));
        }
        return notificationDownloadResponse;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
