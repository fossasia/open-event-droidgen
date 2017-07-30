package org.fossasia.openevent.views.stickyheadersrecyclerview.caching;

import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.fossasia.openevent.views.stickyheadersrecyclerview.util.OrientationProvider;


/**
 * User: Shailesh Baldaniya
 * Date: 13-03-17
 * An implementation of {@link HeaderProvider} that creates and caches header views
 */
public class HeaderViewCache implements HeaderProvider {

    private final StickyRecyclerHeadersAdapter mAdapter;
    private final LongSparseArray<View> mHeaderViews = new LongSparseArray<>();
    private final OrientationProvider mOrientationProvider;

    public HeaderViewCache(StickyRecyclerHeadersAdapter adapter,
                           OrientationProvider orientationProvider) {
        mAdapter = adapter;
        mOrientationProvider = orientationProvider;
    }

    @Override
    public View getHeader(RecyclerView parent, int position) {
        long headerId = mAdapter.getHeaderId(position);

        View header = mHeaderViews.get(headerId);

        if (header != null) {
            if (header.getWidth() != parent.getWidth()) {
                int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
                int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                        parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
                header.measure(childWidth, header.getHeight());
                header.layout(0, 0, parent.getWidth(), header.getHeight());
            }
        } else {
            RecyclerView.ViewHolder viewHolder = mAdapter.onCreateHeaderViewHolder(parent);
            mAdapter.onBindHeaderViewHolder(viewHolder, position);
            header = viewHolder.itemView;
            if (header.getLayoutParams() == null) {
                header.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            int widthSpec;
            int heightSpec;

            if (mOrientationProvider.getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);
            } else {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.UNSPECIFIED);
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.EXACTLY);
            }

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);
            header.measure(childWidth, childHeight);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
            mHeaderViews.put(headerId, header);
        }
        return header;
    }

    @Override
    public void invalidate() {
        mHeaderViews.clear();
    }
}
