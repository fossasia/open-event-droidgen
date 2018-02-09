package org.fossasia.openevent.core.faqs;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.data.FAQ;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FAQViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private LiveData<List<FAQ>> faqData;
    private MutableLiveData<Boolean> faqOnDownloadResponse;
    private final CompositeDisposable compositeDisposable;

    public FAQViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<FAQ>> getFaqData() {
        if (faqData == null) {
            LiveRealmData<FAQ> faqLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getEventFAQs());
            faqData = Transformations.map(faqLiveRealmData, input -> input);
        }
        return faqData;
    }

    public LiveData<Boolean> downloadFAQ() {
        if (faqOnDownloadResponse == null)
            faqOnDownloadResponse = new MutableLiveData<>();
        try {
            compositeDisposable.add(APIClient.getOpenEventAPI().getFAQs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(faqList -> {
                        Timber.i("Downloaded FAQs");
                        realmRepo.saveFAQs(faqList).subscribe();
                        faqOnDownloadResponse.setValue(true);
                    }, throwable -> {
                        Timber.i("FAQs download failed");
                        faqOnDownloadResponse.setValue(false);
                    }));
        } catch (Exception e) {
            Timber.e(e);
        }

        return faqOnDownloadResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
