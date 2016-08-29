package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.ScheduleViewPagerAdapter;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.Days;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Manan Wason on 16/06/16.
 */
public class ScheduleFragment extends Fragment {
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.tabLayout)
    TabLayout scheduleTabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        OpenEventApp.getEventBus().register(true);
        setupViewPager(viewPager);
        scheduleTabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void setupViewPager(ViewPager viewPager) {
        ScheduleViewPagerAdapter adapter = new ScheduleViewPagerAdapter(getChildFragmentManager());
        DbSingleton dbSingleton = DbSingleton.getInstance();

        List<String> event_days = dbSingleton.getDateList();
        int daysofEvent = event_days.size();

        for (int i = 0; i < daysofEvent; i++) {
            adapter.addFragment(new DayScheduleFragment(), Days.values()[i].toString(), i);
        }
        viewPager.setAdapter(adapter);
    }


}

