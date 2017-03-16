package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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

import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by Manan Wason on 16/06/16.
 */
public class ScheduleFragment extends BaseFragment {

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout scheduleTabLayout;

    private CompositeDisposable compositeDisposable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        OpenEventApp.getEventBus().register(true);
        compositeDisposable = new CompositeDisposable();

        setupViewPager(viewPager);
        scheduleTabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_schedule;
    }

    private void setupViewPager(ViewPager viewPager) {
        final ScheduleViewPagerAdapter adapter = new ScheduleViewPagerAdapter(getChildFragmentManager());
        DbSingleton dbSingleton = DbSingleton.getInstance();

        compositeDisposable.add(dbSingleton.getDateListObservable()
                .map(new Function<List<String>, Integer>() {
                    @Override
                    public Integer apply(@NonNull List<String> event_days) throws Exception {
                        return event_days.size();
                    }
                }).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer daysOfEvent) throws Exception {
                        for (int i = 0; i < daysOfEvent; i++) {
                            adapter.addFragment(new DayScheduleFragment(), Days.values()[i].toString(), i);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OpenEventApp.getEventBus().unregister(this);
        if(compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }
}

