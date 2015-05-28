package org.fossasia.openevent.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.R;
import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.fragments.MapFragment;
import org.fossasia.openevent.fragments.SpeakerFragment;
import org.fossasia.openevent.fragments.SponsorsFragment;
import org.fossasia.openevent.fragments.TracksFragment;


public class MainActivity extends ActionBarActivity
        implements ListView.OnItemClickListener {

    private static final String STATE_CURRENT_SECTION = "current_section";

    private Section currentSection;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private View mainMenu;
    private final View.OnClickListener menuFooterClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.settings:
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    break;
                case R.id.about:
                    new AboutDialogFragment().show(getSupportFragmentManager(), "about");
                    break;
            }
            mDrawerLayout.closeDrawer(mainMenu);
        }
    };
    private TextView lastUpdateTextView;
    private MainMenuAdapter menuAdapter;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Setup drawer layout
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.setDrawerShadow(getResources().getDrawable(R.drawable.drawer_shadow), Gravity.LEFT);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.main_menu, R.string.close_menu) {

            @Override
            public void onDrawerOpened(View drawerView) {
                updateActionBar();
                supportInvalidateOptionsMenu();
                // Make keypad navigation easier
                mainMenu.requestFocus();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                updateActionBar();
                supportInvalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(drawerToggle);
        // Disable drawerLayout focus to allow trackball navigation.
        // We handle the drawer closing on back press ourselves.
        mDrawerLayout.setFocusable(false);

        // Setup Main menu
        mainMenu = findViewById(R.id.main_menu);
        ListView menuListView = (ListView) findViewById(R.id.main_menu_list);
        LayoutInflater inflater = LayoutInflater.from(this);
        View menuHeaderView = inflater.inflate(R.layout.header_main_menu, null);
        menuListView.addHeaderView(menuHeaderView, null, false);
        View menuFooterView = inflater.inflate(R.layout.footer_main_menu, null);
        menuFooterView.findViewById(R.id.settings).setOnClickListener(menuFooterClickListener);
        menuFooterView.findViewById(R.id.about).setOnClickListener(menuFooterClickListener);
        menuListView.addFooterView(menuFooterView, null, false);


        menuAdapter = new MainMenuAdapter(inflater);
        menuListView.setAdapter(menuAdapter);
        menuListView.setOnItemClickListener(this);

        lastUpdateTextView = (TextView) findViewById(R.id.last_update);

        // Restore current section
        if (savedInstanceState == null) {
            currentSection = Section.TRACKS;
            String fragmentClassName = currentSection.getFragmentClassName();
            Fragment f = Fragment.instantiate(this, fragmentClassName);
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, f, fragmentClassName).commit();
        } else {
            currentSection = Section.values()[savedInstanceState.getInt(STATE_CURRENT_SECTION)];
        }
        // Ensure the current section is visible in the menu
        menuListView.setSelection(currentSection.ordinal());
        updateActionBar();
    }

    private void updateActionBar() {
        if (mDrawerLayout.isDrawerOpen(mainMenu)) {
            getSupportActionBar().setTitle(null);
        } else {
            getSupportActionBar().setTitle(currentSection.getTitleResId());
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerLayout.isDrawerOpen(mainMenu)) {
            updateActionBar();
        }
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mainMenu)) {
            mDrawerLayout.closeDrawer(mainMenu);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_SECTION, currentSection.ordinal());
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        if ((searchMenuItem != null) && (MenuItemCompat.isActionViewExpanded(searchMenuItem))) {
            MenuItemCompat.collapseActionView(searchMenuItem);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Hide & disable primary (contextual) action items when the main menu is opened
        if (mDrawerLayout.isDrawerOpen(mainMenu)) {
            final int size = menu.size();
            for (int i = 0; i < size; ++i) {
                MenuItem item = menu.getItem(i);
                if ((item.getOrder() & 0xFFFF0000) == 0) {
                    item.setVisible(false).setEnabled(false);
                }
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Will close the drawer if the home button is pressed
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Decrease position by 1 since the listView has a header view.
        Section section = menuAdapter.getItem(position - 1);
        if (section != currentSection) {
            // Switch to new section
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            Fragment f = fm.findFragmentById(R.id.content_frame);
            if (f != null) {
                if (currentSection.shouldKeep()) {
                    ft.detach(f);
                } else {
                    ft.remove(f);
                }
            }
            String fragmentClassName = section.getFragmentClassName();
            if (section.shouldKeep() && ((f = fm.findFragmentByTag(fragmentClassName)) != null)) {
                ft.attach(f);
            } else {
                f = Fragment.instantiate(this, fragmentClassName);
                ft.add(R.id.content_frame, f, fragmentClassName);
            }
            ft.commit();

            currentSection = section;
            menuAdapter.notifyDataSetChanged();
        }

        mDrawerLayout.closeDrawer(mainMenu);
    }


    // MAIN MENU

    private enum Section {
        TRACKS(TracksFragment.class, R.string.menu_tracks, R.drawable.ic_event_grey600_24dp, false),
        BOOKMARKS(BookmarksFragment.class, R.string.menu_bookmarks, R.drawable.ic_bookmark_grey600_24dp, false),
        SPEAKERS(SpeakerFragment.class, R.string.menu_speakers, R.drawable.ic_people_grey600_24dp, false),
        SPONSORS(SponsorsFragment.class, R.string.menu_sponsor, R.drawable.ic_sponsor_drawer, false),
        MAP(MapFragment.class, R.string.menu_map, R.drawable.ic_map_grey600_24dp, false);

        private final String fragmentClassName;
        private final int titleResId;
        private final int iconResId;
        private final boolean keep;

        private Section(Class<? extends Fragment> fragmentClass, @StringRes int titleResId,
                        @DrawableRes int iconResId, boolean keep) {
            this.fragmentClassName = fragmentClass.getName();
            this.titleResId = titleResId;
            this.iconResId = iconResId;
            this.keep = keep;
        }

        public String getFragmentClassName() {
            return fragmentClassName;
        }

        public int getTitleResId() {
            return titleResId;
        }

        public int getIconResId() {
            return iconResId;
        }

        public boolean shouldKeep() {
            return keep;
        }
    }

    public static class AboutDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Context context = getActivity();
            String title;
            try {
                String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                title = String.format("%1$s %2$s", getString(R.string.app_name), versionName);
            } catch (PackageManager.NameNotFoundException e) {
                title = getString(R.string.app_name);
            }

            return new AlertDialog.Builder(context).setTitle(title).setIcon(R.drawable.ic_launcher).setMessage(getResources().getText(R.string.about))
                    .setPositiveButton(android.R.string.ok, null).create();
        }

        @Override
        public void onStart() {
            super.onStart();
            // Make links clickable; must be called after the dialog is shown
            ((TextView) getDialog().findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private class MainMenuAdapter extends BaseAdapter {

        private Section[] sections = Section.values();
        private LayoutInflater inflater;
        private int currentSectionForegroundColor;
        private int currentSectionBackgroundColor;

        public MainMenuAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
            // Select the primary color to tint the current section
            TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
            try {
                currentSectionForegroundColor = a.getColor(0, Color.TRANSPARENT);
            } finally {
                a.recycle();
            }
            currentSectionBackgroundColor = getResources().getColor(R.color.translucent_grey);
        }

        @Override
        public int getCount() {
            return sections.length;
        }

        @Override
        public Section getItem(int position) {
            return sections[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_main_menu, parent, false);
            }

            Section section = getItem(position);

            TextView tv = (TextView) convertView.findViewById(R.id.section_text);
            SpannableString sectionTitle = new SpannableString(getString(section.getTitleResId()));
            Drawable sectionIcon = getResources().getDrawable(section.getIconResId());
            int backgroundColor;
            if (section == currentSection) {
                // Special color for the current section
                sectionTitle.setSpan(new ForegroundColorSpan(currentSectionForegroundColor), 0, sectionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // We need to mutate the drawable before applying the ColorFilter, or else all the similar drawable instances will be tinted.
                sectionIcon.mutate().setColorFilter(currentSectionForegroundColor, PorterDuff.Mode.SRC_IN);
                backgroundColor = currentSectionBackgroundColor;
            } else {
                backgroundColor = Color.TRANSPARENT;
            }
            tv.setText(sectionTitle);
            tv.setCompoundDrawablesWithIntrinsicBounds(sectionIcon, null, null, null);
            tv.setBackgroundColor(backgroundColor);

            return convertView;
        }
    }
}
