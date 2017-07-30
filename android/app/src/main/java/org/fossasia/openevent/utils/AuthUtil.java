package org.fossasia.openevent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.auth.Login;
import org.fossasia.openevent.data.auth.SignUp;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AuthUtil {

    public static void signUpUser(Context context, String email, String password, ProgressBar progressBar) {
        APIClient.getOpenEventAPI().signUp(new SignUp(email, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> loginUser(context, email, password, progressBar),
                        throwable -> {
                            showProgressBar(progressBar, false);
                            showMessage(R.string.error_in_signing_up);
                            Timber.d(throwable.toString());
                        },
                        () -> {
                            showProgressBar(progressBar, false);
                            showMessage(R.string.signed_up_successfully);
                            Timber.d("Signed up successfully");
                        },
                        disposable -> showProgressBar(progressBar, true));
    }

    public static void loginUser(Context context, String email, String password, ProgressBar progressBar) {
        APIClient.getOpenEventAPI().login(new Login(email, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loginResponse -> {
                            //Save token & email in shared preferences
                            SharedPreferencesUtil.putString(ConstantStrings.TOKEN, loginResponse.getAccessToken());
                            SharedPreferencesUtil.putString(ConstantStrings.USER_EMAIL, email);
                            goToMainActivity(context);
                        },
                        throwable -> {
                            showProgressBar(progressBar, false);
                            showMessage(R.string.error_authentication_failed);
                            Timber.d(throwable.toString());
                        },
                        () -> {
                            showProgressBar(progressBar, false);
                            showMessage(R.string.logged_in_successfully);
                            Timber.d("Saved token and logged in successfully");
                        },
                        disposable -> showProgressBar(progressBar, true));
        Timber.d(email);
    }

    public static void logout(Context context) {
        SharedPreferencesUtil.remove(ConstantStrings.TOKEN);
        SharedPreferencesUtil.remove(ConstantStrings.USER_EMAIL);
        goToMainActivity(context);
        showMessage(R.string.logged_out_successfully);
        Timber.d("Removed token & email and logged out successfully");
    }

    private static void goToMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ActivityCompat.finishAffinity((Activity) context);
    }

    private static void showProgressBar(ProgressBar progressBar, boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private static void showMessage(@StringRes int id) {
        Toast.makeText(OpenEventApp.getAppContext(), id, Toast.LENGTH_SHORT).show();
    }
}
