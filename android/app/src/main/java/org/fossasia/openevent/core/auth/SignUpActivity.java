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

public class SignUpActivity extends AppCompatActivity implements AppCompatEditText.OnEditorActionListener {

    @BindView(R.id.text_input_layout_email)
    TextInputLayout emailWrapper;
    @BindView(R.id.text_input_layout_create_password)
    TextInputLayout createPasswordWrapper;
    @BindView(R.id.text_input_layout_confirm_password)
    TextInputLayout confirmPasswordWrapper;
    @BindView(R.id.btnSignUp)
    Button signUp;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.login)
    LinearLayout switchToLogin;

    private TextInputEditText emailInput;
    private TextInputEditText createPasswordInput;
    private TextInputEditText confirmPasswordInput;

    private String email;
    private String password;

    private SignUpActivityViewModel signUpActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        emailInput = (TextInputEditText) emailWrapper.getEditText();
        createPasswordInput = (TextInputEditText) createPasswordWrapper.getEditText();
        confirmPasswordInput = (TextInputEditText) confirmPasswordWrapper.getEditText();

        signUpActivityViewModel = ViewModelProviders.of(this).get(SignUpActivityViewModel.class);

        setEditTextListener();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchToLogin.setOnClickListener(v -> onBackPressed());

        signUp.setOnClickListener(v -> {
            Views.hideKeyboard(this, this.getCurrentFocus());

            email = emailInput.getText().toString();
            password = createPasswordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            signUp(confirmPassword);
        });
    }

    private void signUp(String confirmPassword) {
        if (validateCredentials(email, password, confirmPassword)) {
            progressBar.setVisibility(View.VISIBLE);

            signUpActivityViewModel.signUpUser(email, password).observe(this, signUpResponse -> {
                switch (signUpResponse) {
                    case SignUpActivityViewModel.VALID:
                        //sign up successful
                        showMessage(R.string.signed_up_successfully);
                        loginAfterSignUp();
                        break;
                    default:
                        //sign up unsuccessful
                        showMessage(R.string.error_in_signing_up);
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                }
            });
        }
    }

    private void loginAfterSignUp() {
        signUpActivityViewModel.loginUserAfterSignUp(email, password).observe(this, loginResponse -> {
            progressBar.setVisibility(View.INVISIBLE);
            switch (loginResponse.getResponse()) {
                case SignUpActivityViewModel.VALID:
                    //Save token & email in shared preferences
                    SharedPreferencesUtil.putString(ConstantStrings.TOKEN, loginResponse.getToken());
                    SharedPreferencesUtil.putString(ConstantStrings.USER_EMAIL, email);

                    showMessage(R.string.logged_in_successfully);

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                default:
                    showMessage(R.string.error_authentication_failed);
                    break;
            }
        });
    }

    private void setEditTextListener() {
        emailInput.setOnEditorActionListener(this);
        createPasswordInput.setOnEditorActionListener(this);
        confirmPasswordInput.setOnEditorActionListener(this);
    }

    private boolean validateCredentials(String email, String password, String confirmPassword) {

        // Reset errors.
        emailWrapper.setError(null);
        createPasswordWrapper.setError(null);
        confirmPasswordWrapper.setError(null);

        switch (AuthUtil.validateEmail(email)) {
            case SignUpActivityViewModel.EMPTY:
                handleError(emailWrapper, R.string.error_email_required);
                return false;
            case SignUpActivityViewModel.INVALID:
                handleError(emailWrapper, R.string.error_enter_valid_email);
                return false;
            default:
                //No implementation
        }

        switch (AuthUtil.validatePassword(password)) {
            case SignUpActivityViewModel.EMPTY:
                handleError(createPasswordWrapper, R.string.error_password_required);
                return false;
            case SignUpActivityViewModel.INVALID:
                handleError(createPasswordWrapper, R.string.error_password_length);
                return false;
            default:
                //No implementation
        }

        switch (signUpActivityViewModel.validateConfirmPassword(confirmPassword, password)) {
            case SignUpActivityViewModel.EMPTY:
                handleError(confirmPasswordWrapper, R.string.error_confirm_password);
                return false;
            case SignUpActivityViewModel.INVALID:
                handleError(confirmPasswordWrapper, R.string.error_password_not_matching);
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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Views.hideKeyboard(this, this.getCurrentFocus());
            signUp.performClick();
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
