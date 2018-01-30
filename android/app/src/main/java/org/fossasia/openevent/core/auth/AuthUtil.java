package org.fossasia.openevent.core.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.core.main.MainActivity;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.api.JWTUtils;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.common.utils.Utils;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import timber.log.Timber;

public class AuthUtil {

    public static final int VALID = 0;
    public static final int EMPTY = 1;
    public static final int INVALID = 2;

    private static Authenticator authenticator;

    public static void logout(Context context) {
        SharedPreferencesUtil.remove(ConstantStrings.TOKEN);
        SharedPreferencesUtil.remove(ConstantStrings.USER_EMAIL);
        SharedPreferencesUtil.remove(ConstantStrings.USER_FIRST_NAME);
        SharedPreferencesUtil.remove(ConstantStrings.USER_LAST_NAME);

        //Delete User data from realm
        RealmDataRepository.getDefaultInstance().clearUserData();

        goToMainActivity(context);
        showMessage(R.string.logged_out_successfully);
        Timber.d("Removed token & email and logged out successfully");
    }

    public static Authenticator getAuthenticator() {
        if (authenticator == null) {
            authenticator = new Authenticator() {
                @Nullable
                @Override
                public Request authenticate(@NonNull Route route, @NonNull Response response) throws IOException {
                    if (response.request().header("Authorization") != null) {
                        return null; // Give up, we've already failed to authenticate.
                    }

                    String token = getToken();

                    if (token == null) {
                        Timber.wtf("Someone tried to access authenticated resource without auth token");
                        return null;
                    }

                    return response.request().newBuilder()
                            .header("Authorization", formatToken(token))
                            .build();
                }
            };
        }
        return authenticator;
    }

    public static String getAuthorization() {
        return formatToken(getToken());
    }

    public static String getToken() {
        return SharedPreferencesUtil.getString(ConstantStrings.TOKEN, null);
    }

    public static String formatToken(@NonNull String token) {
        return String.format("JWT %s", token);
    }

    public static boolean isUserLoggedIn() {
        String token = getToken();
        return token != null && !JWTUtils.isExpired(token);
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

    public static int validateEmail(String email) {
        if (Utils.isEmpty(email)) {
            return EMPTY;
        } else if (!Utils.isEmailValid(email)) {
            return INVALID;
        }
        return VALID;
    }

    public static int validatePassword(String password) {
        if (Utils.isEmpty(password)) {
            return EMPTY;
        } else if (!Utils.isPasswordValid(password)) {
            return INVALID;
        }
        return VALID;
    }
}
