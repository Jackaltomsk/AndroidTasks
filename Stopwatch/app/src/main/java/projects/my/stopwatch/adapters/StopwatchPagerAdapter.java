package projects.my.stopwatch.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

import projects.my.stopwatch.fragments.FragmentTimeManager;

/**
 * Адаптер страниц.
 */
public class StopwatchPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public StopwatchPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((FragmentTimeManager) fragments.get(position)).getTitle();
    }
}
