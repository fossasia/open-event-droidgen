package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.RetrofitError;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

abstract class ResponseProcessor<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(!response.isSuccessful()) {
            OpenEventApp.postEventOnUIThread(getErrorResponseEvent(response.code()));
        } else {
            onSuccess(response.body());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        Timber.e(throwable);
        OpenEventApp.postEventOnUIThread(new RetrofitError(throwable));

        DownloadEvent downloadEvent = getDownloadEvent(false);
        if (downloadEvent != null)
            OpenEventApp.getEventBus().post(downloadEvent);

        RealmDataRepository.getDefaultInstance()
                .clearVersions()
                .subscribeOn(Schedulers.computation())
                .subscribe(() -> Timber.d("Deleting of Version table complete"), Timber::e);
    }

    protected void complete(Completable completable) {
        DownloadEvent downloadEvent = getDownloadEvent(true);
        if (downloadEvent == null)
            return;

        completable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> OpenEventApp.postEventOnUIThread(downloadEvent), Timber::e);
    }

    protected abstract void onSuccess(T result);

    protected abstract DownloadEvent getDownloadEvent(boolean success);

    protected abstract Object getErrorResponseEvent(int errorCode);
}
