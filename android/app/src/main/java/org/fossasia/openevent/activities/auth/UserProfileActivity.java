package org.fossasia.openevent.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.auth.User;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.AuthUtil;
import org.fossasia.openevent.utils.CircleTransform;
import org.fossasia.openevent.utils.JWTUtils;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.Views;
import org.json.JSONException;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.first_name)
    TextView firstName;
    @BindView(R.id.last_name)
    TextView lastName;
    @BindView(R.id.email)
    TextView email;

    @BindView(R.id.coordinate_layout_user_profile)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.user_profile_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.edit_profile)
    LinearLayout editProfile;
    @BindView(R.id.change_password)
    LinearLayout changePassword;
    @BindView(R.id.logout)
    LinearLayout logout;

    private User user;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!AuthUtil.isUserLoggedIn()) {
            //Delete User data from realm
            RealmDataRepository.getDefaultInstance().clearUserData();

            Intent intent = new Intent(UserProfileActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            AlertDialog logoutDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.logout_confirmation)
                    .setPositiveButton(R.string.logout, (dialog, which) -> AuthUtil.logout(UserProfileActivity.this))
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create();

            logoutDialog.show();
        });

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        try {
            loadUser(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadUser(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setOnRefreshListener(null);

        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    private void loadUser(boolean loadFromNetwork) throws JSONException {

        user = RealmDataRepository.getDefaultInstance().getUser();
        user.addChangeListener(userModel -> {
            showUserInfo(user);
            Timber.d("User data loaded from disk");
        });

        if (loadFromNetwork && NetworkUtils.haveNetworkConnection(UserProfileActivity.this)) {
            //get latest user data from network
            loadFromNetwork();
            Timber.d("Getting user data from network");
        }
    }

    private void loadFromNetwork() throws JSONException {
        disposable = APIClient.getOpenEventAPI()
                .getUser(JWTUtils.getIdentity(AuthUtil.getAuthorization()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                            RealmDataRepository
                                    .getDefaultInstance()
                                    .saveUser(user)
                                    .subscribe();
                            this.user = user;
                            showUserInfo(user);
                            Timber.d("User data saved in database");
                        },
                        throwable -> {
                            Timber.d(throwable.getMessage() + " Error getting data from network");
                            stopRefreshing();
                        },
                        () -> {
                            stopRefreshing();
                            Timber.d("User data loaded from network");
                        });
    }

    private void showUserInfo(User user) {
        if (user == null || !user.isValid())
            return;

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        String avatarUrl = user.getAvatarUrl();

        if (firstName != null)
            this.firstName.setText(firstName.trim());
        if (lastName != null)
            this.lastName.setText(lastName.trim());

        if (avatarUrl != null) {
            OpenEventApp.picassoWithCache.load(avatarUrl)
                    .transform(new CircleTransform())
                    .into(avatar);
        }
        this.email.setText(email);
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(UserProfileActivity.this), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                try {
                    loadFromNetwork();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void networkUnavailable() {
                stopRefreshing();
                Snackbar.make(coordinatorLayout, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        });
    }

    private void stopRefreshing() {
        Views.setSwipeRefreshLayout(swipeRefreshLayout, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                //do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
