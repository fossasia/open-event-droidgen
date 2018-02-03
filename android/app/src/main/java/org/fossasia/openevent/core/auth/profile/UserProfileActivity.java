package org.fossasia.openevent.core.auth.profile;

import android.arch.lifecycle.ViewModelProviders;
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

import org.fossasia.openevent.R;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.auth.ChangePasswordActivity;
import org.fossasia.openevent.core.auth.LoginActivity;
import org.fossasia.openevent.core.auth.model.User;
import org.fossasia.openevent.core.auth.AuthUtil;
import org.fossasia.openevent.common.ui.image.CircleTransform;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.Views;
import org.json.JSONException;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private UserProfileActivityViewModel userProfileActivityViewModel;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userProfileActivityViewModel = ViewModelProviders.of(this).get(UserProfileActivityViewModel.class);
        if (!AuthUtil.isUserLoggedIn()) {
            //Delete User data from realm
            userProfileActivityViewModel.eraseUserData();

            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
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
    }

    private void subscribeUser() {
        userProfileActivityViewModel.getUser().observe(this, user -> {
            this.user = user;
            showUserInfo(user);
        });

    }

    private void loadUser(boolean loadFromNetwork) throws JSONException {

       subscribeUser();

        if (loadFromNetwork && NetworkUtils.haveNetworkConnection(UserProfileActivity.this)) {
            //get latest user data from network
            loadFromNetwork();
            Timber.d("Getting user data from network");
        }
    }

    private void loadFromNetwork() throws JSONException {
        userProfileActivityViewModel.loadUserFromNetwork(AuthUtil.getAuthorization())
                .observe(this,response -> {
                    switch (response) {
                        case UserProfileActivityViewModel.RETRIEVE_SUCCESSFUL:
                            subscribeUser();
                            stopRefreshing();
                            break;
                        case UserProfileActivityViewModel.RETRIEVE_UNSUCCESSFUL:
                            stopRefreshing();
                            break;
                        default:
                            //Not implementation
                    }
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
            StrategyRegistry.getInstance()
                    .getHttpStrategy()
                    .getPicassoWithCache()
                    .load(avatarUrl)
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
