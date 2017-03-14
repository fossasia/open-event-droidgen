package org.fossasia.openevent.views.stickyheadersrecyclerview;

/**
 * User: Shailesh Baldaniya
 * Date: 13-03-17
 * <p/>
 * ItemVisibilityAdapter provides a way for StickyRecyclerHeadersDecoration
 * to know if a row is visible or not.  This comes into play if the
 * recyclerview's layout manager  is set up to provide extra layout space (by
 * overriding getExtraLayoutSpace). In this case rows that aren't visible (yet)
 * will be bound and StickyRecyclerHeadersDecoration will need to know which
 * are visible to correctly calculate the row to base the sticky header on
 * <p>
 * To use it you must pass an instance of a class that implements this
 * interface as a second argment StickyRecyclerHeadersDecoration's constructor.
 */
public interface ItemVisibilityAdapter {

    /**
     * Return true the specified adapter position is visible, false otherwise
     * <p>
     * The implementation of this method will typically return true if
     * the position is between the layout manager's findFirstVisibleItemPosition
     * and findLastVisibleItemPosition (inclusive).
     *
     * @param position the adapter position
     */
    boolean isPositionVisible(final int position);
}
