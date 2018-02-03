package org.fossasia.openevent.core.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.core.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fossasia.openevent.core.auth.AuthUtil.EMPTY;
import static org.fossasia.openevent.core.auth.AuthUtil.INVALID;
import static org.fossasia.openevent.core.auth.AuthUtil.VALID;

public class LoginActivity extends AppCompatActivity implements AppCompatEditText.OnEditorActionListener {

    @BindView(R.id.text_input_layout_email)
    TextInputLayout emailWrapper;
    @BindView(R.id.text_input_layout_password)
    TextInputLayout passwordWrapper;
    @BindView(R.id.btnLogin)
    Button login;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.sign_up)
    LinearLayout switchToSignUp;

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    private String email;

    private LoginActivityViewModel loginActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(R.anim.slide_in_right, R.anim.stay_in_place);

        ButterKnife.bind(this);

        emailInput = (TextInputEditText) emailWrapper.getEditText();
        passwordInput = (TextInputEditText) passwordWrapper.getEditText();

        setEditTextListener();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchToSignUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        loginActivityViewModel = ViewModelProviders.of(this).get(LoginActivityViewModel.class);

        login.setOnClickListener(v -> {
            Views.hideKeyboard(this, this.getCurrentFocus());

            progressBar.setVisibility(View.VISIBLE);
            email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (validateCredentials(email, password)) {
                loginUser(password);
            }
        });
    }

    private void loginUser(String password) {
        loginActivityViewModel.loginUser(email, password).observe(this, loginResponse -> {
            progressBar.setVisibility(View.INVISIBLE);
            switch (loginResponse.getResponse()) {
                case VALID:
                    //Save token & email in shared preferences
                    SharedPreferencesUtil.putString(ConstantStrings.TOKEN, loginResponse.getAccessToken());
                    SharedPreferencesUtil.putString(ConstantStrings.USER_EMAIL, email);

                    showMessage(R.string.logged_in_successfully);

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                case INVALID:
                    showMessage(R.string.error_authentication_failed);
                    break;
                default:
                    // No implementation
            }
        });
    }

    private void setEditTextListener() {
        emailInput.setOnEditorActionListener(this);
        passwordInput.setOnEditorActionListener(this);
    }

    private boolean validateCredentials(String email, String password) {

        // Reset errors.
        emailWrapper.setError(null);
        passwordWrapper.setError(null);

        switch (AuthUtil.validateEmail(email)) {
            case EMPTY:
                handleError(emailWrapper, R.string.error_email_required);
                return false;
            case INVALID:
                handleError(emailWrapper, R.string.error_enter_valid_email);
                return false;
            default:
                //No implementation
        }

        switch (AuthUtil.validatePassword(password)) {
            case EMPTY:
                handleError(passwordWrapper, R.string.error_password_required);
                return false;
            default:
                //No implementation
        }

        return true;
    }

    private void handleError(TextInputLayout textInputLayout, @StringRes int id) {
        textInputLayout.setError(getString(id));
        textInputLayout.requestFocus();
    }

    @Override
    public void finish() {
        super.finish();
        LoginActivity.this.overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Views.hideKeyboard(this, this.getCurrentFocus());
            login.performClick();
            return true;
        }
        return false;
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

    private static void showMessage(@StringRes int id) {
        Toast.makeText(OpenEventApp.getAppContext(), id, Toast.LENGTH_SHORT).show();
    }
}

