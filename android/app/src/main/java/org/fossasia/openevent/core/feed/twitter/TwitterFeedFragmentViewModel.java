package org.fossasia.openevent.core.feed.twitter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.core.feed.Resource;
import org.fossasia.openevent.core.feed.twitter.api.TwitterApi;
import org.fossasia.openevent.core.feed.twitter.api.TwitterFeed;
import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TwitterFeedFragmentViewModel extends ViewModel {
    private LiveData<Resource<TwitterFeed>> feedLiveData;

    public LiveData<Resource<TwitterFeed>> getPosts(String query, int count, String source) {
        if (feedLiveData == null) {
            Publisher<Resource<TwitterFeed>> publisher = TwitterApi.getLoklakAPI().getTwitterFeed(query, count, source)
                    .map(Resource::success)
                    .onErrorReturn(throwable -> {
                        Timber.e(throwable);
                        return Resource.error(throwable.getMessage(), null);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toFlowable(BackpressureStrategy.BUFFER);
            feedLiveData = LiveDataReactiveStreams.fromPublisher(publisher);
        }
        return feedLiveData;
    }
}
