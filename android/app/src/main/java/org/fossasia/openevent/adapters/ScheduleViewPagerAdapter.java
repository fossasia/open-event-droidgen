package org.fossasia.openevent.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manan Wason on 16/06/16.
 */
public class ScheduleViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();

    private List<String> event_days = DbSingleton.getInstance().getDateList();


    public ScheduleViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title, int day) {
        event_days = DbSingleton.getInstance().getDateList();
        Bundle bundle = new Bundle();
        String dayString = event_days.get(day);
        bundle.putString(ConstantStrings.EVENT_DAY, dayString);
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
