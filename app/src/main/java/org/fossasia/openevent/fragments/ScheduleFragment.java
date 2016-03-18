package org.fossasia.openevent.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.ScheduleSessionsListAdapter;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.views.TitlePickerView;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by duncanleo on 4/2/16.
 */
public class ScheduleFragment extends Fragment implements SearchView.OnQueryTextListener {
    private final String SEARCH = "searchText";
    private String searchText = "";
    private SearchView searchView;
    private boolean showBookmarkedOnly;

    public static final String SCHEDULE_TAB_POSITION = "SCHEDULE_TAB_POSITION", SHOW_BOOKMARKED_ONLY = "SHOW_BOOKMARKED_ONLY";
    private TitlePickerView scheduleTitlePicker;
    private ViewPager scheduleViewPager;
    private ScheduleViewPagerAdapter viewPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        OpenEventApp.getEventBus().register(this);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
        if (getArguments() != null) {
            showBookmarkedOnly = getArguments().getBoolean(SHOW_BOOKMARKED_ONLY, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);
        scheduleTitlePicker = (TitlePickerView) v.findViewById(R.id.schedule_title_picker);
        scheduleViewPager = (ViewPager) v.findViewById(R.id.schedule_view_pager);
        viewPagerAdapter = new ScheduleViewPagerAdapter(getChildFragmentManager());
        scheduleViewPager.setAdapter(viewPagerAdapter);
        scheduleTitlePicker.setupWithViewPager(scheduleViewPager);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (isAdded()) {
            if (searchView != null) {
                bundle.putString(SEARCH, searchText);
            }
        }
        super.onSaveInstanceState(bundle);
    }

    private class ScheduleViewPagerAdapter extends FragmentPagerAdapter {
        private String[] titles;
        private SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public ScheduleViewPagerAdapter(FragmentManager fm) {
            super(fm);
            titles = getResources().getStringArray(R.array.days);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new SessionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(SCHEDULE_TAB_POSITION, position);
            bundle.putBoolean(SHOW_BOOKMARKED_ONLY, showBookmarkedOnly);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_schedule:
                final List<Integer> selectedTracks = new ArrayList<>();
                final List<Track> trackList = DbSingleton.getInstance().getTrackList();
                String[] trackNames = new String[trackList.size()];
                for (int i = 0; i < trackList.size(); i++) {
                    trackNames[i] = trackList.get(i).getName();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.filter_sessions)
                        .setMultiChoiceItems(trackNames, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedTracks.add(trackList.get(which).getId());
                                } else {
                                    selectedTracks.remove(Integer.valueOf(trackList.get(which).getId()));
                                }
                            }
                        })
                        .setNeutralButton(R.string.select_none, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: Filter the results based on the tracks
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                final AlertDialog dialog = builder.create();

                //Set the neutral button
                //Using this complicated method as neutral button will by default dismiss dialog
                //Neutral button here is the toggle all button
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    ListView listView;
                    Button neutral;

                    @Override
                    public void onShow(DialogInterface d) {
                        listView = dialog.getListView();
                        neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                        setCheckedAll(true);
                        neutral.setOnClickListener(new View.OnClickListener() {
                            boolean isAllSelected = true;

                            @Override
                            public void onClick(View v) {
                                isAllSelected = !isAllSelected;
                                setCheckedAll(isAllSelected);
                            }
                        });
                    }

                    //Check/Un-check all the options
                    private void setCheckedAll(boolean isChecked) {
                        for (int j = 0; j < listView.getCount(); j++) {
                            listView.setItemChecked(j, isChecked);
                        }
                        neutral.setText(isChecked ? R.string.select_none : R.string.select_all);
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_schedule, menu);
        MenuItem item = menu.findItem(R.id.action_search_schedule);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        SessionFragment sessionFragment = (SessionFragment) viewPagerAdapter.getRegisteredFragment(scheduleViewPager.getCurrentItem());
        if (sessionFragment == null) {
            Timber.d("SessionFragment is null!");
            return true;
        }
        ScheduleSessionsListAdapter sessionsListAdapter = sessionFragment.getSessionsListAdapter();
        if (!TextUtils.isEmpty(query)) {
            searchText = query;
            sessionsListAdapter.getFilter().filter(searchText);
        } else {
            sessionsListAdapter.refresh();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
