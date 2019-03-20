package com.example.android.moviematch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.prefs);
        EditTextPreference votepref = (EditTextPreference) findPreference(getString(R.string.pref_vote_count_key));
        EditTextPreference ratingpref = (EditTextPreference) findPreference(getString(R.string.pref_vote_count_key));
        EditTextPreference releasepref = (EditTextPreference) findPreference(getString(R.string.pref_vote_count_key));
        votepref.setSummary(votepref.getText());
        ratingpref.setSummary(ratingpref.getText());
        releasepref.setSummary(releasepref.getText());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_vote_count_key))) {
            EditTextPreference pref = (EditTextPreference) findPreference(s);
            pref.setSummary(pref.getText());
        }
        if (s.equals(getString(R.string.pref_vote_avg_key))) {
            EditTextPreference pref = (EditTextPreference) findPreference(s);
            pref.setSummary(pref.getText());
        }
        if (s.equals(getString(R.string.pref_release_year_key))) {
            EditTextPreference pref = (EditTextPreference) findPreference(s);
            pref.setSummary(pref.getText());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

    }
}
