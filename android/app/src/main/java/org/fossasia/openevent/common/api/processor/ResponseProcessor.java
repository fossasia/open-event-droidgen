package org.fossasia.openevent.common.api.processor;

import org.fossasia.openevent.common.events.DownloadEvent;
import org.fossasia.openevent.common.events.RetrofitError;
import org.fossasia.openevent.config.StrategyRegistry;

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
            StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(getErrorResponseEvent(response.code()));
        } else {
            onSuccess(response.body());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        Timber.e(throwable);
        StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new RetrofitError(throwable));

        DownloadEvent downloadEvent = getDownloadEvent(false);
        if (downloadEvent != null)
            StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().post(downloadEvent);
    }

    protected void complete(Completable completable) {
        DownloadEvent downloadEvent = getDownloadEvent(true);
        if (downloadEvent == null)
            return;

        completable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(downloadEvent), Timber::e);
    }

    protected abstract void onSuccess(T result);

    protected abstract DownloadEvent getDownloadEvent(boolean success);

    protected abstract Object getErrorResponseEvent(int errorCode);
}
