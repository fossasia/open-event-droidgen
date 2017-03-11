package org.fossasia.openevent.utils;

/*
* Interface to connect BookmarksFragment and SessionsListAdapter.
* onChange Method is called by the onClick method
* of Positive Button of the 'Remove Bookmark' Dialog
* in the onBindViewHolder method insideSessionsListAdapter
* */

public interface BookmarksListChangeListener {
    void onChange();
}
