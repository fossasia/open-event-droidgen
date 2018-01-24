package org.fossasia.openevent.viewmodels.auth;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.auth.Login;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Data;
import timber.log.Timber;

import static org.fossasia.openevent.utils.AuthUtil.INVALID;
import static org.fossasia.openevent.utils.AuthUtil.VALID;

public class LoginActivityViewModel extends ViewModel {

    private MutableLiveData<LoginResponse> loginResponse;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LiveData<LoginResponse> loginUser(String email, String password) {
        if (loginResponse == null) {
            loginResponse = new MutableLiveData<>();
        }
        compositeDisposable.add(APIClient.getOpenEventAPI().login(new Login(email, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            Timber.d("Saved token and logged in successfully");
                            loginResponse.setValue(new LoginResponse(VALID, response.getAccessToken()));
                        }, throwable -> {
                            Timber.e(throwable.toString());
                            loginResponse.setValue(new LoginResponse(INVALID, ""));
                        }));
        return loginResponse;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    @Data
    public static class LoginResponse {
        private final int response;
        private final String accessToken;
    }
}
