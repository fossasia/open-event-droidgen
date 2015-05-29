package org.fossasia.openevent.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import org.fossasia.openevent.R;

/**
 * Created by manan on 21-05-2015.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private static final String PREF_MODE = "serverurlmode";
    String pref;
    ListPreference listPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        listPreference = (ListPreference) preferenceScreen.findPreference(PREF_MODE);
        listPreference.setDefaultValue(getResources().getString(R.string.default_mode));
        listPreference.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        return false;
    }
}
