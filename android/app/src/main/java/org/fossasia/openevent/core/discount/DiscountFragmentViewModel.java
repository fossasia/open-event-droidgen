package org.fossasia.openevent.core.discount;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.data.DiscountCode;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DiscountFragmentViewModel extends ViewModel {

    private final CompositeDisposable compositeDisposable;
    private final RealmDataRepository realmRepo;

    private LiveRealmData<DiscountCode> discountCodeLiveRealmData;
    private MutableLiveData<Boolean> discountCodesDownloadResponse;

    public DiscountFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }


    public LiveRealmData<DiscountCode> getDiscountCodes() {
        if (discountCodeLiveRealmData == null) {
            discountCodeLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getDiscountCodes());
        }
        return discountCodeLiveRealmData;
    }

    public LiveData<Boolean> downloadDiscountCodes() {
        if (discountCodesDownloadResponse == null)
            discountCodesDownloadResponse = new MutableLiveData<>();
        compositeDisposable.add(APIClient.getOpenEventAPI().getDiscountCodes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(discountList -> {
                    Timber.i("Downloaded Discount Codes");
                    realmRepo.saveDiscountCodes(discountList).subscribe();
                    discountCodesDownloadResponse.setValue(true);
                }, throwable -> {
                    Timber.i("Discount Codes download failed");
                    discountCodesDownloadResponse.setValue(false);
                }));

        return discountCodesDownloadResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}