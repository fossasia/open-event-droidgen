package org.fossasia.openevent.core.feed;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseFragment;

import java.util.List;

public abstract class BaseFeedFragment extends BaseFragment {
    protected ProgressDialog downloadProgressDialog;

    protected void showProgressBar(boolean show) {
        if (show)
            downloadProgressDialog.show();
        else
            downloadProgressDialog.dismiss();
    }

    protected void setupProgressBar() {
        downloadProgressDialog = new ProgressDialog(getContext());
        downloadProgressDialog.setIndeterminate(true);
        downloadProgressDialog.setProgressPercentFormat(null);
        downloadProgressDialog.setProgressNumberFormat(null);
        downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadProgressDialog.setCancelable(false);
        String shownMessage = String.format(getString(R.string.downloading_format), getString(R.string.menu_feed));
        downloadProgressDialog.setMessage(shownMessage);
        downloadProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialogInterface, i) -> {
            downloadProgressDialog.dismiss();
            getActivity().onBackPressed();
        });
    }

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
