package org.fossasia.openevent.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.fossasia.openevent.R;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.MyClusterRenderer;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment
        implements OnMapReadyCallback {

    private static final double DESTINATION_LATITUDE = 1.3327941;
    private static final double DESTINATION_LONGITUDE = 103.7354777;
    private static final int MAXLEVEL = 3;

    private AutoCompleteTextView actv;
    private List<Microlocation> markerItems = new ArrayList<>();
    private List<String> actvItems = new ArrayList<>();
    private ClusterManager<Microlocation> clusterManager;
    private GoogleMap mMap;
    private MyClusterRenderer myClusterRenderer;
    private Marker oldMarker=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final DbSingleton dbSingleton = DbSingleton.getInstance();
        markerItems=dbSingleton.getMicrolocationsList();
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        actv = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView);
        SupportMapFragment supportMapFragment = ((SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map != null) {
            mMap = map;
            setUpMap();
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

    private void initCamera() {

        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(DESTINATION_LATITUDE, DESTINATION_LONGITUDE))
                .zoom(18f)
                .bearing(0.0f)
                .tilt(40f)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.setOnIndoorStateChangeListener(new GoogleMap.OnIndoorStateChangeListener() {
            @Override
            public void onIndoorBuildingFocused() {
                //do nothing
            }

            @Override
            public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {
                if (indoorBuilding != null) {
                    IndoorLevel level =
                            indoorBuilding.getLevels().get(indoorBuilding.getActiveLevelIndex());
                    if (level != null) {
                        int currentLevel = MAXLEVEL-indoorBuilding.getActiveLevelIndex();
                        List<Microlocation> currentLevelMarkerItems = new ArrayList<>();
                        for (Microlocation markerItem : markerItems) {
                            if (markerItem.getFloor() == currentLevel) {
                                currentLevelMarkerItems.add(markerItem);
                            }
                        }
                        if(currentLevel!=0) {
                            refreshClusterManager(currentLevelMarkerItems);
                        }

                        //Set 0 level as default level for all markers.
                        else
                           refreshClusterManager(markerItems);
                    }
                }
                else
                {
                    refreshClusterManager(markerItems);
                }
            }
        });
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        initCamera();
        clusterManager = new ClusterManager<>(getActivity(), mMap);
        myClusterRenderer=new MyClusterRenderer(getActivity(), mMap, clusterManager);
        mMap.setOnCameraChangeListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Microlocation>() {
            @Override
            public boolean onClusterItemClick(Microlocation microlocation) {
                if(oldMarker==null) {
                    oldMarker = myClusterRenderer.getMarker(microlocation);
                    oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_selected));
                }
                else {
                    oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_unselected));
                    Marker selectedMarker = myClusterRenderer.getMarker(microlocation);
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_selected));
                    oldMarker=selectedMarker;
                }

                return false;
            }
        });
        clusterManager
                .setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Microlocation>() {
                    @Override
                    public boolean onClusterClick(final Cluster<Microlocation> cluster) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                cluster.getPosition(), (float) Math.floor(mMap
                                        .getCameraPosition().zoom + 2)), 300,
                                null);

                        return true;
                    }
                });

        new Thread(){
            @Override
            public void run() {
                for (Microlocation microlocation: markerItems) {
                    actvItems.add(microlocation.getName());
                }
            }
        }.start();

        refreshClusterManager(markerItems);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, actvItems);
        actv.setAdapter(adapter);

        actv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actv.setText("");
                refreshClusterManager(markerItems);
            }
        });

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //... your stuff
                View scene = getActivity().getCurrentFocus();
                if (scene != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(scene.getWindowToken(), 0);
                }
                String s = parent.getItemAtPosition(position).toString();
                int pos=actvItems.indexOf(s);
                LatLng lng = new LatLng(markerItems.get(pos).getLatitude(),markerItems.get(pos).getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lng,
                        (float) Math.floor(mMap.getCameraPosition().zoom + 8)));
            }
        });
    }

    private void refreshClusterManager(List<Microlocation> items)
    {
        clusterManager.clearItems();
        clusterManager.addItems(items);
        clusterManager.setRenderer(myClusterRenderer);
    }

}