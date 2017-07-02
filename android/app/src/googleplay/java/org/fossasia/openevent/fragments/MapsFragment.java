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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

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
import org.fossasia.openevent.utils.ConstantStrings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    final private String SEARCH = "searchText";

    private GoogleMap mMap;
    private Marker locationMarker;
    private List<String> searchItems = new ArrayList<>();

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private List<Microlocation> mLocations = new ArrayList<>();
    private Map<String, Marker> stringMarkerMap = new HashMap<>();

    private String searchText = "";
    private boolean isFragmentFromMainActivity = false;
    private String fragmentLocationName;

    private SearchView searchView;
    private SearchView.SearchAutoComplete mSearchAutoComplete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            isFragmentFromMainActivity = getArguments().getBoolean(ConstantStrings.IS_MAP_FRAGMENT_FROM_MAIN_ACTIVITY);
            fragmentLocationName = getArguments().getString(ConstantStrings.LOCATION_NAME);
        }

        if (isFragmentFromMainActivity){
            setHasOptionsMenu(true);
        }

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

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

        if(event == null)
            return;

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

        if(mLocations == null || mLocations.isEmpty())
            return;

        //Add markers for all locations
        for (Microlocation microlocation : mLocations) {
            latitude = microlocation.getLatitude();
            longitude = microlocation.getLongitude();
            location = new LatLng(latitude, longitude);
            locationName = microlocation.getName();
            searchItems.add(locationName);

            marker = handleMarkerEvents(location, locationName);
            stringMarkerMap.put(locationName,marker);
            builder.include(marker.getPosition());
        }

        //Set max zoom level so that all marker are visible
        LatLngBounds bounds = builder.build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(40));
        try{
            mMap.moveCamera(cameraUpdate);
        }catch (IllegalStateException ise){
            mMap.setOnMapLoadedCallback(() -> mMap.moveCamera(cameraUpdate));
        }

        if (fragmentLocationName != null)
            focucOnMarker(stringMarkerMap.get(fragmentLocationName));

        if (searchView == null || mSearchAutoComplete == null)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, searchItems);
        mSearchAutoComplete.setAdapter(adapter);

        mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String loc = adapter.getItem(position);
            int pos = searchItems.indexOf(loc);

            focucOnMarker(stringMarkerMap.get(mLocations.get(pos).getName()));

            searchView.clearFocus();

            View mapView = getActivity().getCurrentFocus();
            if (mapView != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mapView.getWindowToken(), 0);
            }
        });
    }

    private void focucOnMarker(Marker marker){
        marker.showInfoWindow();
        marker.setIcon(vectorToBitmap(getContext(), R.drawable.map_marker, R.color.color_primary));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), (float) Math.floor(mMap.getCameraPosition().zoom + 8)));
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
    public void onSaveInstanceState(Bundle bundle) {
        if (isAdded() && searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_map, menu);
        MenuItem item = menu.findItem(R.id.action_search_map);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchAutoComplete.setDropDownBackgroundResource(R.drawable.background_white);
        mSearchAutoComplete.setDropDownAnchor(R.id.action_search_map);
        mSearchAutoComplete.setThreshold(0);
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