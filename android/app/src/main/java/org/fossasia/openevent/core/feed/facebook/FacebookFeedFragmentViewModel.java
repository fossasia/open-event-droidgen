package org.fossasia.openevent.core.feed.facebook;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.core.feed.facebook.api.FacebookApi;
import org.fossasia.openevent.core.feed.facebook.api.FacebookPageId;
import org.fossasia.openevent.core.feed.facebook.api.Feed;
import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Data;

import static org.fossasia.openevent.core.auth.AuthUtil.INVALID;
import static org.fossasia.openevent.core.auth.AuthUtil.VALID;

public class FacebookFeedFragmentViewModel extends ViewModel {

    private LiveData<FacebookPageId> facebookPageIdLiveData;
    private MutableLiveData<PostsResponse> feedLiveData;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FacebookFeedFragmentViewModel() {
        facebookPageIdLiveData = new MutableLiveData<>();
        feedLiveData = new MutableLiveData<>();
    }

    public LiveData<FacebookPageId> updateFBPageID(String fbAccessToken, String fbPageId) {
        if (fbPageId == null) {
            Publisher<FacebookPageId> publisher = FacebookApi.getFacebookGraphAPI().getPageId(SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null),
                    fbAccessToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toFlowable(BackpressureStrategy.BUFFER);
            facebookPageIdLiveData = LiveDataReactiveStreams.fromPublisher(publisher);
        }
        return facebookPageIdLiveData;
    }

    public LiveData<PostsResponse> getPosts(String fields, String fbAccessToken, String fbPageId) {
        compositeDisposable.add(FacebookApi.getFacebookGraphAPI()
                .getPosts(fbPageId,
                        fields,
                        fbAccessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feed -> feedLiveData.setValue(new PostsResponse(VALID, feed)),
                        throwable -> feedLiveData.setValue(new PostsResponse(INVALID, null))));
        return feedLiveData;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    @Data
    public static class PostsResponse {
        private final int response;
        private final Feed feed;
    }
}
