package projects.my.stopwatch.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;

import projects.my.stopwatch.R;
import projects.my.stopwatch.activities.ColorActivity;
import projects.my.timerdb.dao.GenericDao;
import projects.my.timerdb.dao.extensions.PropertiesExtension;
import projects.my.timerdb.infrastructure.DbManager;
import projects.my.timerdb.models.Properties;

/**
 * Фрагмент настроек.
 */
public class PreferencesFragment extends PreferenceFragment {

    private static final String TAG = PreferencesFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String colorPrefKey = getResources().getString(R.string.pref_key_select_color);
        if (preference.getKey().equals(colorPrefKey)) {
            Intent intent = new Intent(getActivity(), ColorActivity.class);
            startActivityForResult(intent, ColorActivity.REQUEST_COLOR_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ColorActivity.REQUEST_COLOR_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int colorId = data.getIntExtra(ColorActivity.COLOR, android.R.color.white);
                GenericDao<Properties> dao = DbManager.getDbContext()
                        .getGenericDao(Properties.class);
                PropertiesExtension ext = new PropertiesExtension(dao);
                ext.setNewColor(colorId);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast tst = Toast.makeText(getActivity(), "Request cancelled", Toast.LENGTH_SHORT);
                tst.show();
            }
        }
        else super.onActivityResult(requestCode, resultCode, data);
    }
}