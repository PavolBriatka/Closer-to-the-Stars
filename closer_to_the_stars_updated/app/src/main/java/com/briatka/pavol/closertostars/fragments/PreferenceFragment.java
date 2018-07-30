package com.briatka.pavol.closertostars.fragments;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.briatka.pavol.closertostars.R;

public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_closer_to_the_stars);

        Preference sizePreference = findPreference(getString(R.string.pref_size_key));
        sizePreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        Toast warning = Toast.makeText(getContext(), getString(R.string.size_warning), Toast.LENGTH_LONG);
        Toast error = Toast.makeText(getContext(), getString(R.string.size_error), Toast.LENGTH_LONG);
        Toast formatError = Toast.makeText(getContext(), getString(R.string.size_format_error), Toast.LENGTH_LONG);

        String sizeKey = getString(R.string.pref_size_key);
        if (preference.getKey().equals(sizeKey)) {
            String stringValue = (String) newValue;
            try {
                int sizeValue = Integer.parseInt(stringValue);
                if (sizeValue > 20 && sizeValue <= 50) {
                    warning.show();
                    return true;
                }
                if (sizeValue > 50 || sizeValue < 1) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException exception) {
                formatError.show();
                return false;
            }

        }
        return true;
    }
}
