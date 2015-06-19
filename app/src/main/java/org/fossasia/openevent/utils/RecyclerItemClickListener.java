package org.fossasia.openevent.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
<<<<<<< HEAD
import android.widget.AdapterView;
=======
>>>>>>> master

/**
 * Created by MananWason on 15-06-2015.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
<<<<<<< HEAD
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;
=======
    GestureDetector mGestureDetector;
    private OnItemClickListener mListener;
>>>>>>> master

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
<<<<<<< HEAD
            @Override public boolean onSingleTapUp(MotionEvent e) {
=======
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
>>>>>>> master
                return true;
            }
        });
    }

<<<<<<< HEAD
    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
=======
    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
>>>>>>> master
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView));
            return true;
        }
        return false;
    }

<<<<<<< HEAD
    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }
=======
    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }
>>>>>>> master

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
<<<<<<< HEAD
=======

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
>>>>>>> master
}