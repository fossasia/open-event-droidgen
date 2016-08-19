package org.fossasia.openevent.fragments;

import android.content.Intent;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.fossasia.openevent.R;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbSingleton;

public class MapsFragment extends SupportMapFragment
        implements LocationListener, OnMapReadyCallback {
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
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Event event = DbSingleton.getInstance().getEventDetails();
        float latitude = event.getLatitude();
        float longitude = event.getLongitude();
        String location_title = event.getLocationName();

        location = new LatLng(latitude, longitude);

        if (map != null) {
            map.addMarker(new MarkerOptions().position(location).title(location_title));
            map.animateCamera(CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder().target(location).zoom(15f).bearing(0).tilt(0).build()));

            mMap = map;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_map_url:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Urls.WEB_APP_URL_BASIC + Urls.MAP);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share URL"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchDirections() {
        // Build intent to start Google Maps directions

    }

    private void get_Latlng() {
        // do nothing
    }

    @Override
    public void onLocationChanged(Location locations) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 10);
        if (mMap != null) {
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // do nothing
    }

    @Override
    public void onProviderEnabled(String s) {
        // do nothing
    }

    @Override
    public void onProviderDisabled(String s) {
        // do nothing
    }
}