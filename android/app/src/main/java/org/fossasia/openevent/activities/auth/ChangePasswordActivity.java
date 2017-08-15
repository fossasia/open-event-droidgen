package org.fossasia.openevent.activities.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.fossasia.openevent.R;
import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.auth.Login;
import org.fossasia.openevent.data.auth.User;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.AuthUtil;
import org.fossasia.openevent.utils.JWTUtils;
import org.fossasia.openevent.utils.Utils;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.text_input_layout_current_password)
    TextInputLayout currentPasswordWrapper;
    @BindView(R.id.text_input_layout_new_password)
    TextInputLayout newPasswordWrapper;
    @BindView(R.id.text_input_layout_confirm_password)
    TextInputLayout confirmPasswordWrapper;
    @BindView(R.id.btnChangePassword)
    Button changePassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    String currentPassword;
    String newPassword;

    private AppCompatEditText currentPasswordInput;
    private AppCompatEditText newPasswordInput;
    private AppCompatEditText confirmPasswordInput;

    private User user;
    private Disposable checkPasswordDisposable;
    private Disposable changePasswordDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = RealmDataRepository.getDefaultInstance().getUser();
        user.addChangeListener(userModel -> {
        });

        currentPasswordInput = (AppCompatEditText) currentPasswordWrapper.getEditText();
        newPasswordInput = (AppCompatEditText) newPasswordWrapper.getEditText();
        confirmPasswordInput = (AppCompatEditText) confirmPasswordWrapper.getEditText();

        changePassword.setOnClickListener(v -> {
            hideKeyBoard();
            saveChanges();
        });
    }

    private void saveChanges() {
        if (currentPasswordInput == null || newPasswordInput == null || confirmPasswordInput == null)
            return;

        currentPassword = currentPasswordInput.getText().toString();
        newPassword = newPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (validateCredentials(currentPassword, newPassword, confirmPassword)) {
            checkCurrentPassword(currentPassword);
        }
    }

    private void checkCurrentPassword(String currentPassword) {
        if (user == null)
            return;

        checkPasswordDisposable = APIClient.getOpenEventAPI().login(new Login(user.getEmail(), currentPassword))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                            Timber.d("Received token. Password is correct");
                        },
                        throwable -> {
                            showProgressBar(false);
                            Toast.makeText(ChangePasswordActivity.this, R.string.error_password_not_correct, Toast.LENGTH_SHORT).show();
                            Timber.d("Error changing password: " + throwable.getMessage());
                        },
                        () -> {
                            showProgressBar(false);
                            //Old password matched change
                            changePassword();
                        },
                        disposable -> showProgressBar(true));
    }

    private void changePassword() {
        if (newPassword == null)
            return;

        int id = 0;
        try {
            id = JWTUtils.getIdentity(AuthUtil.getAuthorization());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        changePasswordDisposable = APIClient.getOpenEventAPI().updateUser(User.builder().id(id).password(newPassword).build(), id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                            RealmDataRepository
                                    .getDefaultInstance()
                                    .saveUser(user)
                                    .subscribe();
                            this.user = user;
                            showProgressBar(false);
                            Timber.d("User data saved in database");
                        },
                        throwable -> {
                            showProgressBar(false);
                            Toast.makeText(ChangePasswordActivity.this, R.string.error_changing_password, Toast.LENGTH_SHORT).show();
                            Timber.d("Error changing password" + throwable.getMessage());
                        },
                        () -> {
                            showProgressBar(false);
                            Toast.makeText(ChangePasswordActivity.this, R.string.password_changed_successfully, Toast.LENGTH_SHORT).show();
                            Timber.d("Password changed successfully");
                            finish();
                        },
                        disposable -> showProgressBar(true));
    }

    private boolean validateCredentials(String currentPassword, String newPassword, String confirmPasssword) {

        // Reset errors.
        currentPasswordWrapper.setError(null);
        newPasswordWrapper.setError(null);
        confirmPasswordWrapper.setError(null);

        if (Utils.isEmpty(currentPassword)) {
            handleError(currentPasswordWrapper, R.string.error_password_required);
            return false;
        }

        if (Utils.isEmpty(newPassword)) {
            handleError(newPasswordWrapper, R.string.error_password_required);
            return false;
        } else if (!Utils.isPasswordValid(newPassword)) {
            handleError(newPasswordWrapper, R.string.error_password_length);
            return false;
        }

        if (Utils.isEmpty(confirmPasssword)) {
            handleError(confirmPasswordWrapper, R.string.error_confirm_password);
            return false;
        }

        if (!confirmPasssword.equals(newPassword)) {
            handleError(confirmPasswordWrapper, R.string.error_password_not_matching);
            return false;
        }

        return true;
    }

    private void handleError(TextInputLayout textInputLayout, @StringRes int id) {
        textInputLayout.setError(getString(id));
        textInputLayout.requestFocus();
    }

    private void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (checkPasswordDisposable != null && !checkPasswordDisposable.isDisposed())
            checkPasswordDisposable.dispose();
        if (changePasswordDisposable != null && !changePasswordDisposable.isDisposed())
            changePasswordDisposable.dispose();
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
