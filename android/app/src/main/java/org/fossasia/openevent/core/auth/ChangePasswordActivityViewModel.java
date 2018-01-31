package org.fossasia.openevent.core.auth;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.core.auth.model.Login;
import org.fossasia.openevent.core.auth.model.User;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.api.JWTUtils;
import org.json.JSONException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ChangePasswordActivityViewModel extends ViewModel {

    public static final int ON_ERROR = 2;
    public static final int ON_COMPLETE = 3;
    public static final int ON_EMPTY_PASSWORD = 4;

    private RealmDataRepository realmRepo;
    private User user;
    private LiveData<User> userLiveData;
    private MutableLiveData<Integer> checkPasswordResponse;
    private MutableLiveData<Integer> changePasswordResponse;
    private final CompositeDisposable compositeDisposable;

    public ChangePasswordActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<User> getUser() {
        if (userLiveData == null) {
            user = realmRepo.getUser();
            userLiveData = RealmDataRepository.asLiveDataForObject(user);
        }
        return userLiveData;
    }

    public LiveData<Integer> checkCurrentPassword(String currentPassword) {
        if (checkPasswordResponse == null) {
            checkPasswordResponse = new MutableLiveData<>();
        }
        
        compositeDisposable.add(APIClient.getOpenEventAPI().login(new Login(user.getEmail(), currentPassword))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                            Timber.d("Received token. Password is correct");
                        },
                        throwable -> {
                            checkPasswordResponse.setValue(ON_ERROR);
                            Timber.d("Error changing password: " + throwable.getMessage());
                        },
                        () -> {
                            checkPasswordResponse.setValue(ON_COMPLETE);
                        }));
        return checkPasswordResponse;
    }

    public LiveData<Integer> changePassword(String newPassword) {
        if (changePasswordResponse == null) {
            changePasswordResponse = new MutableLiveData<>();
        }
        if (TextUtils.isEmpty(newPassword)) {
            changePasswordResponse.setValue(ON_EMPTY_PASSWORD);
            return changePasswordResponse;
        }
        int id;
        try {
            id = JWTUtils.getIdentity(AuthUtil.getAuthorization());
            compositeDisposable.add(APIClient.getOpenEventAPI().updateUser(User.builder().id(id).password(newPassword).build(), id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapCompletable(user -> RealmDataRepository
                            .getDefaultInstance()
                            .saveUser(user))
                    .subscribe(() -> {
                                changePasswordResponse.setValue(ON_COMPLETE);
                                Timber.d("User data saved in database");
                                Timber.d("Password changed successfully");
                            },
                            throwable -> {
                                changePasswordResponse.setValue(ON_ERROR);
                                Timber.d("Error changing password" + throwable.getMessage());
                            }));
        } catch (JSONException e) {
            changePasswordResponse.setValue(ON_ERROR);
            Timber.e(e);
        }
        return changePasswordResponse;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
