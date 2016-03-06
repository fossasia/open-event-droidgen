package org.fossasia.openevent.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * User: msk
 * Date: 25-01-2016
 */
public class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

    private Runnable runnable;
    private InputMethodManager inputMethodManager;
    public SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
            runnable.run();
            runnable = null;
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
        super.onDrawerSlide(drawerView, slideOffset);
    }

    public void runWhenIdle(Runnable runnable) {
        this.runnable = runnable;
    }
}