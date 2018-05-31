package org.fossasia.openevent.core.feedback;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.core.auth.model.User;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Feedback;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import timber.log.Timber;

public class FeedbackFragmentViewModel extends ViewModel {

    public static final int ON_ERROR = 2;
    public static final int ON_SUCCESS = 1;

    private final RealmDataRepository realmRepo;
    private final CompositeDisposable compositeDisposable;
    private LiveRealmData<Feedback> feedbackLiveRealmData;
    private MutableLiveData<Boolean> feedbackDownloadResponse;
    private MutableLiveData<Integer> postFeedbackResponse;

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

    public LiveData<Integer> postFeedback(float rating, String comments) {
        if (postFeedbackResponse == null) {
            postFeedbackResponse = new MutableLiveData<>();
        }
        User user = realmRepo.getUserSync();
        Event event = realmRepo.getEventSync();
        Realm realm = Realm.getDefaultInstance();
        Event eventCopy = realm.copyFromRealm(event, 0);
        User userCopy = realm.copyFromRealm(user,0);
        
        if (user == null || event == null) {
           postFeedbackResponse.setValue(ON_ERROR);
        } else {
            compositeDisposable.add(APIClient.getOpenEventAPI().postFeedback(Feedback.builder().event(eventCopy)
                    .rating(Float.toString(rating)).comment(comments).user(userCopy).build())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(feedback -> {
                                postFeedbackResponse.setValue(ON_SUCCESS);
                                Timber.d("Posted successfully");
                            },
                            throwable -> {
                                postFeedbackResponse.setValue(ON_ERROR);
                                Timber.d("Error posting feedback " + throwable.toString());
                            }));
        }
        realm.close();
        return postFeedbackResponse;
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
                    Timber.i("Feedbacks download failed");
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