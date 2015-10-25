package projects.my.stopwatch.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import projects.my.stopwatch.R;

/**
 * Фрагмент настроек.
 */
public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
