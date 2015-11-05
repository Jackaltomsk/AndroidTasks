package projects.my.stopwatch.common;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import projects.my.stopwatch.R;

/**
 * Содержит вспомогательные методы для активити.
 */
public class ActivityUtils {

    /**
     * Реализует инициализацию тулбара.
     * @param activity Текущая активити.
     * @param setUpNavigation Флаг установки навигации в тулбаре.
     */
    public static void setToolbar(AppCompatActivity activity, boolean setUpNavigation) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (setUpNavigation) {
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
