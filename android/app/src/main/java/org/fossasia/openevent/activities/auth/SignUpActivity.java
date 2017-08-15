package org.fossasia.openevent.activities.auth;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.utils.AuthUtil;
import org.fossasia.openevent.utils.Utils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        emailInput = (TextInputEditText) emailWrapper.getEditText();
        createPasswordInput = (TextInputEditText) createPasswordWrapper.getEditText();
        confirmPasswordInput = (TextInputEditText) confirmPasswordWrapper.getEditText();

        setEditTextListener();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchToLogin.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));

        signUp.setOnClickListener(v -> {
            hideKeyBoard();
            if (emailInput == null || createPasswordInput == null || confirmPasswordInput == null)
                return;

            email = emailInput.getText().toString();
            password = createPasswordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (validateCredentials(email, password, confirmPassword)) {
                AuthUtil.signUpUser(SignUpActivity.this, email, password, progressBar);
            }
        });
    }

    private void setEditTextListener() {
        if (emailInput == null || createPasswordInput == null || confirmPasswordInput == null)
            return;

        emailInput.setOnEditorActionListener(this);
        createPasswordInput.setOnEditorActionListener(this);
        confirmPasswordInput.setOnEditorActionListener(this);
    }

    private boolean validateCredentials(String email, String password, String confirmPasssword) {

        // Reset errors.
        emailWrapper.setError(null);
        createPasswordWrapper.setError(null);
        confirmPasswordWrapper.setError(null);

        if (Utils.isEmpty(email)) {
            handleError(emailWrapper, R.string.error_email_required);
            return false;
        } else if (!Utils.isEmailValid(email)) {
            handleError(emailWrapper, R.string.error_enter_valid_email);
            return false;
        }

        if (Utils.isEmpty(password)) {
            handleError(createPasswordWrapper, R.string.error_password_required);
            return false;
        } else if (!Utils.isPasswordValid(password)) {
            handleError(createPasswordWrapper, R.string.error_password_length);
            return false;
        }

        if (Utils.isEmpty(confirmPasssword)) {
            handleError(confirmPasswordWrapper, R.string.error_confirm_password);
            return false;
        }

        if (!confirmPasssword.equals(password)) {
            handleError(confirmPasswordWrapper, R.string.error_password_not_matching);
            return false;
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
            hideKeyBoard();
            signUp.performClick();
            return true;
        }
        return false;
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
