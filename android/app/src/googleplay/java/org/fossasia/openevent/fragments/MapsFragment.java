package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.content.res.Resources;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.fossasia.openevent.R;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class MapsFragment extends SupportMapFragment
        implements LocationListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng location;

    private List<Microlocation> mLocations = new ArrayList<>();
    private CompositeDisposable compositeDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);

        compositeDisposable = new CompositeDisposable();
        final DbSingleton dbSingleton = DbSingleton.getInstance();

        compositeDisposable.add(dbSingleton.getMicrolocationListObservable()
                .subscribe(new Consumer<ArrayList<Microlocation>>() {
                    @Override
                    public void accept(@NonNull ArrayList<Microlocation> microlocations) throws Exception {
                        mLocations.clear();
                        mLocations.addAll(microlocations);
                        if(mMap != null) {
                            showLocationsOnMap();
                        }
                    }
                }));
    }

    @Override
    public void onMapReady(GoogleMap map) {

        if(map != null){
            mMap = map;
            mMap.getUiSettings().setMapToolbarEnabled(true);
        }

        showEventLocationOnMap();
    }

    private void showEventLocationOnMap(){

        Event event = DbSingleton.getInstance().getEventDetails();
        float latitude = event.getLatitude();
        float longitude = event.getLongitude();
        String location_title = event.getLocationName();

        location = new LatLng(latitude, longitude);

        if (mMap != null) {
            //Add marker for event location
            mMap.addMarker(new MarkerOptions().position(location).title(location_title));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder().target(location).zoom(15f).bearing(0).tilt(0).build()));
        }
    }

    private void showLocationsOnMap(){
        float latitude;
        float longitude;
        Marker marker;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //Add markers for all locations
        for (Microlocation microlocation : mLocations) {
            latitude = microlocation.getLatitude();
            longitude = microlocation.getLongitude();
            location = new LatLng(latitude, longitude);

            marker = mMap.addMarker(new MarkerOptions().position(location).title(microlocation.getName()));
            builder.include(marker.getPosition());
        }

        //Set max zoom level so that all marker are visible
        LatLngBounds bounds = builder.build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(40));
        try{
            mMap.moveCamera(cameraUpdate);
        }catch (IllegalStateException ise){
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(cameraUpdate);
                }
            });
        }

    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
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
            default:
            	//do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
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