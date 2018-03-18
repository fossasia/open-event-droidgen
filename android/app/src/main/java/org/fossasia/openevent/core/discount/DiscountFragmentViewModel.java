package org.fossasia.openevent.core.discount;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.data.DiscountCode;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DiscountFragmentViewModel extends ViewModel {

    private LiveData<List<DiscountCode>> discountCodes;
    private RealmDataRepository realmRepo;
    private MutableLiveData<Boolean> discountCodesDownloadResponse;
    private CompositeDisposable compositeDisposable;

    public DiscountFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }


    public LiveData<List<DiscountCode>> getDiscountCodes() {
        if (discountCodes == null) {
            LiveRealmData<DiscountCode> discountCodeLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getDiscountCodes());
            discountCodes = Transformations.map(discountCodeLiveRealmData, input -> input);
        }
        return discountCodes;
    }

    public LiveData<Boolean> downloadDiscountCodes() {
        if (discountCodesDownloadResponse == null)
            discountCodesDownloadResponse = new MutableLiveData<>();
        try {
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
        } catch (Exception e) {
            Timber.e(e);
        }

        return discountCodesDownloadResponse;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}