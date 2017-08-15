package org.fossasia.openevent.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.fossasia.openevent.utils.ConstantStrings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manan Wason on 16/06/16.
 */
public class ScheduleViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> fragmentTitleList = new ArrayList<>();


    public ScheduleViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(final Fragment fragment, final String title, String dayArgument) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantStrings.EVENT_DAY, dayArgument);
        fragment.setArguments(bundle);

        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
