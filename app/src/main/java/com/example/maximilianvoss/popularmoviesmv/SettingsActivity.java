package com.example.maximilianvoss.popularmoviesmv;

/**
 * Created by ruedigervoss on 19/03/16.
 */
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    private void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {

            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            if (index >= 0) {
                preference.setSummary(listPreference.getEntries()[index]);
            }

        }  else {

            preference.setSummary(stringValue);
        }
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Adding preference from xml
        addPreferencesFromResource(R.xml.pref_general);
        //binding pref summary to value
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_order_key)));
    }
}

