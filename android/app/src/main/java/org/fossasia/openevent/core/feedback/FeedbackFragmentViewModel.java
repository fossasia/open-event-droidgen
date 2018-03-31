package org.fossasia.openevent.core.feedback;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.data.Feedback;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FeedbackFragmentViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private CompositeDisposable compositeDisposable;

    private LiveData<List<Feedback>> feedbackList;
    private MutableLiveData<Boolean> feedbackDownloadResponse;

    public FeedbackFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<Feedback>> getFeedback() {
        if (feedbackList == null) {
            LiveRealmData<Feedback> feedbackLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getFeedbacks());
            feedbackList = Transformations.map(feedbackLiveRealmData, input -> input);
        }
        return feedbackList;
    }

    public LiveData<Boolean> downloadFeedbacks() {
        if (feedbackDownloadResponse == null)
            feedbackDownloadResponse = new MutableLiveData<>();
        try {
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
        } catch (Exception e) {
            Timber.e(e);
        }

        return feedbackDownloadResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}