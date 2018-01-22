package org.fossasia.openevent.viewmodels.auth;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.auth.Login;
import org.fossasia.openevent.data.auth.User;
import org.fossasia.openevent.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SignUpActivityViewModel extends ViewModel {
    public static final int VALID = 0;
    public static final int EMPTY = 1;
    public static final int INVALID = 2;

    private MutableLiveData<Integer> signUpResponse;
    private MutableLiveData<Pair<Integer,String>> loginResponse;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public int validateEmail(String email) {
       if (Utils.isEmpty(email)) {
           return EMPTY;
       } else if (!Utils.isEmailValid(email)) {
           return INVALID;
       }
       return VALID;
    }

    public int validatePassword(String password) {
        if (Utils.isEmpty(password)) {
            return EMPTY;
        } else if (!Utils.isPasswordValid(password)) {
            return INVALID;
        }
        return VALID;
    }

    public int validateConfirmPassword(String confirmPassword, String password) {
        if (Utils.isEmpty(confirmPassword)) {
            return EMPTY;
        } else if (!confirmPassword.equals(password)) {
            return INVALID;
        }
        return VALID;
    }

    public LiveData<Integer> signUpUser(String email, String password) {
        if (signUpResponse == null) {
            signUpResponse = new MutableLiveData<>();
        }
        compositeDisposable.add(APIClient.getOpenEventAPI().signUp(User.builder().email(email).password(password).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                            Timber.d("Signed up successfully");
                            signUpResponse.setValue(SignUpActivityViewModel.VALID);
                        },
                        throwable -> {
                            Timber.d(throwable.toString());
                            signUpResponse.setValue(SignUpActivityViewModel.INVALID);
                        }));
        return signUpResponse;
    }

    public LiveData<Pair<Integer,String>> loginUserAfterSignUp(String email, String password) {
        if (loginResponse == null) {
            loginResponse = new MutableLiveData<>();
        }
        compositeDisposable.add(APIClient.getOpenEventAPI().login(new Login(email, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            Timber.d("Saved token and logged in successfully");
                            loginResponse.setValue(new Pair<>(SignUpActivityViewModel.VALID, response.getAccessToken()));
                        },
                        throwable -> {
                            Timber.d(throwable.toString());
                            loginResponse.setValue(new Pair<>(SignUpActivityViewModel.INVALID, ""));
                        }));
        return loginResponse;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
