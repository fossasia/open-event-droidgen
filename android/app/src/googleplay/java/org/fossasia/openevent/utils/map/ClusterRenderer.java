package org.fossasia.openevent.utils.map;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.fossasia.openevent.R;

/**
 * Created by rohanagarwal94 on 4/7/17.
 */
public class ClusterRenderer extends DefaultClusterRenderer<MicrolocationClusterWrapper> implements ClusterManager.OnClusterItemClickListener<MicrolocationClusterWrapper>, GoogleMap.OnMapClickListener {

    private Context context;
    private GoogleMap map;
    private MicrolocationClusterWrapper microlocationClusterWrapper;

    public ClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<MicrolocationClusterWrapper> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        this.map = map;
        setOnClusterItemClickListener(this);
    }

    @Override
    protected void onBeforeClusterItemRendered(MicrolocationClusterWrapper item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        markerOptions.title(item.getMicrolocation().getName());
        if (microlocationClusterWrapper != null && item.equals(microlocationClusterWrapper)) {
            markerOptions.icon(ImageUtils.vectorToBitmap(context, R.drawable.map_marker, R.color.color_primary));
        } else {
            markerOptions.icon(ImageUtils.vectorToBitmap(context, R.drawable.map_marker, R.color.dark_grey));
        }
    }

    @Override
    protected void onClusterItemRendered(final MicrolocationClusterWrapper clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        clusterItem.setMarker(marker);
   }

    public void detach() {
        map = null;
        context = null;
        setOnClusterItemClickListener(null);
   }

    @Override
    public boolean onClusterItemClick(MicrolocationClusterWrapper item) {
        if (microlocationClusterWrapper != null) {
            getMarker(microlocationClusterWrapper).setIcon(ImageUtils.vectorToBitmap(context, R.drawable.map_marker, R.color.dark_grey));
        }
        microlocationClusterWrapper = item;
        getMarker(item).setIcon(ImageUtils.vectorToBitmap(context, R.drawable.map_marker, R.color.color_primary));
        return false;
    }

    public void focusOnMarkers(MicrolocationClusterWrapper item) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(item.getPosition(), map.getMaxZoomLevel()), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    Marker marker = item.getMarker();
                    if (microlocationClusterWrapper != null)
                        getMarker(microlocationClusterWrapper).setIcon(ImageUtils.vectorToBitmap(context, R.drawable.map_marker, R.color.dark_grey));
                    if(marker != null) {
                        microlocationClusterWrapper = item;
                        marker.showInfoWindow();
                        marker.setIcon(ImageUtils.vectorToBitmap(context, R.drawable.map_marker, R.color.color_primary));
                    }
                }, 100);
            }

            @Override
            public void onCancel() {
                // do nothing
            }
        });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        getMarker(microlocationClusterWrapper).setIcon(
                ImageUtils.vectorToBitmap(context, R.drawable.map_marker, R.color.dark_grey));
    }
}
