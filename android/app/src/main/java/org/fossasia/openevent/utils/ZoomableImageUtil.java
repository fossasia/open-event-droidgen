package org.fossasia.openevent.utils;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import org.fossasia.openevent.fragments.ZoomableImageDialogFragment;

public class ZoomableImageUtil {
    
    public static void showZoomableImageDialogFragment(FragmentManager fragmentManager, String imageUri) {
        if (!TextUtils.isEmpty(imageUri)) {
            ZoomableImageDialogFragment zoomableImageDialogFragment = new ZoomableImageDialogFragment();
            zoomableImageDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantStrings.IMAGE_ZOOM_KEY, imageUri);
            zoomableImageDialogFragment.setArguments(bundle);
            zoomableImageDialogFragment.show(fragmentManager, "ZoomableImage");
        }
    }
}
