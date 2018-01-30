package org.fossasia.openevent.core.auth.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.core.auth.model.User;
import org.fossasia.openevent.common.arch.LiveRealmDataObject;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.api.JWTUtils;
import org.json.JSONException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class UserProfileActivityViewModel extends ViewModel {
    public static final int RETRIEVE_SUCCESSFUL = 1;
    public static final int RETRIEVE_UNSUCCESSFUL = 2;

    private final CompositeDisposable compositeDisposable;
    private RealmDataRepository realmRepo;
    private MutableLiveData<Integer> loadUserFromNetworkResponse;
    private LiveRealmDataObject<User> liveRealmDataObject;

    public UserProfileActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }

    public void eraseUserData() {
        realmRepo.clearUserData();
    }

    public LiveData<User> getUser() {
        if(liveRealmDataObject == null) {
            liveRealmDataObject = RealmDataRepository.asLiveDataForObject(realmRepo.getUser());
        }
        return  liveRealmDataObject;
    }

    public LiveData<Integer> loadUserFromNetwork(String auth) throws JSONException {
        int id= JWTUtils.getIdentity(auth);
        if(loadUserFromNetworkResponse == null){
            loadUserFromNetworkResponse = new MutableLiveData<>();
        }
        Disposable disposable = APIClient.getOpenEventAPI()
                .getUser(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(user -> RealmDataRepository
                        .getDefaultInstance()
                        .saveUser(user))
                .subscribe(() -> {
                    loadUserFromNetworkResponse.setValue(RETRIEVE_SUCCESSFUL);
                    Timber.d("User data saved in database");
                },throwable -> {
                    loadUserFromNetworkResponse.setValue(RETRIEVE_UNSUCCESSFUL);
                    Timber.d(throwable.getMessage() + " Error getting data from network");
                });

        compositeDisposable.add(disposable);
        return loadUserFromNetworkResponse;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
