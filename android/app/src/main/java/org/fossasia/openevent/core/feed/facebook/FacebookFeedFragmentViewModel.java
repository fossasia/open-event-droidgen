package org.fossasia.openevent.core.feed.facebook;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.core.feed.Resource;
import org.fossasia.openevent.core.feed.facebook.api.FacebookApi;
import org.fossasia.openevent.core.feed.facebook.api.FacebookPageId;
import org.fossasia.openevent.core.feed.facebook.api.Feed;
import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FacebookFeedFragmentViewModel extends ViewModel {

    private LiveData<Resource<FacebookPageId>> facebookPageIdLiveData;
    private LiveData<Resource<Feed>> feedLiveData;

    public LiveData<Resource<FacebookPageId>> getFBPageID(String fbAccessToken) {
        if (facebookPageIdLiveData == null) {
            Publisher<Resource<FacebookPageId>> publisher = FacebookApi.getFacebookGraphAPI().getPageId(SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null),
                    fbAccessToken)
                    .map((Function<FacebookPageId, Resource<FacebookPageId>>) facebookPageId -> {
                        if (facebookPageId != null && facebookPageId.getId() != null) {
                            return Resource.success(facebookPageId);
                        } else {
                            return Resource.error("facebookPageId returned null", null);
                        }
                    })
                    .onErrorReturn(throwable -> {
                        Timber.e(throwable);
                        return Resource.error(throwable.getMessage(), null);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toFlowable(BackpressureStrategy.BUFFER);
            facebookPageIdLiveData = LiveDataReactiveStreams.fromPublisher(publisher);
        }
        return facebookPageIdLiveData;
    }

    public LiveData<Resource<Feed>> getPosts(String fields, String fbAccessToken, String fbPageId) {
        if (feedLiveData == null) {
            Publisher<Resource<Feed>> publisher = FacebookApi.getFacebookGraphAPI()
                    .getPosts(fbPageId,
                            fields,
                            fbAccessToken)
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
