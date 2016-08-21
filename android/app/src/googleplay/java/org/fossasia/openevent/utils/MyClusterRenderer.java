package org.fossasia.openevent.utils;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Microlocation;

/**
 * Created by rohanagarwal94 on 11/8/16.
 */
public class MyClusterRenderer extends DefaultClusterRenderer<Microlocation> {

    private Context context;

    public MyClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<Microlocation> clusterManager) {
        super(context, map, clusterManager);
        this.context=context;
    }

    @Override
    protected void onBeforeClusterItemRendered(Microlocation item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        markerOptions.title(item.getName());
        markerOptions.snippet(String.valueOf(context.getResources().getString(R.string.floor) + item.getFloor()));
    }

    @Override
    protected void onClusterItemRendered(final Microlocation clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }



}
