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
    private final static int FRAGMENTS_COUNT = 2;

    public StopwatchPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new Fragment[FRAGMENTS_COUNT];
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
        // Отдаем новый массив, чтобы нельзя было изменить внутренний.
        Fragment[] newArray = new Fragment[FRAGMENTS_COUNT];
        for (int i = 0; i < FRAGMENTS_COUNT; i++) {
            newArray[i] = fragments[i];
        }
        return newArray;
    }
}