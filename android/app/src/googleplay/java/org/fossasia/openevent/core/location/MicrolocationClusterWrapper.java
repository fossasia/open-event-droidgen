package org.fossasia.openevent.core.location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

import org.fossasia.openevent.data.Microlocation;

public class MicrolocationClusterWrapper implements ClusterItem {

    private LatLng latLng;
    private Marker marker;
    private Microlocation microlocation;

    public MicrolocationClusterWrapper(Microlocation microlocation) {
        this.microlocation = microlocation;
        this.latLng = new LatLng(microlocation.getLatitude(), microlocation.getLongitude());
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    public Microlocation getMicrolocation() {
        return microlocation;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }
}
