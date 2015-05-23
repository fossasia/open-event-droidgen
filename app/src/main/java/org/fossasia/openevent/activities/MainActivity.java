package org.fossasia.openevent.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
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
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.fragments.MapFragment;
import org.fossasia.openevent.fragments.SpeakerFragment;
import org.fossasia.openevent.fragments.SponsorsFragment;
import org.fossasia.openevent.fragments.TracksFragment;


public class MainActivity extends ActionBarActivity
        implements ListView.OnItemClickListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mainMenu;
    private Section current_section;
    private MainMenuAdapter menuAdapter;

    private static final String CURRENT_SECTION = "current_section";

    private final View.OnClickListener menuFooterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.settings:
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    overridePendingTransition(R.anim.abc_slide_out_bottom, R.anim.abc_slide_out_top);
                    break;

            }
            mDrawerLayout.closeDrawer(mainMenu);
        }
    };
    private TextView net_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        //Drawer Setup
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.setDrawerShadow(getResources().getDrawable(R.drawable.drawer_shadow), Gravity.LEFT);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.main_menu, R.string.close_menu) {
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
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setFocusable(true);


        mainMenu = findViewById(R.id.main_menu);
        ListView menuList = (ListView) findViewById(R.id.main_menu_list);
        LayoutInflater inflater = LayoutInflater.from(this);

        //Header Main Menu
        View menuHeaderView = inflater.inflate(R.layout.header_main_menu, null);
        menuList.addHeaderView(menuHeaderView, null, false);
        View menuFooterView = inflater.inflate(R.layout.footer_main_menu, null);
        menuFooterView.findViewById(R.id.settings).setOnClickListener(menuFooterClickListener);
        menuFooterView.findViewById(R.id.about).setOnClickListener(menuFooterClickListener);
        menuList.addFooterView(menuFooterView, null, false);


        menuList.setOnItemClickListener(this);

        if (savedInstanceState == null) {
            current_section = Section.TRACKS;
            String fragmentName = current_section.getFragmentClassName();
            Fragment fragment = Fragment.instantiate(this, fragmentName);
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment, fragmentName).commit();

        } else {
            current_section = Section.values()[savedInstanceState.getInt(CURRENT_SECTION)];
            menuList.setSelection(current_section.ordinal());
            updateActionBar();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstance) {
        super.onPostCreate(savedInstance);

        if (mDrawerLayout.isDrawerOpen(mainMenu)) {
            updateActionBar();
        }
        mDrawerToggle.syncState();
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
        outState.putInt(CURRENT_SECTION, current_section.ordinal());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateActionBar() {
        if (mDrawerLayout.isDrawerOpen(mainMenu)) {
            getSupportActionBar().setTitle(null);

        } else {
            getSupportActionBar().setTitle(current_section.getTitleResId());
        }
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }


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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Section section = menuAdapter.getItem(position - 1);
        if (section != current_section) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            Fragment f = fm.findFragmentById(R.id.content_frame);
            if (f != null) {
                if (current_section.shouldKeep()) {
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

            current_section = section;
            menuAdapter.notifyDataSetChanged();

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
            if (section == current_section) {
                // Special color for the current section
                //sectionTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, sectionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
