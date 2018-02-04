package org.fossasia.openevent.common.ui.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.config.StrategyRegistry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ZoomableImageDialogFragment extends DialogFragment {

    private Unbinder unbinder;

    @BindView(R.id.image_view_zoomable)
    PhotoView photoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zoomable_image, container, false);
        unbinder = ButterKnife.bind(this, view);
        String imageUri = this.getArguments().getString(ConstantStrings.IMAGE_ZOOM_KEY);
        Drawable placeholder = VectorDrawableCompat.create(getActivity().getResources(),
                R.drawable.ic_placeholder_24dp,null);
        if (imageUri != null) {
            Picasso.with(getContext())
                    .load(Uri.parse(imageUri))
                    .placeholder(placeholder)
                    .into(photoView);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        StrategyRegistry.getInstance().getLeakCanaryStrategy().getRefWatcher().watch(this);
    }
}
