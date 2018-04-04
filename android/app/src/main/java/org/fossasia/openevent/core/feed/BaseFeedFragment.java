package org.fossasia.openevent.core.feed;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.common.ui.base.BaseFragment;

import java.util.List;

public abstract class BaseFeedFragment extends BaseFragment {
    protected ProgressDialog downloadProgressDialog;

    protected void handleVisibility() {
        if (!getFeedItems().isEmpty()) {
            getNoFeedView().setVisibility(View.GONE);
            getRecyclerView().setVisibility(View.VISIBLE);
        } else {
            getNoFeedView().setVisibility(View.VISIBLE);
            getRecyclerView().setVisibility(View.GONE);
        }
    }

    protected abstract List getFeedItems();

    protected abstract RecyclerView getRecyclerView();

    protected abstract View getNoFeedView();

}
