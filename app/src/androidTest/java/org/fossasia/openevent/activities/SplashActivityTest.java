package org.fossasia.openevent.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import org.fossasia.openevent.R;

/**
 * Created by Ronin on 2016-01-08.
 */
public class SplashActivityTest extends ActivityInstrumentationTestCase2<SplashActivity> {

    private SplashActivity mActivity;
    private ImageView imageView;
    private View rootView;

    public SplashActivityTest() {
        super(SplashActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();
        imageView = (ImageView) mActivity.findViewById(R.id.logo_image);
        rootView = mActivity.getWindow().getDecorView().getRootView();
    }

    @MediumTest
    @UiThreadTest
    public void testImageView_layout()
    {
        assertNotNull(imageView);

        final ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);

        ViewAsserts.assertHorizontalCenterAligned(rootView, imageView);
        ViewAsserts.assertHorizontalCenterAligned(rootView,imageView);
    }
}