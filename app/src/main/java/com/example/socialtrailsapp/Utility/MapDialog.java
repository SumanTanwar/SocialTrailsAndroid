package com.example.socialtrailsapp.Utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.socialtrailsapp.Interface.ILocationSetter;
import com.example.socialtrailsapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapDialog extends Dialog implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText searchBox;
    private ListView autocompleteList;
    private PlacesClient placesClient;
    private ArrayAdapter<String> adapter;
    private List<String> predictionsList = new ArrayList<>();
    private List<String> placeIdsList = new ArrayList<>();
    private boolean shouldProcessTextChange = true;
    private Marker currentMarker;
    private FragmentActivity fragmentActivity;

    private LatLng selectedLatLng;
    private String selectedAddress;
    private boolean isMapReady = false; // Track if the map is ready

    public MapDialog(@NonNull FragmentActivity activity) {
        super(activity);
        this.fragmentActivity = activity;
    }

    public MapDialog(@NonNull FragmentActivity activity, LatLng existingLatLng, String existingAddress) {
        super(activity);
        this.fragmentActivity = activity;
        this.selectedLatLng = existingLatLng;
        this.selectedAddress = existingAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_map);

        initializePlacesClient();
        setupUIElements();
        setupMapFragment();
        setupSearchBox();

        if (isMapReady && selectedLatLng != null && selectedAddress != null) {
            searchBox.setText(selectedAddress);
            updateMapWithMarker(selectedLatLng, selectedAddress);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));
        }
    }

    private void initializePlacesClient() {
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), "AIzaSyBdmLSrq0OuQob_ZvkV6zh9sVS2FmnYo4o"); // Replace with your actual API Key
        }
        placesClient = Places.createClient(getContext());
    }

    private void setupUIElements() {
        searchBox = findViewById(R.id.searchBox);
        autocompleteList = findViewById(R.id.autocompleteList);
        Button confirmButton = findViewById(R.id.confirmButton);
        ImageView cancelButton = findViewById(R.id.cancelButton);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, predictionsList);
        autocompleteList.setAdapter(adapter);

        confirmButton.setOnClickListener(v -> {
            if (selectedLatLng != null && selectedAddress != null) {
                ((ILocationSetter) fragmentActivity).setLocation(selectedLatLng, selectedAddress);
                dismiss(); // Close the dialog
            }
        });

        cancelButton.setOnClickListener(v -> dismiss()); // Close the dialog
    }

    private void setupMapFragment() {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.map, mapFragment)
                    .commitNow(); // Immediately commit the transaction
        }
        mapFragment.getMapAsync(this);
    }

    private void resetMap() {
        if (mMap != null) {
            mMap.clear(); // Clear existing markers
            selectedLatLng = null;
            selectedAddress = null;
            searchBox.setText(""); // Clear the search box
            autocompleteList.setVisibility(View.GONE); // Hide autocomplete
        }
    }

    @Override
    public void dismiss() {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment != null) {
            fragmentManager.beginTransaction().remove(mapFragment).commit();
        }
        super.dismiss();
    }

    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (shouldProcessTextChange) {
                    handleSearchQuery(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        autocompleteList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = adapter.getItem(position);
            String selectedPlaceId = placeIdsList.get(position);
            if (selectedItem != null) {
                shouldProcessTextChange = false;
                searchBox.setText(selectedItem);
                fetchPlaceDetails(selectedPlaceId);
                autocompleteList.setVisibility(View.GONE); // Hide autocomplete after selection
            }
        });
    }

    private void handleSearchQuery(String query) {
        if (!query.isEmpty()) {
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .build();

            placesClient.findAutocompletePredictions(request)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            predictionsList.clear();
                            placeIdsList.clear();

                            for (AutocompletePrediction prediction : task.getResult().getAutocompletePredictions()) {
                                predictionsList.add(prediction.getFullText(null).toString());
                                placeIdsList.add(prediction.getPlaceId());
                            }
                            adapter.notifyDataSetChanged();
                            autocompleteList.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            predictionsList.clear();
            placeIdsList.clear();
            adapter.notifyDataSetChanged();
            autocompleteList.setVisibility(View.GONE);
        }
    }

    private void fetchPlaceDetails(String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request)
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    selectedLatLng = place.getLatLng(); // Store the selected location
                    selectedAddress = place.getFormattedAddress(); // Store the selected address

                    if (selectedLatLng != null) {
                        updateMapWithMarker(selectedLatLng, selectedAddress);
                    }
                })
                .addOnFailureListener(exception -> {
                    exception.printStackTrace();
                })
                .addOnCompleteListener(task -> {
                    shouldProcessTextChange = true;
                });
    }

    private void updateMapWithMarker(LatLng latLng, String address) {
        if (mMap != null) {
            if (currentMarker != null) {
                currentMarker.remove();
            }
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            Log.e("MapDialog", "Map is not ready yet");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true; // Set map as ready

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;
            fetchPlaceFromLatLng(latLng);
        });

        // If existing location is set, update the map
        if (selectedLatLng != null && selectedAddress != null) {
            updateMapWithMarker(selectedLatLng, selectedAddress);
            searchBox.setText(selectedAddress);
            autocompleteList.setVisibility(View.GONE);
        }
    }

    private void fetchPlaceFromLatLng(LatLng latLng) {
        new Thread(() -> {
            try {
                String apiKey = "AIzaSyBdmLSrq0OuQob_ZvkV6zh9sVS2FmnYo4o"; // Replace with your actual API Key
                String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&key=" + apiKey;

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(jsonResponse.toString());
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    selectedAddress = results.getJSONObject(0).getString("formatted_address");
                    fragmentActivity.runOnUiThread(() -> {
                        searchBox.setText(selectedAddress);
                        updateMapWithMarker(latLng, selectedAddress);
                        autocompleteList.setVisibility(View.GONE); // Hide autocomplete after selecting from the map
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
