package org.fossasia.openevent.fragments;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbSingleton;

public class MapsFragment extends SupportMapFragment implements LocationListener {
    private GoogleMap mMap;
    private LatLng location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DbSingleton dbSingleton = DbSingleton.getInstance();


        Bundle latlng = getArguments();
        if (latlng != null && latlng.containsKey(Event.LOCATION)) {
            location = latlng.getParcelable(Event.LOCATION);

            String location_title = dbSingleton.getEventDetails().getLocationName();
            mMap = getMap();
            if (mMap != null) {
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(location_title));
                mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                                .target(location)
                                .zoom(15f)
                                .bearing(0)
                                .tilt(0)
                                .build()));

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private void launchDirections() {
        // Build intent to start Google Maps directions

    }

    private void get_Latlng() {

    }

    @Override
    public void onLocationChanged(Location locations) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 10);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}