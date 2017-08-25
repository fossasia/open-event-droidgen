package org.fossasia.openevent.fragments;


import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import org.fossasia.openevent.api.DataDownloadManager;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.Views;
import org.fossasia.openevent.views.MarginDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmResults;
import timber.log.Timber;

import static org.fossasia.openevent.utils.SortOrder.sortOrderSpeaker;

public class SpeakersListFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    final private String SEARCH = "searchText";

    @BindView(R.id.speaker_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_no_speakers)  TextView noSpeakersView;
    @BindView(R.id.rv_speakers) RecyclerView speakersRecyclerView;

    private List<Speaker> speakers = new ArrayList<>();
    private SpeakersListAdapter speakersListAdapter;

    private GridLayoutManager gridLayoutManager;

    private String searchText = "";
    private SearchView searchView;

    private int sortType;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private RealmResults<Speaker> realmResults;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
        setUpRecyclerView();

        sortType = SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SPEAKER, 0);

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        loadData();

        handleVisibility();

        return view;
    }

    private void setUpRecyclerView() {
        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        final int spanCount = (int) (width / 150.00);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);

        speakersListAdapter = new SpeakersListAdapter(speakers, getActivity());

        speakersRecyclerView.addItemDecoration(new MarginDecoration(getContext()));
        speakersRecyclerView.setHasFixedSize(true);
        speakersRecyclerView.setAdapter(speakersListAdapter);
        speakersRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private void loadData() {
        realmResults = realmRepo.getSpeakers(sortOrderSpeaker());
        realmResults.addChangeListener((speakers, orderedCollectionChangeSet) -> {
            this.speakers.clear();
            this.speakers.addAll(speakers);

            speakersListAdapter.setCopyOfSpeakers(speakers);
            speakersListAdapter.notifyDataSetChanged();
            if (!Utils.isEmpty(searchText))
                speakersListAdapter.filter(searchText);
            handleVisibility();
        });
    }

    private void handleVisibility() {
        if (!speakers.isEmpty()) {
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
        Utils.unregisterIfUrlValid(this);

        // Remove listeners to fix memory leak
        realmResults.removeAllChangeListeners();
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
                            SharedPreferencesUtil.putInt(ConstantStrings.PREF_SORT_SPEAKER, which);
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
        DrawableCompat.setTint(menu.findItem(R.id.action_search_speakers).getIcon(), Color.WHITE);
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

    @Subscribe
    public void speakerDownloadDone(SpeakerDownloadEvent event) {
        Views.setSwipeRefreshLayout(swipeRefreshLayout, false);

        if (event.isState()) {
            Timber.i("Speaker download completed");
        } else {
            Timber.i("Speaker download failed.");
            if (getActivity() != null && swipeRefreshLayout != null) {
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                DataDownloadManager.getInstance().downloadSpeakers();
            }

            @Override
            public void networkUnavailable() {
                OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
            }
        });
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
        speakersListAdapter.filter(query);

        return true;
    }

}
