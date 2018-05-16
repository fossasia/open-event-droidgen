package org.fossasia.openevent.core.feedback;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.data.Feedback;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FeedbackFragmentViewModel extends ViewModel {

    private final RealmDataRepository realmRepo;
    private final CompositeDisposable compositeDisposable;
    private LiveRealmData<Feedback> feedbackLiveRealmData;
    private MutableLiveData<Boolean> feedbackDownloadResponse;

    public FeedbackFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveRealmData<Feedback> getFeedback() {
        if (feedbackLiveRealmData == null) {
            feedbackLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getFeedbacks());
        }
        return feedbackLiveRealmData;
    }

    public LiveData<Boolean> downloadFeedbacks() {
        if (feedbackDownloadResponse == null)
            feedbackDownloadResponse = new MutableLiveData<>();

        compositeDisposable.add(APIClient.getOpenEventAPI().getFeedbacks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feedbackList -> {
                    Timber.i("Downloaded Feedbacks ");
                    realmRepo.saveFeedbacks(feedbackList).subscribe();
                    feedbackDownloadResponse.setValue(true);
                }, throwable -> {
                    Timber.i("Feedbacks Codes download failed");
                    feedbackDownloadResponse.setValue(false);
                }));

        return feedbackDownloadResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}