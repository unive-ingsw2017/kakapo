package com.heliohost.kakapo.datitalia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Panda Team.
 */
public class MapManager implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private static final String TAG = "MapManager";

    private SupportMapFragment fragment;
    private Context context;
    private GoogleMap mMap;
    private Map<Marker, Object> markers;
    private Map<Object, Marker> markersById;
    private Map<Object, MarkerOptions> markersToAdd;
    private MapListener listener;

    MapManager(SupportMapFragment fragment, Context context) {
        this.fragment = fragment;
        this.context = context;
        MapsInitializer.initialize(context);
        markers = new HashMap<>();
        markersById = new HashMap<>();
        markersToAdd = new HashMap<>();
        listener = null;
    }

    void startLoadMap() {
        fragment.getMapAsync(this);
    }

    private MarkerOptions buildMarkerOptions(LatLng latLng, String title) {
        return new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private MarkerOptions buildMarkerOptions(LatLng latLng, String title, String snippet) {
        return new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private MarkerOptions buildMarkerOptions(LatLng latLng, String title, BitmapDescriptor icon) {
        return new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(icon);
    }

    private MarkerOptions buildMarkerOptions(LatLng latLng, String title, String snippet, BitmapDescriptor icon) {
        return new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(icon);
    }

    /**
     * If map is loaded, it adds the marker directly to the map
     * If map isn't loaded, it adds the marker to the list of will-be-added markers
     *
     * @param id            internal id to retrieve correct marker from outside
     * @param markerOptions markerOptions to create the marker
     */
    void insertMarker(Object id, MarkerOptions markerOptions) {
        if (mMap != null) {
            Log.d(TAG, "Really inserting a marker");

            Marker marker = mMap.addMarker(markerOptions);
            markers.put(marker, id);
            markersById.put(id, marker);
        } else {
            Log.d(TAG, "Not really inserting a marker");
            markersToAdd.put(id, markerOptions);
        }
    }

    void insertMarker(Object id, LatLng latLng, String title) {
        insertMarker(id, buildMarkerOptions(latLng, title));
    }

    void insertMarker(Object id, double latitude, double longitude, String title) {
        insertMarker(id, new LatLng(latitude, longitude), title);
    }

    void insertMarker(Object id, LatLng latLng, String title, String snippet) {
        insertMarker(id, buildMarkerOptions(latLng, title, snippet));
    }

    void insertMarker(Object id, double latitude, double longitude, String title, String snippet) {
        insertMarker(id, new LatLng(latitude, longitude), title, snippet);
    }

    void insertMarker(Object id, LatLng latLng, String title, BitmapDescriptor icon) {
        insertMarker(id, buildMarkerOptions(latLng, title, icon));
    }

    void insertMarker(Object id, double latitude, double longitude, String title, BitmapDescriptor icon) {
        insertMarker(id, new LatLng(latitude, longitude), title, icon);
    }

    void insertMarker(Object id, LatLng latLng, String title, String snippet, BitmapDescriptor icon) {
        insertMarker(id, buildMarkerOptions(latLng, title, snippet, icon));
    }

    void insertMarker(Object id, double latitude, double longitude, String title, String snippet, BitmapDescriptor icon) {
        insertMarker(id, new LatLng(latitude, longitude), title, snippet, icon);
    }

    void removeMarker(Object id) {
        if (markersById.containsKey(id)) {
            Marker marker = markersById.get(id);
            markersById.remove(id);
            markers.remove(marker);
            marker.remove();
        }
        if (markersToAdd.containsKey(id)) {
            markersToAdd.remove(id);
        }
    }

    void clearMarkers() {
        if (mMap != null)
            mMap.clear();
    }

    void setOnInfoWindowClickListener(MapListener listener) {
        this.listener = listener;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: !!!");
        mMap = googleMap;
        mMap.setMyLocationEnabled(false);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Object key = markers.get(marker);
                if (listener != null) {
                    listener.onInfoWindowClick(marker, key);
                }
            }
        });
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.478344, 12.255008), 6f));
        for (Map.Entry<Object, MarkerOptions> marker : markersToAdd.entrySet()) {
            insertMarker(marker.getKey(), marker.getValue());
        }
        markersToAdd.clear();
    }

    void onDestroy() {
        mMap.setOnInfoWindowClickListener(null);
        mMap.clear();
        markers.clear();
        markersById.clear();
        markersToAdd.clear();
        fragment = null;
        context = null;
        mMap = null;
        markers = null;
        markersById = null;
        markersToAdd = null;
        listener = null;
    }

    public SupportMapFragment getSupportMapFragment() {
        return this.fragment;
    }

    public interface MapListener {
        void onInfoWindowClick(Marker marker, Object key);
    }


}