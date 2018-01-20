package org.fossasia.openevent.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.listeners.BookmarkStatus;
import org.fossasia.openevent.adapters.ScheduleViewPagerAdapter;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.listeners.OnBookmarkSelectedListener;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.SnackbarUtil;
import org.fossasia.openevent.utils.SortOrder;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.viewmodels.ScheduleFragmentViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Manan Wason on 16/06/16.
 */
public class ScheduleFragment extends BaseFragment implements OnBookmarkSelectedListener {

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout scheduleTabLayout;
    @BindView(R.id.schedule_fab_filter) FloatingActionButton fabFilter;
    @BindView(R.id.filter_text) TextView filtersText;
    @BindView(R.id.close_filter) ImageView closeFilterBarButton;
    @BindView(R.id.filter_bar) LinearLayout filterBar;
    @BindView(R.id.coordinate_layout_schedule)
    protected CoordinatorLayout coordinatorLayoutParent;

    private CompositeDisposable compositeDisposable;
    private int sortType;
    private int sortOrder;
    private ScheduleViewPagerAdapter adapter;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private List<Track> tracks = new ArrayList<>();
    private String tracksNames[];
    private boolean isTrackSelected[];
    private List<String> selectedTracks;
    private Dialog sortDialog;
    private ScheduleFragmentViewModel scheduleFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        filterBar.setVisibility(View.GONE);
        OpenEventApp.getEventBus().register(true);
        compositeDisposable = new CompositeDisposable();
        sortType = SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SCHEDULE, 2);
        sortOrder = SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_ORDER, 0);
        selectedTracks = new ArrayList<>();

        scheduleFragmentViewModel = ViewModelProviders.of(this).get(ScheduleFragmentViewModel.class);

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

        scheduleFragmentViewModel.getEventDateString().observe(this, datePair -> {
            for (int i = 0; i < datePair.size(); i++) {
                adapter.addFragment(new DayScheduleFragment(), datePair.get(i).first, datePair.get(i).second);
                ((DayScheduleFragment) adapter.getLast()).setOnBookmarkSelectedListener(this);
                adapter.notifyDataSetChanged();
            }
        });

        scheduleFragmentViewModel.getTracks().observe(this, tracksList -> {
            tracks.clear();
            tracks.addAll(tracksList);
            tracksNames = new String[tracks.size()];
            isTrackSelected = new boolean[tracks.size()];

            for(int i = 0; i < tracks.size(); i++){
                tracksNames[i] = tracks.get(i).getName();
            }
        });

        viewPager.setAdapter(adapter);
        viewPager.setPageMargin(Math.round(Utils.dpToPx(15)));
        viewPager.setPageMarginDrawable(R.color.grey);

        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // No action
            }

            @Override
            public void onPageSelected(int position) {
                // No action
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                notifyUpdate(-1, selectedTracks);
            }
        };

        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private void notifyUpdate(int position, List<String> selectedTracks) {
        if(position == -1)
            position = viewPager.getCurrentItem();

        ((DayScheduleFragment) adapter.getItem(position)).filter(selectedTracks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                final AlertDialog.Builder dialogSort = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog_sort_title)
                        .setSingleChoiceItems(R.array.session_sort, sortType, (dialog, which) -> {
                            sortType = which;
                            SharedPreferencesUtil.putInt(ConstantStrings.PREF_SORT_SCHEDULE, which);
                            notifyUpdate(-1, selectedTracks);
                        })
                        .setPositiveButton(R.string.ascending, (dialog, which) -> {
                            sortOrder = SortOrder.SORT_ORDER_ASCENDING;
                            SharedPreferencesUtil.putInt(ConstantStrings.PREF_SORT_ORDER, sortOrder);
                            notifyUpdate(-1, selectedTracks);
                            dialog.dismiss();
                        })
                        .setNegativeButton(R.string.descending, (dialog, which) -> {
                            sortOrder = SortOrder.SORT_ORDER_DESCENDING;
                            SharedPreferencesUtil.putInt(ConstantStrings.PREF_SORT_ORDER, sortOrder);
                            notifyUpdate(-1, selectedTracks);
                            dialog.dismiss();
                        });

                AlertDialog dialog = dialogSort.show();
                dialog.getButton(sortOrder == SortOrder.SORT_ORDER_ASCENDING ? AlertDialog.BUTTON_NEGATIVE : AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                dialog.getButton(sortOrder == SortOrder.SORT_ORDER_ASCENDING ? AlertDialog.BUTTON_POSITIVE : AlertDialog.BUTTON_NEGATIVE)
                        .setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                dialog.show();
                break;
            default:
                //Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick (R.id.schedule_fab_filter)
    public void filterSchedule() {
        final AlertDialog.Builder dialogSort = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_filter_title)
                .setOnKeyListener((dialog, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK &&
                            event.getAction() == KeyEvent.ACTION_UP &&
                            !event.isCanceled()) {
                        Arrays.fill(isTrackSelected, false);
                        selectedTracks.clear();
                        notifyUpdate(-1, selectedTracks);
                        dialog.cancel();
                        return true;
                    }
                    return false;
                })
                .setMultiChoiceItems(tracksNames, isTrackSelected, (dialog, which, isSelected) -> isTrackSelected[which] = isSelected)
                .setPositiveButton("Filter", (dialogInterface, j) -> {
                    selectedTracks.clear();
                    int count = 0;
                    String tracksFiltered = "";
                    for(int i=0 ; i<isTrackSelected.length ; i++) {
                        if (isTrackSelected[i]) {
                            selectedTracks.add(tracksNames[i]);
                            if(count == 0)
                                tracksFiltered += tracksNames[i];
                            else
                                tracksFiltered += ("," + tracksNames[i]);
                            count++;
                        }
                    }
                    notifyUpdate(-1, selectedTracks);
                    if(count!=0) {
                        filtersText.setText("Filters" + "(" + count +")" + ": " + tracksFiltered);
                        filterBar.setVisibility(View.VISIBLE);
                    } else {
                        filterBar.setVisibility(View.GONE);
                    }
                });

        dialogSort.show();
    }

    @OnClick (R.id.close_filter)
    public void closeFilterBar() {
        Arrays.fill(isTrackSelected, false);
        selectedTracks.clear();
        notifyUpdate(-1, selectedTracks);
        filterBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OpenEventApp.getEventBus().unregister(this);
        if(compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
        if(viewPager != null && onPageChangeListener != null)
            viewPager.removeOnPageChangeListener(onPageChangeListener);
        for (int i = 0; i < adapter.getCount(); i++)
            ((DayScheduleFragment) adapter.getItem(i)).clearOnBookmarkSelectedListener();
    }

    @Override
    public void showSnackbar(BookmarkStatus bookmarkStatus) {
        Snackbar snackbar = Snackbar.make(coordinatorLayoutParent, SnackbarUtil.getMessageResource(bookmarkStatus), Snackbar.LENGTH_LONG);
        SnackbarUtil.setSnackbarAction(getContext(), snackbar, bookmarkStatus)
                .show();    }
}

