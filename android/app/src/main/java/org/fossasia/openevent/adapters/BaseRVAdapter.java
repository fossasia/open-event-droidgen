package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mohit
 * Date: 1/2/16
 * <p/>
 * Base class for all the adapters binding to a recycler view
 * Supports add, remove, clear methods & animations
 */
public abstract class BaseRVAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> implements Filterable {

    private List<T> dataList;

    BaseRVAdapter(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public T getItem(int position) {
        return dataList.get(position);
    }

    protected void addItem(T item) {
        addItem(getItemCount() - 1, item);
    }

    public void addItem(int position, T data) {
        dataList.add(position, data);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final T data = dataList.remove(fromPosition);
        dataList.add(toPosition, data);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * Animates addition/removal of items whenever the data changes.
     * Use this method when replacing data
     */
    public void animateTo(List<T> newData) {
        applyAndAnimateRemovals(newData);
        applyAndAnimateAdditions(newData);
        applyAndAnimateMovedItems(newData);
    }

    public void reset(List<T> newData) {
        this.dataList.clear();
        dataList = new ArrayList<>(newData);
        notifyDataSetChanged();
    }

    private void applyAndAnimateRemovals(List<T> newSessions) {
        List<T> dataList = new ArrayList<>(this.dataList);
        for (int i = dataList.size() - 1; i >= 0; i--) {
            final T data = dataList.get(i);
            if (!newSessions.contains(data)) {
                removeItem(this.dataList.indexOf(data));
            }
        }
    }

    private void applyAndAnimateAdditions(List<T> newSessions) {
        List<T> dataList = new ArrayList<>(this.dataList);
        int count = newSessions.size();
        for (int i = 0; i < count; i++) {
            final T data = newSessions.get(i);
            if (!dataList.contains(data)) {
                addItem(i, data);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<T> newDataList) {
        List<T> dataList = new ArrayList<>(this.dataList);
        for (int toPosition = newDataList.size() - 1; toPosition >= 0; toPosition--) {
            final T data = newDataList.get(toPosition);
            final int fromPosition = dataList.indexOf(data);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    /**
     * Removes an item from position
     */
    private T removeItem(int position) {
        final T speaker = dataList.remove(position);
        notifyItemRemoved(position);
        return speaker;
    }
}
