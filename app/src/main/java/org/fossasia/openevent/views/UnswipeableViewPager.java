package org.fossasia.openevent.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by duncanleo on 25/2/16.
 * This is a ViewPager that does not accept swipes to change the page(s).
 */
public class UnswipeableViewPager extends ViewPager {
    public UnswipeableViewPager(Context context) {
        super(context);
    }

    public UnswipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }
}
