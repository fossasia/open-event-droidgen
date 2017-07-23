package org.fossasia.openevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.utils.DateConverter;
import org.fossasia.openevent.utils.SharedPreferencesUtil;

/**
 * User: manan
 * Date: 21-05-2015
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    public static final String NOTIFICATION_PREF_MODE = "notification";
    public static final String LANGUAGE_PREF_MODE = "change_language";
    private SwitchPreference internetPreference;
    private SwitchPreference timezonePreference;
    private Preference prefNotification;
    private Preference languagePreference;
    private AppCompatDelegate mDelegate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_right, R.anim.stay_in_place);

        PreferenceManager.setDefaultValues(this,R.xml.settings,false);
        addPreferencesFromResource(R.xml.settings);
        setContentView(R.layout.activity_settings);
        setToolbar();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        internetPreference = (SwitchPreference) preferenceScreen.findPreference(getResources().getString(R.string.download_mode_key));
        internetPreference.setOnPreferenceChangeListener(this);
        timezonePreference = (SwitchPreference) preferenceScreen.findPreference(getResources().getString(R.string.timezone_mode_key));
        timezonePreference.setOnPreferenceChangeListener(this);

        prefNotification = findPreference(NOTIFICATION_PREF_MODE);
        languagePreference=findPreference(LANGUAGE_PREF_MODE);
        languagePreference.setSummary(OpenEventApp.sDefSystemLanguage);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object choice) {

        if (preference.getKey().equals(getResources().getString(R.string.download_mode_key))) {
            if (choice.equals(false)) {
                internetPreference.setChecked(false);
            } else {
                internetPreference.setChecked(true);
            }
        } else if (preference.getKey().equals(getResources().getString(R.string.timezone_mode_key))) {
            boolean showLocalTimezone = choice.equals(true);

            timezonePreference.setChecked(showLocalTimezone);
            DateConverter.setShowLocalTimeZone(showLocalTimezone);
        } else if (preference.getKey().equals(getResources().getString(R.string.notification_key))) {
            prefNotification.setSummary((String) choice);
        } else if (preference.getKey().equals(getResources().getString(R.string.language_key))) {
            languagePreference.setSummary((String) choice);
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
        prefNotification.setOnPreferenceChangeListener((preference, newValue) -> {
            prefNotification.setSummary((String) newValue);
            return true;
        });

        languagePreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent((Settings.ACTION_LOCALE_SETTINGS)));
            return true;
        });


        prefNotification.setSummary(SharedPreferencesUtil.getString(getString(R.string.notification_key), ""));
        languagePreference.setSummary(OpenEventApp.sDefSystemLanguage);
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

    @Override
    public void finish() {
        super.finish();
        SettingsActivity.this.overridePendingTransition(0,R.anim.slide_out_right);

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
