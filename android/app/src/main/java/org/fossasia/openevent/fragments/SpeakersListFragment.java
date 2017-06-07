package org.fossasia.openevent.fragments;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SpeakersListAdapter;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.ShowNotificationSnackBar;
import org.fossasia.openevent.views.MarginDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static org.fossasia.openevent.utils.SortOrder.sortOrderSpeaker;

public class SpeakersListFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static final String PREF_SORT = "sortType";

    final private String SEARCH = "searchText";

    private SharedPreferences prefsSort;

    @BindView(R.id.speaker_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_no_speakers)  TextView noSpeakersView;
    @BindView(R.id.rv_speakers) RecyclerView speakersRecyclerView;

    private List<Speaker> mSpeakers = new ArrayList<>();
    private SpeakersListAdapter speakersListAdapter;

    private GridLayoutManager gridLayoutManager;

    private String searchText = "";

    private SearchView searchView;

    private int sortType;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        OpenEventApp.getEventBus().register(this);

        prefsSort = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortType = prefsSort.getInt(PREF_SORT, 0);

        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        final int spanCount = (int) (width/150.00);

        speakersRecyclerView.addItemDecoration(new MarginDecoration(getContext()));
        speakersRecyclerView.setHasFixedSize(true);
        speakersListAdapter = new SpeakersListAdapter(mSpeakers, getActivity());
        speakersRecyclerView.setAdapter(speakersListAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        speakersRecyclerView.setLayoutManager(gridLayoutManager);

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        loadData();

        handleVisibility();

        return view;
    }

    private void loadData() {
        realmRepo.getSpeakers(sortOrderSpeaker(getContext()))
                .addChangeListener((speakers, orderedCollectionChangeSet) -> {

                    mSpeakers.clear();
                    mSpeakers.addAll(speakers);

                    speakersListAdapter.notifyDataSetChanged();

                    handleVisibility();
                });
    }

    private void handleVisibility() {
        if (!mSpeakers.isEmpty()) {
            noSpeakersView.setVisibility(View.GONE);
            speakersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSpeakersView.setVisibility(View.VISIBLE);
            speakersRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_speakers;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OpenEventApp.getEventBus().unregister(this);

        // Remove listeners to fix memory leak
        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
        if(searchView != null) searchView.setOnQueryTextListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (isAdded() && searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:

                int num_orgs = speakersListAdapter.getDistinctOrgs();
                int num_country = speakersListAdapter.getDistinctCountry();

                int list_options;

                if(num_orgs==1 && num_country==1){
                    list_options = R.array.speaker_sort_name;
                } else if(num_orgs==1){
                    list_options = R.array.speaker_sort_name_country;
                } else if(num_country==1){
                    list_options = R.array.speaker_sort_name_organisation;
                } else{
                    list_options = R.array.speaker_sort_all;
                }

                final AlertDialog.Builder dialogSort = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog_sort_title)
                        .setSingleChoiceItems(list_options, sortType, (dialog, which) -> {
                            sortType = which;
                            SharedPreferences.Editor editor = prefsSort.edit();
                            editor.putInt(PREF_SORT, which);
                            editor.apply();
                            loadData();
                            dialog.dismiss();
                        });

                dialogSort.show();
                break;
            default:
                //Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_speakers, menu);
        MenuItem item = menu.findItem(R.id.action_search_speakers);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if(searchView != null && !TextUtils.isEmpty(searchText))
            searchView.setQuery(searchText, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 150.00);
        gridLayoutManager.setSpanCount(spanCount);
    }

    private void refresh() {
        NetworkUtils.checkConnectionSpeakers(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {
            @Override
            public void activeConnection() {
                //Internet is working
            }

            @Override
            public void inactiveConnection() {
                //internet is not working
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                showNotification();
            }

            @Override
            public void networkAvailable() {
                //network is available
            }

            @Override
            public void networkUnavailable() {
                //network is unavailable
            }
        });
    }

    public void showNotification() {
        ShowNotificationSnackBar showNotificationSnackBar = new ShowNotificationSnackBar(getContext(),getView(),swipeRefreshLayout) {
            @Override
            public void refreshClicked() {
                refresh();
            }
        };
        //show snackbar will be useful if user have blocked notification for this app
        showNotificationSnackBar.showSnackBar();
        showNotificationSnackBar.buildNotification();
    }

    @Subscribe
    public void speakerDownloadDone(SpeakerDownloadEvent event) {
        if(swipeRefreshLayout == null)
            return;

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            Timber.i("Speaker download completed");
        } else {
            Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            Timber.i("Speaker download failed.");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //hide keyboard on search click
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchText = query;

        speakersListAdapter.getFilter().filter(query);

        return true;
    }

}
