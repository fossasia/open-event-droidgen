package org.fossasia.openevent.fragments;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ZoomControls;

import org.fossasia.openevent.BuildConfig;
import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.ConstantStrings;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class OSMapFragment extends Fragment {

    private static double DESTINATION_LATITUDE = 0;

    private static double DESTINATION_LONGITUDE = 0;

    private static String DESTINATION_NAME = "";

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 100;

    private View rootView;

    private Snackbar snackbar;

    private SharedPreferences sharedPreferences;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_map, container,false);
        return rootView;
    }

    private void populateMap() {

        if (!mayRequestStorageForOfflineMap())
            return;

        final MapView mapView = (MapView) rootView.findViewById(R.id.mapview);
        final ZoomControls zoomControls = (ZoomControls) rootView.findViewById(R.id.zoomControls);
        Resources resources = getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            zoomControls.setPadding(0, 0, 0, resources.getDimensionPixelSize(resourceId) + 4);
        }
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        event = realmRepo.getEvent();

        event.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel realmModel) {
                showEvent();
            }
        });

        GeoPoint geoPoint = new GeoPoint(getDestinationLatitude(), getDestinationLongitude());
        mapView.getController().setCenter(geoPoint);
        mapView.getController().setZoom(17);
        mapView.setMaxZoomLevel(20);
        mapView.setMinZoomLevel(3);
        OverlayItem position = new OverlayItem(getDestinationName(), "Location", geoPoint);

        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mapView.getController().zoomIn();
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mapView.getController().zoomOut();
            }
        });

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

    private void showEvent() {
        if(event == null)
            return;

        try {
            int id = sharedPreferences.getInt(ConstantStrings.SESSION_MAP_ID,-1);

            if(id != -1){
                Session session = realmRepo.getSessionSync(id);

                Microlocation microlocation = session.getMicrolocation();
                
                if (microlocation.getLatitude() == 0 && microlocation.getLongitude() == 0) {
                    setDestinationLatitude(event.getLatitude());
                    setDestinationLongitude(event.getLongitude());
                    setDestinationName(event.getLocationName());
                } else {
                    setDestinationLatitude(microlocation.getLatitude());
                    setDestinationLongitude(microlocation.getLongitude());
                    setDestinationName(microlocation.getName());
                }
            } else{
                setDestinationLatitude(event.getLatitude());
                setDestinationLongitude(event.getLongitude());
                setDestinationName(event.getLocationName());
            }
        } catch (Exception e) {
            setDestinationLatitude(event.getLatitude());
            setDestinationLongitude(event.getLongitude());
            setDestinationName(event.getLocationName());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateMap();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), WRITE_EXTERNAL_STORAGE)) {
                        showPermissionRationaleSnackBar();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.permissions_insufficient), Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            default:
                //do nothing
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateMap();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (snackbar!=null && snackbar.isShown())
            snackbar.dismiss();

        if(event != null)
            event.removeAllChangeListeners();
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

    private boolean mayRequestStorageForOfflineMap() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            showPermissionRationaleSnackBar();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
        }

        return false;
    }

    private void showPermissionRationaleSnackBar() {
        snackbar = Snackbar.make(rootView.findViewById(R.id.map), getString(R.string.storage_permission_rationale),
                Snackbar.LENGTH_INDEFINITE).setAction(getString(android.R.string.ok).toUpperCase(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request the permission
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            }
        });
        snackbar.show();

    }
}