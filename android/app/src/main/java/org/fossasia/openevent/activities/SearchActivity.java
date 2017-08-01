package org.fossasia.openevent.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.GlobalSearchAdapter;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private GlobalSearchAdapter globalSearchAdapter;
    private List<Object> results = new ArrayList<>();

    private Realm realm = Realm.getDefaultInstance();

    private SearchView searchView;
    private String searchText;

    private final String SEARCH = "SAVE_KEY_ON_ROTATE";

    private final static String TRACK_HEADER = "Tracks";
    private final static String SESSION_HEADER = "Sessions";
    private final static String SPEAKER_HEADER = "Speakers";
    private final static String LOCATION_HEADER = "Locations";

    @BindView(R.id.search_recyclerView)
    protected RecyclerView searchRecyclerView;
    @BindView(R.id.txt_no_results)
    protected TextView noResultsView;
    @BindView(R.id.fab_filter_search)
    FloatingActionButton fabFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OpenEventApp.getEventBus().register(this);

        handleVisibility();

        populateFilters();

        globalSearchAdapter = new GlobalSearchAdapter(results, this);
        searchRecyclerView.setAdapter(globalSearchAdapter);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            handleIntent(getIntent());
        }

        fabFilter.setOnClickListener(view -> {
            AlertDialog filterDialog;

            final String[] checks = {TRACK_HEADER, SESSION_HEADER, SPEAKER_HEADER, LOCATION_HEADER};
            final ArrayList<Integer> selectedItems = new ArrayList<>();
            final boolean[] defaultCheckedValues = {SharedPreferencesUtil.getBoolean(TRACK_HEADER, true),
                    SharedPreferencesUtil.getBoolean(SESSION_HEADER, true),
                    SharedPreferencesUtil.getBoolean(SPEAKER_HEADER, true),
                    SharedPreferencesUtil.getBoolean(LOCATION_HEADER, true)};

            AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
            builder.setTitle("Filters");
            builder.setMultiChoiceItems(checks, defaultCheckedValues,
                    (dialog, indexSelected, isChecked) -> {
                        SharedPreferencesUtil.putBoolean(checks[indexSelected], isChecked);
                        defaultCheckedValues[indexSelected] = isChecked;
                        if (isChecked) {
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    })
                    .setPositiveButton("OK", (dialog, id) -> searchQuery(searchText))
                    .setNegativeButton("Cancel", (dialog, id) -> Timber.d("Filter Dialog Cancelled"));

            filterDialog = builder.create();
            filterDialog.show();
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search_home).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, true);
        }
        return true;

    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query != null) {
            searchQuery(query);
            searchText = query;
        } else {
            results.clear();
            handleVisibility();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        final String query = intent.getStringExtra(SearchManager.QUERY);
        searchText = query;
        searchQuery(query);
    }

    private void searchQuery(String constraint) {
        results.clear();

        if (!TextUtils.isEmpty(constraint)) {
            String query = constraint.toLowerCase(Locale.getDefault());
            String wildcardQuery = String.format("*%s*", query);

            if (SharedPreferencesUtil.getBoolean(TRACK_HEADER, true)) {
                addResultsFromTracks(wildcardQuery);
            }
            if (SharedPreferencesUtil.getBoolean(SESSION_HEADER, true)) {
                addResultFromSessions(wildcardQuery);
            }
            if (SharedPreferencesUtil.getBoolean(SPEAKER_HEADER, true)) {
                addResultsFromSpeakers(wildcardQuery);
            }
            if (SharedPreferencesUtil.getBoolean(LOCATION_HEADER, true)) {
                addResultsFromLocations(wildcardQuery);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (searchView != null) {
            outState.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OpenEventApp.getEventBus().unregister(this);

        //Closing realm instance and detaching listeners to avoid memory leaks
        realm.close();
        realm.removeAllChangeListeners();

        //Set listener to null to avoid memory leaks
        if (searchView != null) searchView.setOnQueryTextListener(null);
    }

    public void addResultsFromTracks(String queryString) {
        RealmResults<Track> filteredTracks = realm.where(Track.class)
                .like("name", queryString, Case.INSENSITIVE).findAllSortedAsync("name");

        filteredTracks.addChangeListener(tracks -> {
            if (tracks.size() > 0) {
                results.add(TRACK_HEADER);
            }
            results.addAll(tracks);
            globalSearchAdapter.notifyDataSetChanged();
            Timber.d("Filtering done total results %d", tracks.size());
            handleVisibility();
        });
    }

    public void addResultsFromSpeakers(String queryString) {
        RealmResults<Speaker> filteredSpeakers = realm.where(Speaker.class)
                .like("name", queryString, Case.INSENSITIVE).or()
                .like("country", queryString, Case.INSENSITIVE).or()
                .like("organisation", queryString, Case.INSENSITIVE).findAllSortedAsync("name");

        filteredSpeakers.addChangeListener(speakers -> {
            if (speakers.size() > 0) {
                results.add(SPEAKER_HEADER);
            }
            results.addAll(speakers);
            globalSearchAdapter.notifyDataSetChanged();
            Timber.d("Filtering done total results %d", speakers.size());
            handleVisibility();
        });
    }

    public void addResultsFromLocations(String queryString) {
        RealmResults<Microlocation> filteredMicrolocations = realm.where(Microlocation.class)
                .like("name", queryString, Case.INSENSITIVE).findAllSortedAsync("name");

        filteredMicrolocations.addChangeListener(microlocations -> {
            if (microlocations.size() > 0) {
                results.add(LOCATION_HEADER);
            }
            results.addAll(filteredMicrolocations);
            globalSearchAdapter.notifyDataSetChanged();
            Timber.d("Filtering done total results %d", microlocations.size());
            handleVisibility();
        });
    }

    public void addResultFromSessions(String queryString) {
        RealmResults<Session> filteredSessions = realm.where(Session.class)
                .like("title", queryString, Case.INSENSITIVE).findAllSortedAsync("title");

        filteredSessions.addChangeListener(sessions -> {
            if (sessions.size() > 0) {
                results.add(SESSION_HEADER);
            }
            results.addAll(filteredSessions);
            globalSearchAdapter.notifyDataSetChanged();
            Timber.d("Filtering done total results %d", sessions.size());
            handleVisibility();
        });
    }

    public void handleVisibility() {
        if (results.size() == 0) {
            noResultsView.setVisibility(View.VISIBLE);
            searchRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            noResultsView.setVisibility(View.INVISIBLE);
            searchRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void populateFilters() {
        SharedPreferencesUtil.putBoolean(TRACK_HEADER, true);
        SharedPreferencesUtil.putBoolean(SPEAKER_HEADER, true);
        SharedPreferencesUtil.putBoolean(LOCATION_HEADER, true);
        SharedPreferencesUtil.putBoolean(SESSION_HEADER, true);
    }

}

