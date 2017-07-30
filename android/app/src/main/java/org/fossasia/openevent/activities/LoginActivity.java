package org.fossasia.openevent.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.utils.AuthUtil;
import org.fossasia.openevent.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements AppCompatEditText.OnEditorActionListener {

    @BindView(R.id.text_input_layout_email)
    TextInputLayout mTextInputLayoutEmail;
    @BindView(R.id.text_input_layout_password)
    TextInputLayout mTextInputLayoutPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.sign_up)
    TextView switchToSignUp;

    private AppCompatEditText mEditTextEmail;
    private AppCompatEditText mEditTextPassword;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(R.anim.slide_in_right, R.anim.stay_in_place);

        ButterKnife.bind(this);

        mEditTextEmail = (AppCompatEditText) mTextInputLayoutEmail.getEditText();
        mEditTextPassword = (AppCompatEditText) mTextInputLayoutPassword.getEditText();

        setEditTextListener();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchToSignUp.setOnClickListener(v -> onBackPressed());

        btnLogin.setOnClickListener(v -> {
            hideKeyBoard();
            if (mEditTextEmail == null || mEditTextPassword == null)
                return;

            email = mEditTextEmail.getText().toString();
            String password = mEditTextPassword.getText().toString();

            if (validateCredentials(email, password)) {
                AuthUtil.loginUser(LoginActivity.this, email, password, progressBar);
            }
        });
    }

    private void setEditTextListener() {
        if (mEditTextEmail == null || mEditTextPassword == null)
            return;

        mEditTextEmail.setOnEditorActionListener(this);
        mEditTextPassword.setOnEditorActionListener(this);
    }

    private boolean validateCredentials(String email, String password) {

        // Reset errors.
        mTextInputLayoutEmail.setError(null);
        mTextInputLayoutPassword.setError(null);

        if (Utils.isEmpty(email)) {
            handleError(mTextInputLayoutEmail, R.string.error_email_required);
            return false;
        } else if (!Utils.isEmailValid(email)) {
            handleError(mTextInputLayoutEmail, R.string.error_enter_valid_email);
            return false;
        }

        if (Utils.isEmpty(password)) {
            handleError(mTextInputLayoutPassword, R.string.error_password_required);
            return false;
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
            hideKeyBoard();
            btnLogin.performClick();
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

