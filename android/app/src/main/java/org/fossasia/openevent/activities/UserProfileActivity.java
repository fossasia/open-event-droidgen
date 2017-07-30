package org.fossasia.openevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.utils.AuthUtil;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.email)
    TextView mTextViewEmail;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.logout)
    TextView mTextViewLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!Utils.isUserLoggedIn()) {
            Intent intent = new Intent(UserProfileActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }

        String email = SharedPreferencesUtil.getString(ConstantStrings.USER_EMAIL, null);
        if (email != null) {
            mTextViewEmail.setText(email);
        }

        mTextViewLogout.setOnClickListener(v -> {
            AlertDialog logoutDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.logout_confirmation)
                    .setPositiveButton(R.string.logout, (dialog, which) -> AuthUtil.logout(UserProfileActivity.this))
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create();

            logoutDialog.show();
        });
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
