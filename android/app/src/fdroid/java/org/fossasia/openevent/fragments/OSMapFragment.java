package org.fossasia.openevent.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.BuildConfig;
import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Locale;

public class OSMapFragment extends Fragment {

    private static double DESTINATION_LATITUDE = 0;

    private static double DESTINATION_LONGITUDE = 0;

    private static String DESTINATION_NAME = "";

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 100;

    MapView mapView;
    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, null);
        Activity activity = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            } else {
                populateMap();
            }
        } else {
            populateMap();
        }
        return rootView;
    }

    private void populateMap() {
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        Event event = DbSingleton.getInstance().getEventDetails();
        setDestinationLatitude(event.getLatitude());
        setDestinationLongitude(event.getLongitude());
        setDestinationName(event.getLocationName());
        GeoPoint geoPoint = new GeoPoint(getDestinationLatitude(), getDestinationLongitude());
        mapView.getController().setCenter(geoPoint);
        mapView.getController().setZoom(17);
        OverlayItem position = new OverlayItem(getDestinationName(), "Location", geoPoint);

        ArrayList<OverlayItem> items = new ArrayList<>();
        items.add(position);

        mapView.getOverlays().add(new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        return false;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        return false;
                    }
                }, new DefaultResourceProxyImpl(getActivity())));
        mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateMap();
                    Snackbar.make(rootView.findViewById(R.id.map), "Permissions Given!", Snackbar.LENGTH_LONG)
                            .show();

                } else {
                    Snackbar.make(rootView.findViewById(R.id.map), "Insufficient Permissions!", Snackbar.LENGTH_LONG)
                            .show();
                }
                return;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.directions:
                launchDirections();
                return true;
        }*/
        return false;
    }

    private void launchDirections() {
        // Build intent to start Google Maps directions
        String uri = String.format(Locale.US,
                "https://www.google.com/maps/search/%1$s/@%2$f,%3$f,17z",
                DESTINATION_NAME, DESTINATION_LATITUDE, DESTINATION_LONGITUDE);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        startActivity(intent);
    }

    private void get_Latlng() {

    }

    public static double getDestinationLatitude() {
        return DESTINATION_LATITUDE;
    }

    public static double getDestinationLongitude() {
        return DESTINATION_LONGITUDE;
    }

    public static String getDestinationName() {
        return DESTINATION_NAME;
    }

    public static void setDestinationLatitude(double destinationLatitude) {
        DESTINATION_LATITUDE = destinationLatitude;
    }

    public static void setDestinationLongitude(double destinationLongitude) {
        DESTINATION_LONGITUDE = destinationLongitude;
    }

    public static void setDestinationName(String destinationName) {
        DESTINATION_NAME = destinationName;
    }
}