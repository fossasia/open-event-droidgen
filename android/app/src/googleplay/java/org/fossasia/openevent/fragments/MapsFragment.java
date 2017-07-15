package org.fossasia.openevent.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.map.ClusterRenderer;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.map.ImageUtils;
import org.fossasia.openevent.utils.map.MicrolocationClusterWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    final private String SEARCH = "searchText";

    private GoogleMap mMap;
    private List<String> searchItems = new ArrayList<>();

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private List<MicrolocationClusterWrapper> mLocations = new ArrayList<>();
    private ClusterRenderer clusterRenderer;
    private ClusterManager<MicrolocationClusterWrapper> clusterManager;
    private Map<String, MicrolocationClusterWrapper> stringMicrolocationClusterWrapperMap = new HashMap<>();

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
                    for(Microlocation microlocation : microlocations) {
                        mLocations.add(new MicrolocationClusterWrapper(microlocation));
                    }
                });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if(map != null) {
            mMap = map;
            mMap.getUiSettings().setMapToolbarEnabled(true);
            clusterManager = new ClusterManager<>(getActivity(), mMap);
            clusterRenderer = new ClusterRenderer(getActivity(), mMap, clusterManager);
            mMap.setOnCameraChangeListener(clusterManager);
            mMap.setOnMarkerClickListener(clusterManager);
            refreshClusterManager(mLocations);
            handleClusterEvents();
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
        mMap.addMarker(new MarkerOptions().position(location).title(locationTitle)
                    .icon(ImageUtils.vectorToBitmap(getContext(), R.drawable.map_marker, R.color.color_primary_dark)));
    }

    private void showLocationsOnMap() {
        String locationName;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if(mLocations == null || mLocations.isEmpty())
            return;

        //Add search names for all locations
        for (MicrolocationClusterWrapper microlocation : mLocations) {
            locationName = microlocation.getMicrolocation().getName();
            searchItems.add(locationName);
            stringMicrolocationClusterWrapperMap.put(locationName, microlocation);
            builder.include(microlocation.getPosition());
        }

        //Set max zoom level so that all marker are visible
        LatLngBounds bounds = builder.build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, dpToPx(40));
        try {
            mMap.moveCamera(cameraUpdate);
        } catch (IllegalStateException ise){
            mMap.setOnMapLoadedCallback(() -> mMap.moveCamera(cameraUpdate));
        }

        if (fragmentLocationName != null)
            clusterRenderer.focusOnMarkers(stringMicrolocationClusterWrapperMap.get(fragmentLocationName));

        if (searchView == null || mSearchAutoComplete == null)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, searchItems);
        mSearchAutoComplete.setAdapter(adapter);

        mSearchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String loc = adapter.getItem(position);

            clusterRenderer.focusOnMarkers(stringMicrolocationClusterWrapperMap.get(loc));

            searchView.clearFocus();

            View mapView = getActivity().getCurrentFocus();
            if (mapView != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mapView.getWindowToken(), 0);
            }
        });
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void handleClusterEvents() {
        clusterManager.setOnClusterItemClickListener(clusterRenderer);

        clusterManager.setOnClusterClickListener(cluster -> {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            cluster.getPosition(), (float) Math.floor(mMap
                                    .getCameraPosition().zoom + 2)), 300,
                            null);

                    return true;
                });

        mMap.setOnMapClickListener(clusterRenderer);

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (isAdded() && searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clusterRenderer.detach();
    }

    @Override
    public void onLocationChanged(Location location) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom
                (new LatLng(location.getLatitude(),location.getLongitude()), 10);
        if (mMap != null) {
            mMap.animateCamera(cameraUpdate);
        }
    }

    private void refreshClusterManager(List<MicrolocationClusterWrapper> items) {
        clusterManager.clearItems();
        clusterManager.addItems(items);
        clusterManager.setRenderer(clusterRenderer);
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
