package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.twitter.TwitterFeed;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Data;

import static org.fossasia.openevent.utils.AuthUtil.INVALID;
import static org.fossasia.openevent.utils.AuthUtil.VALID;

public class TwitterFeedFragmentViewModel extends ViewModel {
    private MutableLiveData<PostsResponse> feedLiveData;
    private final CompositeDisposable compositeDisposable;

    public TwitterFeedFragmentViewModel() {
        compositeDisposable = new CompositeDisposable();
        feedLiveData = new MutableLiveData<>();
    }

    public LiveData<PostsResponse> getPosts(String query, int count, String source) {
        compositeDisposable.add(APIClient.getLoklakAPI().getTwitterFeed(query, count, source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feed -> feedLiveData.setValue(new PostsResponse(VALID, feed))
                    , throwable -> feedLiveData.setValue(new PostsResponse(INVALID, null))));
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
        private final TwitterFeed twitterFeed;
    }
}
