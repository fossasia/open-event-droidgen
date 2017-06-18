package org.fossasia.openevent.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker locationMarker;
    private List<String> searchItems = new ArrayList<>();

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private List<Microlocation> mLocations = new ArrayList<>();

    private android.support.v7.app.ActionBar toolbar;

    @BindView(R.id.map_toolbar)AutoCompleteTextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(!Utils.getTwoPane()) {
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = ((SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);

        realmRepo.getLocations()
                .addChangeListener((microlocations, orderedCollectionChangeSet) -> {
                    mLocations.clear();
                    mLocations.addAll(microlocations);
                });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if(map != null){
            mMap = map;
            mMap.getUiSettings().setMapToolbarEnabled(true);
            showLocationsOnMap();
            showEventLocationOnMap();
        }
    }

    private void showEventLocationOnMap() {
        Event event = realmRepo.getEventSync();

        double latitude = event.getLatitude();
        double longitude = event.getLongitude();
        
        String locationTitle = event.getLocationName();

        LatLng location = new LatLng(latitude, longitude);
        handleMarkerEvents(location, locationTitle);
    }

    private void showLocationsOnMap() {
        float latitude;
        float longitude;
        LatLng location;
        Marker marker;
        String locationName;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //Add markers for all locations
        for (Microlocation microlocation : mLocations) {
            latitude = microlocation.getLatitude();
            longitude = microlocation.getLongitude();
            location = new LatLng(latitude, longitude);
            locationName = microlocation.getName();
            searchItems.add(locationName);

            marker = handleMarkerEvents(location, locationName);
            builder.include(marker.getPosition());
        }

        LayoutInflater inflator = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toolbarView = inflator.inflate(R.layout.map_toolbar, null);
        ButterKnife.bind(this, toolbarView);

        toolbar.setCustomView(toolbarView );

        //Setting up AutoCompleteTextView with the locations
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, searchItems);
        textView.setAdapter(adapter);

        textView.setOnItemClickListener((parent, view, position, id) -> {
            String loc = adapter.getItem(position);
            int pos = searchItems.indexOf(loc);
            LatLng lng = new LatLng(mLocations.get(pos).getLatitude(), mLocations.get(pos).getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lng, (float) Math.floor(mMap.getCameraPosition().zoom + 8)));

            View mapView = getActivity().getCurrentFocus();
            if (mapView != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mapView.getWindowToken(), 0);
            }
        });

        //Set max zoom level so that all marker are visible
        LatLngBounds bounds = builder.build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(40));
        try{
            mMap.moveCamera(cameraUpdate);
        }catch (IllegalStateException ise){
            mMap.setOnMapLoadedCallback(() -> mMap.moveCamera(cameraUpdate));
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private Marker handleMarkerEvents(LatLng location, String locationTitle) {
        if (mMap != null) {
            locationMarker = mMap.addMarker(new MarkerOptions().position(location).title(locationTitle)
                    .icon(vectorToBitmap(getContext(), R.drawable.map_marker, R.color.dark_grey)));
            mMap.setOnMarkerClickListener(marker -> {
                locationMarker.setIcon(vectorToBitmap(getContext(), R.drawable.map_marker, R.color.dark_grey));
                locationMarker = marker;
                marker.setIcon(vectorToBitmap(getContext(), R.drawable.map_marker, R.color.color_primary));
                return false;
            });
            mMap.setOnMapClickListener(latLng -> locationMarker.setIcon(
                    vectorToBitmap(getContext(), R.drawable.map_marker, R.color.dark_grey)));
        }
        return locationMarker;
    }

    private BitmapDescriptor vectorToBitmap(Context context, @DrawableRes int id, int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, ContextCompat.getColor(context, color));
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onDestroyView() {
        toolbar.setDisplayShowCustomEnabled(false);
        super.onDestroyView();
    }

    @Override
    public void onLocationChanged(Location location) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom
                (new LatLng(location.getLatitude(),location.getLongitude()), 10);
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