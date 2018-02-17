package org.fossasia.openevent.core.feed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseFragment;
import org.fossasia.openevent.common.ui.image.OnImageZoomListener;
import org.fossasia.openevent.core.feed.facebook.FacebookFeedFragment;
import org.fossasia.openevent.core.feed.twitter.TwitterFeedFragment;

import butterknife.BindView;

public class FeedFragment extends BaseFragment {

    @BindView(R.id.viewpager)
    protected ViewPager viewPager;
    @BindView(R.id.tabLayout)
    protected TabLayout feedTabLayout;

    private OpenCommentsDialogListener openCommentsDialogListener;
    private OnImageZoomListener onImageZoomListener;

    public static FeedFragment getInstance(OpenCommentsDialogListener openCommentsDialogListener,
                                           OnImageZoomListener onImageZoomListener) {
        FeedFragment feedFragment = new FeedFragment();
        feedFragment.openCommentsDialogListener = openCommentsDialogListener;
        feedFragment.onImageZoomListener = onImageZoomListener;

        return feedFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setUpViewPager(viewPager);
        feedTabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setUpViewPager(ViewPager viewPager) {
        FeedViewPagerAdapter feedViewPagerAdapter = new FeedViewPagerAdapter(getChildFragmentManager());
        feedViewPagerAdapter.addFragment(FacebookFeedFragment.getInstance(openCommentsDialogListener, onImageZoomListener), getString(R.string.facebook));
        feedViewPagerAdapter.addFragment(TwitterFeedFragment.getInstance(onImageZoomListener), getString(R.string.twitter));
        feedViewPagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(feedViewPagerAdapter);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_feed_main;
    }

}
