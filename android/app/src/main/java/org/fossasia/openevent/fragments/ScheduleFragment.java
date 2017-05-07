package org.fossasia.openevent.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.ScheduleViewPagerAdapter;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Manan Wason on 16/06/16.
 */
public class ScheduleFragment extends BaseFragment {

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout scheduleTabLayout;
    @BindView(R.id.schedule_fab) FloatingActionButton fabOpenClose;
    @BindView(R.id.schedule_fab_filter) FloatingActionButton fabFilter;
    @BindView(R.id.schedule_fab_sort) FloatingActionButton fabSort;
    @BindView(R.id.filter_text) TextView filtersText;
    @BindView(R.id.close_filter) ImageView closeFilterBarButton;
    @BindView(R.id.filter_bar) LinearLayout filterBar;

    private CompositeDisposable compositeDisposable;
    private boolean fabmenuVisible = false;
    private int sortType;
    private SharedPreferences sharedPreferences;
    private ScheduleViewPagerAdapter adapter;
    private List<Track> mTracks = new ArrayList<>();
    private String tracksNames[];
    private boolean isTrackSelected[];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        filterBar.setVisibility(View.GONE);
        OpenEventApp.getEventBus().register(true);
        compositeDisposable = new CompositeDisposable();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortType = sharedPreferences.getInt(ConstantStrings.PREF_SORT, 0);

        setupViewPager(viewPager);
        scheduleTabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_schedule;
    }

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ScheduleViewPagerAdapter(getChildFragmentManager());
        DbSingleton dbSingleton = DbSingleton.getInstance();

        compositeDisposable.add(dbSingleton.getDateListObservable()
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> eventDayList) throws Exception {
                        int eventDays = eventDayList.size();
                        for(int i = 0; i < eventDays; i++) {
                            String date = eventDayList.get(i);

                            adapter.addFragment(new DayScheduleFragment(),
                                    ISO8601Date.getTimeZoneDateFromString(date), date);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }));

        compositeDisposable.add(dbSingleton.getTrackListObservable()
                .subscribe(new Consumer<List<Track>>() {
                    @Override
                    public void accept(@NonNull List<Track> tracks) throws Exception {
                        mTracks.clear();
                        mTracks.addAll(tracks);
                        tracksNames = new String[mTracks.size()];
                        isTrackSelected = new boolean[mTracks.size()];

                        for(int i = 0 ; i<mTracks.size() ; i++ ){
                            tracksNames[i] = mTracks.get(i).getName();
                        }

                        viewPagerScroll();
                    }
                }));

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Called when the current page is scrolled
            }

            @Override
            public void onPageSelected(int position) {
                adapter.getItem(position).onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Called when the scroll state changes
            }
        });
    }

    public void viewPagerScroll() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //intentional
            }

            @Override
            public void onPageSelected(int position) {
                ScheduleViewPagerAdapter scheduleViewPagerAdapter = (ScheduleViewPagerAdapter) viewPager.getAdapter();
                DayScheduleFragment dayScheduleFragment = (DayScheduleFragment)scheduleViewPagerAdapter.getItem(position);
                dayScheduleFragment.refreshSchedule();
                dayScheduleFragment.filter(tracksNames,isTrackSelected);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            //intentional
            }
        });
    }

    @OnClick (R.id.schedule_fab)
    public void showMenu() {
        if(!fabmenuVisible) {
            fabFilter.setVisibility(View.VISIBLE);
            fabSort.setVisibility(View.VISIBLE);
            fabOpenClose.setImageResource(R.drawable.ic_close_24dp);
            fabmenuVisible = true;
        } else {
            fabFilter.setVisibility(View.GONE);
            fabSort.setVisibility(View.GONE);
            fabOpenClose.setImageResource(R.drawable.ic_plus_24dp);
            fabmenuVisible = false;
        }
    }

    @OnClick (R.id.schedule_fab_sort)
    public void sortSchedule() {
        final AlertDialog.Builder dialogSort = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_sort_title)
                .setSingleChoiceItems(R.array.session_sort, sortType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sortType = which;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(ConstantStrings.PREF_SORT, which);
                        editor.apply();
                        ScheduleViewPagerAdapter scheduleViewPagerAdapter = (ScheduleViewPagerAdapter) viewPager.getAdapter();
                        DayScheduleFragment dayScheduleFragment = (DayScheduleFragment)scheduleViewPagerAdapter.getItem(viewPager.getCurrentItem());
                        dayScheduleFragment.refreshSchedule();
                        dialog.dismiss();
                    }
                });

        dialogSort.show();
    }

    @OnClick (R.id.schedule_fab_filter)
    public void filterSchedule() {
        final AlertDialog.Builder dialogSort = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_filter_title)
                .setMultiChoiceItems(tracksNames, isTrackSelected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isSelected) {
                        isTrackSelected[which] = isSelected;
                        ScheduleViewPagerAdapter scheduleViewPagerAdapter = (ScheduleViewPagerAdapter) viewPager.getAdapter();
                        DayScheduleFragment dayScheduleFragment = (DayScheduleFragment)scheduleViewPagerAdapter.getItem(viewPager.getCurrentItem());
                        dayScheduleFragment.filter(tracksNames,isTrackSelected);
                        int count = 0;
                        String tracksFiltered = "";
                        for(int i=0 ; i<isTrackSelected.length ; i++) {
                            if (isTrackSelected[i]) {
                                if(count == 0)
                                    tracksFiltered += tracksNames[i];
                                else
                                    tracksFiltered += ("," + tracksNames[i]);
                                count++;
                            }
                        }
                        if(count!=0) {
                            filtersText.setText("Filters" + "(" + count +")" + ": " + tracksFiltered);
                            filterBar.setVisibility(View.VISIBLE);
                        } else {
                            filterBar.setVisibility(View.GONE);
                        }
                    }
                })
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        dialogSort.show();
    }


    @OnClick (R.id.close_filter)
    public void closeFilterBar() {
        Arrays.fill(isTrackSelected, false);
        ScheduleViewPagerAdapter scheduleViewPagerAdapter = (ScheduleViewPagerAdapter) viewPager.getAdapter();
        DayScheduleFragment dayScheduleFragment = (DayScheduleFragment)scheduleViewPagerAdapter.getItem(viewPager.getCurrentItem());
        dayScheduleFragment.filter(tracksNames,isTrackSelected);
        filterBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OpenEventApp.getEventBus().unregister(this);
        if(compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }
}

