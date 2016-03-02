package org.fossasia.openevent.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.FilterAdapter;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Information;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.utils.SaveFilters;

import java.util.ArrayList;
import java.util.List;

/**
 * User: MananWason
 * Date: 14-06-2015
 */
public class TracksActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "TracksActivity";

    final private String SEARCH = "org.fossasia.openevent.searchText";

    SessionsListAdapter sessionsListAdapter;

    private String track;

    private RecyclerView sessionsRecyclerView;

    private String searchText = "";

    private SearchView searchView;
    private RecyclerView recyclerView;
    static String[] title = { "DevOps", "General", "OpenTech", "Web","Python", "Mozilla", "Exhibition"};
    private TextView reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        track = getIntent().getStringExtra(IntentStrings.TRACK);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadImage();
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(track);
        sessionsRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        sessionsListAdapter = new SessionsListAdapter(dbSingleton.getSessionbyTracksname(track));

        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionsRecyclerView.setAdapter(sessionsListAdapter);
        sessionsListAdapter.setOnClickListener(new SessionsListAdapter.SetOnClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Session model = (Session) sessionsListAdapter.getItem(position);
                String sessionName = model.getTitle();
                Intent intent = new Intent(getApplicationContext(), SessionDetailActivity.class);
                intent.putExtra(IntentStrings.SESSION, sessionName);
                intent.putExtra(IntentStrings.TRACK, track);
                startActivity(intent);
            }
        });
        sessionsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton shareFab = (FloatingActionButton) findViewById(R.id.share_fab);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Urls.WEB_APP_URL_BASIC + Urls.SESSIONS);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getString(R.string.share_links)));
            }
        });

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
        // use no filter at starting
        noFilter();


    }
    public void noFilter() {
        Boolean[] temp = new Boolean[title.length];
        for(int i = 0; i < temp.length; i++) {
            temp[i] = false;
        }
        new SaveFilters().saveArray(temp, "Filters", this);
    }
    public void applyFilter() {

        Boolean[] sessFilter = new SaveFilters().loadArray("Filters", this);
        for(int i = 0; i < title.length; i++) {
            if(sessFilter[i]) {
                // apply this filter
                Log.d("whifilter", title[i]);
                sessionsListAdapter.getFilter().filter(title[i]);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
    }

    private void loadImage() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        Track current = dbSingleton.getTrackbyName(track);

        ImageView backdrop1 = (ImageView) findViewById(R.id.backdrop);
        if (current.getImage().length() != 0) {
            Picasso.with(getApplicationContext()).load(current.getImage()).into(backdrop1);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search_sessions:
                return true;
            case R.id.filter_button:
                // dialog for filter selection
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                LayoutInflater inflater = LayoutInflater.from(this);
                final View view = inflater.inflate(R.layout.filter_item, null);
                //recyclerview
                recyclerView = (RecyclerView)view.findViewById(R.id.filter_tracks);
                FilterAdapter adapter = new FilterAdapter(this, getData());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                reset = (TextView)view.findViewById(R.id.reset);
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noFilter();
                       // sessionsListAdapter.getFilter()
                        Log.d("clicked", "reset");
                        FilterAdapter adapter1 = new FilterAdapter(getBaseContext(), getData());
                        recyclerView.setAdapter(adapter1);

                    //    view.invalidate();
                    }
                });


                builder.setView(view);

                builder.setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionsListAdapter.refresh();
                        applyFilter();
                    }
                });

                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_sessions_activity, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search_sessions).getActionView();
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!TextUtils.isEmpty(query)) {
            sessionsListAdapter.getFilter().filter(query);
            Log.d("changed", query);
        } else {
            sessionsListAdapter.refresh();
        }
        searchText = query;
        return true;
    }

    // getdata for filter recyclerview
    public static List<Information> getData() {
        List<Information> data = new ArrayList<>();

        for(int i = 0; i < title.length && i < title.length; i++) {
            Information current  = new Information();

            current.Title = title[i];

            data.add(current);
        }
        return data;
    }
}
