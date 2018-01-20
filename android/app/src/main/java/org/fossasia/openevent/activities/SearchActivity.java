package org.fossasia.openevent.activities;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
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
import org.fossasia.openevent.listeners.BookmarkStatus;
import org.fossasia.openevent.adapters.GlobalSearchAdapter;
import org.fossasia.openevent.viewmodels.SearchActivityViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private GlobalSearchAdapter globalSearchAdapter;
    private List<Object> results = new ArrayList<>();

    private Realm realm = Realm.getDefaultInstance();
    private SearchView searchView;
    private String searchText;

    private final String SEARCH = "SAVE_KEY_ON_ROTATE";
    private SearchActivityViewModel searchViewModel;

    @BindView(R.id.search_recyclerView)
    protected RecyclerView searchRecyclerView;
    @BindView(R.id.txt_no_results)
    protected TextView noResultsView;
    @BindView(R.id.main_content)
    protected CoordinatorLayout coordinatorLayoutParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OpenEventApp.getEventBus().register(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpRecyclerView();
        handleVisibility();

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            handleIntent(getIntent());
        }
        searchViewModel = ViewModelProviders.of(this).get(SearchActivityViewModel.class);
        searchText = searchViewModel.getSearchText();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_activity, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        // To expand search view by default
        searchView.setIconified(false);
        searchView.requestFocus();
        if (searchText != null) {
            searchView.setQuery(searchText, true);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchQuery(query);
        return true;
    }

    private void setUpRecyclerView() {
        globalSearchAdapter = new GlobalSearchAdapter(results, this);
        searchRecyclerView.setAdapter(globalSearchAdapter);
        searchRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        searchQuery(query);
    }

    private void searchQuery(String constraint) {
        searchViewModel.getSearchResults(constraint).observe(SearchActivity.this, searches -> {
            results.clear();
            results.add(searches);
            globalSearchAdapter.setCopyOfSearches(searches);
            globalSearchAdapter.notifyDataSetChanged();
            handleVisibility();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OpenEventApp.getEventBus().unregister(this);
        //Set listener to null to avoid memory leaks
        if (searchView != null) searchView.setOnQueryTextListener(null);
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

}

