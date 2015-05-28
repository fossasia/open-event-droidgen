package org.fossasia.openevent.activities;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import org.fossasia.openevent.R;
import org.fossasia.openevent.fragments.SettingsFragment;

/**
 * Created by manan on 21-05-2015.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    String pref;
    Context mContext;
    ListPreference listPreference;
    private static final String PREF_MODE = "mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
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
