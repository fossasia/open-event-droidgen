package org.fossasia.openevent.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.fossasia.openevent.R;

/**
 * User: manan
 * Date: 21-05-2015
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    public static final String NOTIFICATION_PREF_MODE = "notification";
    private SwitchPreference internetPreference;
    private SwitchPreference timezonePreference;
    private Preference prefNotification;
    private SharedPreferences preferences;
    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        addPreferencesFromResource(R.xml.settings);
        setContentView(R.layout.activity_settings);
        setToolbar();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        internetPreference = (SwitchPreference) preferenceScreen.findPreference(getResources().getString(R.string.download_mode_key));
        internetPreference.setOnPreferenceChangeListener(this);
        timezonePreference = (SwitchPreference) preferenceScreen.findPreference(getResources().getString(R.string.timezone_mode_key));
        timezonePreference.setOnPreferenceChangeListener(this);

        prefNotification = findPreference(NOTIFICATION_PREF_MODE);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        if (preference.getKey().equals(getResources().getString(R.string.download_mode_key))) {
            if (o.equals(false)) {
                internetPreference.setChecked(false);
            } else if (o.equals(true)) {
                internetPreference.setChecked(true);
            }
        } else if (preference.getKey().equals(getResources().getString(R.string.timezone_mode_key))) {
            if (o.equals(false)) {
                timezonePreference.setChecked(false);
            } else if (o.equals(true)) {
                timezonePreference.setChecked(true);
            }
        }
        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void onResume() {
        super.onResume();
        prefNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefNotification.setSummary((String) newValue);
                return true;
            }
        });

        prefNotification.setSummary(preferences.getString(getString(R.string.notification_key), ""));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void setToolbar() {
        setContentView(R.layout.activity_settings);
        setSupportActionBar((Toolbar) findViewById(R.id.setting_toolbar));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
    }

    private ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
