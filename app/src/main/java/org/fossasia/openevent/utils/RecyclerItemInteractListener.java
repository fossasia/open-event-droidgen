package org.fossasia.openevent.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by duncanleo on 25/2/16.
 * This is a RecyclerView OnItemTouchListener capable of detecting clicks and the opening
 * of an options menu, which is specified through an id that MUST be present in each child view.
 */
public class RecyclerItemInteractListener implements RecyclerView.OnItemTouchListener {
    GestureDetector clickGestureDetector;
    private float originalX = 0f, previousX = 0f, maxSwipeDistance = 200f;
    private int optionsViewId = -1;

    private OnItemInteractListener listener;

    public RecyclerItemInteractListener(Context context, OnItemInteractListener listener, int optionsViewId) {
        this.listener = listener;
        this.optionsViewId = optionsViewId;
        clickGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    /**
     * Set the id of the options screen
     * @param optionsView layout id
     */
    public void setOptionsView(int optionsView) {
        this.optionsViewId = optionsView;
    }

    /**
     * Snap a child view to a certain translationX. Animates using an AccelerateDecelerateInterpolator for 40ms.
     * @param childView view to snap
     * @param translationX translationX to snap to
     */
    private void snapChildView(View childView, float translationX) {
        childView.animate().cancel();
        childView.animate().translationX(translationX).setDuration(40).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        final View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (childView == null) {
            return false;
        }
        final int position = recyclerView.getChildPosition(childView);
        if (clickGestureDetector.onTouchEvent(e)) {
            listener.onItemClick(childView, position);
            //Snap item back if open
            if (childView.getTranslationX() != 0f) {
                snapChildView(childView, 0f);
            }
            return false;
        } else {
            float travelDistance = e.getRawX() - originalX, translationX = childView.getTranslationX();

            //Assume the width of the options view is the same, so no re-calculation is needed
            if (maxSwipeDistance != -1) {
                View optionsView = childView.findViewById(optionsViewId);
                optionsView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                maxSwipeDistance = optionsView.getMeasuredWidth() + optionsView.getPaddingRight();
            }

            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    originalX = e.getRawX();
                    previousX = e.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    translationX += travelDistance;
                    if (translationX < 0f) {
                        translationX = 0f;
                    } else if (translationX > maxSwipeDistance) {
                        translationX = maxSwipeDistance;
                    }

                    //listener
                    if (listener != null && translationX == maxSwipeDistance) {
                        listener.onOptionsViewOpened(childView.findViewById(optionsViewId), position);
                    }

                    childView.setTranslationX(translationX);
                    //Check for snapping
                    if (e.getRawX() > previousX) {
                        snapChildView(childView, maxSwipeDistance);
                    } else if (e.getRawX() < previousX) {
                        snapChildView(childView, 0f);
                    }
                    previousX = e.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                    return true;
            }
        }
        return false;
    }

    public void setMaxSwipeDistance(float maxSwipeDistance) {
        this.maxSwipeDistance = maxSwipeDistance;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemInteractListener {
        /**
         * Called when an item is clicked
         * @param view View of the item
         * @param position position of the item
         */
        void onItemClick(View view, int position);

        /**
         * Called when an item is swiped open to reveal its options view
         * This is a suitable place to set click handlers, or change displayed
         * views depending on the item.
         * @param optionsView View of the item's options view
         * @param position position of the item
         */
        void onOptionsViewOpened(View optionsView, int position);
    }
}