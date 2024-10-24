package com.example.socialtrailsapp.Utility;


import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.socialtrailsapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapLocationPinDialog extends Dialog implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private String selectedAddress;
    private Marker currentMarker;
    private FragmentActivity fragmentActivity;

    public MapLocationPinDialog(@NonNull FragmentActivity activity, LatLng latLng, String address) {
        super(activity);
        this.fragmentActivity = activity;
        this.selectedLatLng = latLng;
        this.selectedAddress = address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_maplocationpin);

        setupMapFragment();
        ImageView cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void setupMapFragment() {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.map, mapFragment)
                    .commitNow();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (selectedLatLng != null) {
            updateMapWithMarker(selectedLatLng, selectedAddress);
        }
    }

    private void updateMapWithMarker(LatLng latLng, String address) {
        if (mMap != null) {
            if (currentMarker != null) {
                currentMarker.remove();
            }
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
