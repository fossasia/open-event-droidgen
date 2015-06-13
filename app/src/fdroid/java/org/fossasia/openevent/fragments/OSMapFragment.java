package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;

import org.fossasia.openevent.R;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Locale;

public class OSMapFragment extends Fragment {

    private static final double DESTINATION_LATITUDE = 52.52433;

    private static final double DESTINATION_LONGITUDE = 13.389893;

    private static final String DESTINATION_NAME = "Kalkscheune Johannisstraße 2  10117 Berlin Germany";

    MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container);
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        GeoPoint geoPoint = new GeoPoint(DESTINATION_LATITUDE, DESTINATION_LONGITUDE);
        mapView.getController().setCenter(geoPoint);
        mapView.getController().setZoom(15);
        OverlayItem position = new OverlayItem(DESTINATION_NAME, "Location", geoPoint);

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

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*inflater.inflate(R.menu.map, menu);*/
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

}