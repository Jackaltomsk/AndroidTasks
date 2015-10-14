package projects.my.stopwatch.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import projects.my.stopwatch.fragments.CountDownFragment;
import projects.my.stopwatch.fragments.FragmentTimeManager;
import projects.my.stopwatch.fragments.TimeFragment;

/**
 * Адаптер страниц.
 */
public class StopwatchPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments;

    public StopwatchPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new Fragment[2];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (fragments[position] == null) {
                    Fragment frg = TimeFragment.newInstance();
                    fragments[position] = frg;
                }
                return fragments[position];
            case 1:
                if (fragments[position] == null) {
                    Fragment frg = CountDownFragment.newInstance();
                    fragments[position] = frg;
                }
                return fragments[position];
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((FragmentTimeManager) getItem(position)).getTitle();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (fragments[position] == null) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragments[position] = fragment;
            return fragment;
        }
        else return fragments[position];
    }

    public Fragment[] getFragments() {
        return new Fragment[] { fragments[0], fragments[1] };
    }
}
