package projects.my.stopwatch.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import projects.my.stopwatch.R;
import projects.my.stopwatch.common.ActivityUtils;
import projects.my.stopwatch.fragments.AppPreferencesFragment;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        ActivityUtils.setToolbar(this, true);
        getFragmentManager().beginTransaction().replace(R.id.pref_content,
                new AppPreferencesFragment()).commit();
    }
}