package com.example.socialtrailsapp.Interface;

import com.google.android.gms.maps.model.LatLng;

public interface ILocationSetter {
    void setLocation(LatLng latLng, String address);
}
